package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager{

    HistoryManager historyManager = Managers.getDefaultHistory();

    private int taskIdCounter = 0;
    private HashMap<Integer, Task> tasksHashMap = new HashMap<>();

    @Override
    public HashMap<Integer, Task> getTasksHashMap() {
        return tasksHashMap;
    }

    @Override
    public void clearTasks() {
        tasksHashMap.clear();
    }


    private Task getById(int id) {
        return tasksHashMap.get(id);
    }

    @Override
    public Task getTask(int taskId) {
        historyManager.addTask(getById(taskId));
        return getById(taskId);
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
        tasksHashMap.put(taskIdCounter, subtask);

        Epic epic = (Epic)tasksHashMap.get(epicId);
        epic.addSubtask(taskIdCounter);
        epicStatusCalculation(epicId);

        taskIdCounter++;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(taskIdCounter);
        tasksHashMap.put(taskIdCounter, epic);

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
        tasksHashMap.put(id, subtask);
        epicStatusCalculation(subtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        epic.setId(id);
        tasksHashMap.put(id, epic);
    }

    @Override
    public void removeById(int id) {

        if(getById(id).getClass() == Epic.class) {
            ArrayList<Integer> subtasksList = getSubtasksList(id);
            for (int subtaskId : subtasksList) {
                tasksHashMap.remove(subtaskId);
            }

        } else if (getById(id).getClass() == Subtask.class) {
            Subtask subtask = (Subtask)getById(id);
            Epic epic = (Epic)getById(subtask.getEpicId());

            epic.removeSubtask(id);
            epicStatusCalculation(subtask.getEpicId());
        }

        tasksHashMap.remove(id);
    }

    @Override
    public ArrayList<Integer> getSubtasksList(int epicId) {
        Epic epic = (Epic)getById(epicId);
        return (ArrayList<Integer>)epic.getSubtasks();
    }


    private void epicStatusCalculation(int epicId) {
        Epic epic = (Epic)getById(epicId);
        ArrayList<Integer> subtasksList = getSubtasksList(epicId);

        int newStatus = 0;
        int doneStatus = 0;
        int wipStatus = 0;
        for (int subtaskId : subtasksList) {
            TaskStatus status = getById(subtaskId).getStatus();

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
        System.out.println();
    }


    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}
