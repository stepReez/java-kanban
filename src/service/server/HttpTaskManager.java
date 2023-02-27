package service.server;

import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
import model.Task;
import service.FileBackedTasksManager;
import service.HistoryManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private KVTaskClient kvTaskClient;
    private static Gson gson;
    public HttpTaskServer httpTaskServer;

    public HttpTaskManager(String url){
        try {
            httpTaskServer = new HttpTaskServer();;
            httpTaskServer.start();
            kvTaskClient = new KVTaskClient(url);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            System.out.println(123);
        }
        gson = new Gson();
    }

    @Override
    public void createTask(Task task) {
        httpTaskServer.fm.createTask(task);
        String jsonTask = gson.toJson(task);
        String key = "Task=" + task.getId();
        save(jsonTask, key);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        httpTaskServer.fm.createSubtask(subtask);
        String jsonSubtask = gson.toJson(subtask);
        String key = "Subtask=" + subtask.getId();
        save(jsonSubtask, key);
    }

    @Override
    public void createEpic(Epic epic) {
        httpTaskServer.fm.createEpic(epic);
        String jsonEpic = gson.toJson(epic);
        String key = "Epic=" + epic.getId();
        save(jsonEpic, key);
    }

    @Override
    public void updateTask(Task task, int id) {
        httpTaskServer.fm.updateTask(task, id);
        String key = "Task=" + id;
        save(gson.toJson(task), key);
    }

    @Override
    public void updateSubtask(Subtask subtask, int id) {
        httpTaskServer.fm.updateSubtask(subtask, id);
        String key = "Subtask=" + id;
        save(gson.toJson(subtask), key);
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        httpTaskServer.fm.updateEpic(epic, id);
        String key = "Epic=" + id;
        save(gson.toJson(epic), key);
    }

    @Override
    public void deleteTask(int id) {
        httpTaskServer.fm.deleteTask(id);
        String key = "Task=" + id;
        save("", key);
    }

    @Override
    public void deleteEpic(int id) {
        httpTaskServer.fm.deleteEpic(id);
        String key = "Epic=" + id;
        save("", key);
    }

    @Override
    public void deleteSubtask(int id) {
        httpTaskServer.fm.deleteSubtask(id);
        String key = "Subtask=" + id;
        save("", key);
    }

    @Override
    public Task getTask(int taskId) {
        Task task = httpTaskServer.fm.getTask(taskId);
        saveHistory();
        return task;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = httpTaskServer.fm.getEpic(epicId);
        saveHistory();
        return epic;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subtask = httpTaskServer.fm.getSubtask(subtaskId);
        saveHistory();
        return subtask;
    }

    /*
    Метод сохраняет задачи по ключам 'Task', 'Epic', 'Subtask'
     */
    public void save(String task, String id) {
        try {
            kvTaskClient.put(id, task);
            saveHistory();
            String keys = kvTaskClient.load("keys");
            keys = keys + "," + id;
            kvTaskClient.put("keys", keys);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
    Метод сохраняет историю просмотра задач в виде списка ID задач по ключу "history"
     */
    private void saveHistory() {
        try {
            kvTaskClient.put("history", gson.toJson(historyToString(httpTaskServer.fm.historyManager)));
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
    Метод считывает все задачи и историю с сервера и записывает их в память
     */
    @Override
    public void readSave() {
        try {
            String[] keys = kvTaskClient.load("keys").split(",");
            for (String key : keys) {
                String[] keyType = key.split("=");
                String jsonSave = kvTaskClient.load(key);
                switch (keyType[0]) {
                    case "Task":
                        createTask(gson.fromJson(jsonSave, Task.class));
                        break;
                    case "Epic":
                        createEpic(gson.fromJson(jsonSave, Epic.class));
                        break;
                    case "Subtask":
                        Subtask subtask = gson.fromJson(jsonSave, Subtask.class);
                        createSubtask(subtask);
                        break;
                }
            }

            String[] historySave = kvTaskClient.load("history").split(",");
            for (String history : historySave) {
                int id;
                try {
                    id = Integer.parseInt(history);
                } catch (NumberFormatException e) {
                    id = -1;
                }

                if (getTasksHashMap().containsKey(id)) {
                    httpTaskServer.fm.historyManager.addTask(getTasksHashMap().get(id));
                } else if (getEpicHashMap().containsKey(id)) {
                    httpTaskServer.fm.historyManager.addTask(getEpicHashMap().get(id));
                } else if (getSubtaskHashMap().containsKey(id)) {
                    httpTaskServer.fm.historyManager.addTask(getSubtaskHashMap().get(id));
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

    }

    public ArrayList<Task> getHistory() {
        return httpTaskServer.fm.historyManager.getHistory();
    }

    public void stop() {
        httpTaskServer.stop();
    }
}
