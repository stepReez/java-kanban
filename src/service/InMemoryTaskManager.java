package service;

import model.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager{

    HistoryManager historyManager = Managers.getDefaultHistory();

    private int taskIdCounter = 0;
    private HashMap<Integer, Task> tasksHashMap = new HashMap<>();
    private HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private TreeSet<Task> prioritizedTasks;

    public int getTaskIdCounter() {
        return taskIdCounter;
    }

    @Override
    public HashMap<Integer, Task> getTasksHashMap() {
        return tasksHashMap;
    }

    @Override
    public HashMap<Integer, Epic> getEpicHashMap() {
        return epicHashMap;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtaskHashMap() {
        return subtaskHashMap;
    }

    public InMemoryTaskManager() {
        Comparator<Task> comparator = (task1, task2) -> {

            if (task1.getStartTime().isAfter(task2.getStartTime())) {
                return 1;

            } else if (task1.getStartTime().isBefore(task2.getStartTime())) {
                return -1;

            } else {
                return 0;
            }
        };

        prioritizedTasks = new TreeSet<>(comparator);
    }

    @Override
    public void clearTasks() {
        tasksHashMap.clear();
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    @Override
    public Task getTask(int taskId) {
        historyManager.addTask(tasksHashMap.get(taskId));
        return tasksHashMap.get(taskId);
    }

    @Override
    public Epic getEpic(int epicId) {
        historyManager.addTask(epicHashMap.get(epicId));
        return epicHashMap.get(epicId);
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        historyManager.addTask((subtaskHashMap.get(subtaskId)));
        return subtaskHashMap.get(subtaskId);
    }

    @Override
    public void createTask(Task task) {
        try {
            addPrioritizedTasks(task);
            task.setId(taskIdCounter);
            tasksHashMap.put(taskIdCounter, task);
            taskIdCounter++;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        try {
            addPrioritizedTasks(subtask);
            subtask.setId(taskIdCounter);
            subtask.setEpicId(epicId);
            subtaskHashMap.put(taskIdCounter, subtask);

            Epic epic = epicHashMap.get(epicId);
            epic.addSubtask(taskIdCounter);
            epicStatusCalculation(epicId);
            calculateEpicDuration(epicId);

            taskIdCounter++;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void createEpic(Epic epic) {
        try {
            addPrioritizedTasks(epic);
            epic.setId(taskIdCounter);
            epicHashMap.put(taskIdCounter, epic);

            taskIdCounter++;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void updateTask(Task task, int id) {
        try {
            deletePrioritizedTasks(tasksHashMap.get(id));
            addPrioritizedTasks(task);
            task.setId(id);
            tasksHashMap.put(id, task);
        } catch (IllegalArgumentException e) {
            addPrioritizedTasks(tasksHashMap.get(id));
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void updateSubtask(Subtask subtask, int id) {
        try {
            deletePrioritizedTasks(subtaskHashMap.get(id));
            addPrioritizedTasks(subtask);
            subtask.setId(id);
            subtaskHashMap.put(id, subtask);
            epicStatusCalculation(subtask.getEpicId());
            calculateEpicDuration(subtask.getEpicId());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            addPrioritizedTasks(subtaskHashMap.get(id));
        }

    }

    @Override
    public void updateEpic(Epic epic, int id) {
        try {
            deletePrioritizedTasks(epicHashMap.get(id));
            addPrioritizedTasks(epic);
            epic.setId(id);
            epicHashMap.put(id, epic);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            addPrioritizedTasks(epicHashMap.get(id));
        }
    }

    @Override
    public void deleteTask(int id) {
        deletePrioritizedTasks(tasksHashMap.get(id));
        tasksHashMap.remove(id);
        if(historyManager.getHistory().contains(tasksHashMap.get(id))) {
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        ArrayList<Integer> subtasksList = getSubtasksList(id);
        for (int subtaskId : subtasksList) {
            deletePrioritizedTasks(subtaskHashMap.get(subtaskId));
            subtaskHashMap.remove(subtaskId);
            if(historyManager.getHistory().contains(subtaskHashMap.get(subtaskId))) {
                historyManager.remove(subtaskId);
            }
        }
        deletePrioritizedTasks(epicHashMap.get(id));
        epicHashMap.remove(id);
        if(historyManager.getHistory().contains((epicHashMap.get(id)))) {
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtaskHashMap.get(id);
        Epic epic = epicHashMap.get(subtask.getEpicId());

        epic.removeSubtask(id);
        epicStatusCalculation(subtask.getEpicId());
        calculateEpicDuration(subtask.getEpicId());
        deletePrioritizedTasks(subtaskHashMap.get(id));
        subtaskHashMap.remove(id);
        if(historyManager.getHistory().contains(subtaskHashMap.get(id))) {
            historyManager.remove(id);
        }
    }

    @Override
    public ArrayList<Integer> getSubtasksList(int epicId) {
        Epic epic = epicHashMap.get(epicId);
        return (ArrayList<Integer>)epic.getSubtasks();
    }


    private void epicStatusCalculation(int epicId) {
        Epic epic = epicHashMap.get(epicId);
        ArrayList<Integer> subtasksList = getSubtasksList(epicId);

        int newStatus = 0;
        int doneStatus = 0;
        int wipStatus = 0;
        for (int subtaskId : subtasksList) {
            TaskStatus status = subtaskHashMap.get(subtaskId).getStatus();

            switch (status) {
                case NEW:
                    newStatus++;
                    break;
                case DONE:
                    doneStatus++;
                    break;
                case IN_PROGRESS:
                    wipStatus++;
                    break;
            }
        }

        if (newStatus == subtasksList.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (doneStatus == subtasksList.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (newStatus == 0 && doneStatus == 0 && wipStatus == 0) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void calculateEpicDuration(int epicId) {
        Epic epic = epicHashMap.get(epicId);
        Duration duration = Duration.ofHours(0);
        for(Integer subtaskId : epic.getSubtasks()) {
            duration = duration.plus(getSubtaskHashMap().get(subtaskId).getDuration());
        }
        epic.setDuration(duration);
        epic.setEndTime(epic.getStartTime().plus(duration));
    }

    public void addPrioritizedTasks(Task task) throws IllegalArgumentException {
        for (Task taskTime : prioritizedTasks) {
            if ((taskTime.getStartTime().isBefore(task.getStartTime()) &&
                    taskTime.getEndTime().isAfter(task.getStartTime())) ||
                    (taskTime.getStartTime().isBefore(task.getEndTime()) &&
                            taskTime.getEndTime().isAfter(task.getEndTime()))) {

                throw new IllegalArgumentException("Время выполнения задач не может пересекаться");
            }
        }
        prioritizedTasks.add(task);
    }

    private void deletePrioritizedTasks(Task task) {
            prioritizedTasks.remove(task);
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public void printTasks() {
        for (Task task : tasksHashMap.values()) {
            System.out.println(task);
        }
        for (Epic epic : epicHashMap.values()) {
            System.out.println(epic);
        }
        for (Subtask subtask : subtaskHashMap.values()) {
            System.out.println(subtask);
        }
        System.out.println();
    }


    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}
