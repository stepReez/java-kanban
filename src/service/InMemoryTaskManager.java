package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager{

    HistoryManager historyManager = Managers.getDefaultHistory();

    private int taskIdCounter = 0;
    private HashMap<Integer, Task> tasksHashMap = new HashMap<>();
    private HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();

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
        task.setId(taskIdCounter);
        tasksHashMap.put(taskIdCounter, task);
        taskIdCounter++;
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        subtask.setId(taskIdCounter);
        subtask.setEpicId(epicId);
        subtaskHashMap.put(taskIdCounter, subtask);

        Epic epic = epicHashMap.get(epicId);
        epic.addSubtask(taskIdCounter);
        epicStatusCalculation(epicId);

        taskIdCounter++;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(taskIdCounter);
        epicHashMap.put(taskIdCounter, epic);

        taskIdCounter++;
    }

    @Override
    public void updateTask(Task task, int id) {
        task.setId(id);
        tasksHashMap.put(id, task);
    }

    @Override
    public void updateSubtask(Subtask subtask, int id) {
        subtask.setId(id);
        subtaskHashMap.put(id, subtask);
        epicStatusCalculation(subtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        epic.setId(id);
        epicHashMap.put(id, epic);
    }

    @Override
    public void deleteTask(int id) {
        tasksHashMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        ArrayList<Integer> subtasksList = getSubtasksList(id);
        for (int subtaskId : subtasksList) {
            subtaskHashMap.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epicHashMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtaskHashMap.get(id);
        Epic epic = epicHashMap.get(subtask.getEpicId());

        epic.removeSubtask(id);
        epicStatusCalculation(subtask.getEpicId());
        subtaskHashMap.remove(id);
        historyManager.remove(id);
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
