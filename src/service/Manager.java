package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private int taskIdCounter = 0;
    private HashMap<Integer, Task> tasksHashMap = new HashMap<>();

    public HashMap<Integer, Task> getTasksHashMap() {
        return tasksHashMap;
    }

    public void clearTasks() {
        tasksHashMap.clear();
    }

    public Task getById(int id) {
        return tasksHashMap.get(id);
    }

    public void createTask(Task task) {
        task.setId(taskIdCounter);
        tasksHashMap.put(taskIdCounter, task);
        taskIdCounter++;
    }

    public void createSubtask(Subtask subtask, int epicId) {
        subtask.setId(taskIdCounter);
        subtask.setEpicId(epicId);
        tasksHashMap.put(taskIdCounter, subtask);

        Epic epic = (Epic)tasksHashMap.get(epicId);
        epic.addSubtask(taskIdCounter);
        epicStatusCalculation(epicId);

        taskIdCounter++;
    }

    public void createEpic(Epic epic) {
        epic.setId(taskIdCounter);
        tasksHashMap.put(taskIdCounter, epic);
        taskIdCounter++;
    }

    public void updateTask(Task task, int id) {
        task.setId(id);
        tasksHashMap.put(id, task);
    }

    public void updateSubtask(Subtask subtask, int id) {
        subtask.setId(id);
        tasksHashMap.put(id, subtask);
        epicStatusCalculation(subtask.getEpicId());
    }


    public void updateEpic(Epic epic, int id) {
        epic.setId(id);
        tasksHashMap.put(id, epic);
    }

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

    public void printTasks() {
        for (Task task : tasksHashMap.values()) {
            System.out.println(task);
        }
        System.out.println();
    }
}
