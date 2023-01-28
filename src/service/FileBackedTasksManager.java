package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static model.TaskStatus.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public void save() {
        try (FileWriter fw = new FileWriter("src/resources/save.csv")) {
            fw.write("id,type,name,status,description,epic\r\n");

            for(int i = 0; i < getTaskIdCounter(); i++) {

                if (getTasksHashMap().containsKey(i)) {
                    fw.write(toString(getTasksHashMap().get(i)));

                } else if (getEpicHashMap().containsKey(i)) {
                    fw.write(toString(getEpicHashMap().get(i)));

                } else if (getSubtaskHashMap().containsKey(i)) {
                    fw.write(toString(getSubtaskHashMap().get(i)));
                } else {
                    throw new ManagerSaveException("Ошибка сохранения");
                }
            }

            fw.write("\r\n");
            fw.write(historyToString(historyManager));

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void readSave() {
        try {
            String history = Files.readString(Path.of("src/resources/save.csv"));
            if (history.length() > 1) {
                String[] historyLines = history.split(System.lineSeparator());

                for (int i = 1; i < historyLines.length - 2; i++) {
                    fromString(historyLines[i]);
                }
                List<Integer> historyList = historyFromString(historyLines[historyLines.length - 1]);

                for (Integer id : historyList) {
                    if (getTasksHashMap().containsKey(id)) {
                        getTask(id);

                    } else if (getEpicHashMap().containsKey(id)) {
                        getEpic(id);

                    } else if (getSubtaskHashMap().containsKey(id)) {
                        getSubtask(id);
                    }
                }
            } else {
                throw new ManagerSaveException("Файл сохранения пустой");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%n", task.getId(), task.getType(),
                task.getName(), task.getStatus(), task.getDescription());
    }

    public String toString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,%n", epic.getId(), epic.getType(),
                epic.getName(), epic.getStatus(), epic.getDescription());
    }

    public String toString(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d%n", subtask.getId(), subtask.getType(),
                subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
    }

    private void fromString(String value) {
        String[] csvTask = value.split(",");

        String name = csvTask[2];
        String description = csvTask[4];
        TaskStatus taskStatus = null;

        switch (csvTask[3]) {
            case "NEW" :
                taskStatus = NEW;
                break;
            case "IN_PROGRESS" :
                taskStatus = IN_PROGRESS;
                break;
            case "DONE" :
                taskStatus = DONE;
                break;
        }

        switch (csvTask[1]) {
            case "TASK" :
                createTask(new Task(name, description, taskStatus));
                break;
            case "EPIC" :
                createEpic(new Epic(name, description));
                break;
            case "SUBTASK" :
                createSubtask(new Subtask(name, description, taskStatus), Integer.parseInt(csvTask[5]));
                break;

        }
    }

    public static String historyToString(HistoryManager manager) {
        ArrayList<Task> history = manager.getHistory();
        StringBuilder sb = new StringBuilder("");
        for (Task task : history) {
            sb.append(task.getId());
            sb.append(",");
        }
        return sb.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        String[] historyId = value.split(",");
        for (String id : historyId) {
            history.add(Integer.parseInt(id));
        }
        return history;
    }



    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        super.createSubtask(subtask, epicId);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task, int id) {
        super.updateTask(task, id);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, int id) {
        super.updateSubtask(subtask, id);
        save();
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        super.updateEpic(epic, id);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public Task getTask(int taskId) {
        Task task = super.getTask(taskId);
        save();
        return (task);
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = super.getEpic(epicId);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subtask = super.getSubtask(subtaskId);
        save();
        return subtask;
    }
}
