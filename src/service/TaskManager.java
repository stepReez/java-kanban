package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {

    public HashMap<Integer, Task> getTasksHashMap();

    public void clearTasks();

    public Task getTask(int taskId);

    public void createTask(Task task);

    public void createSubtask(Subtask subtask, int epicId);

    public void createEpic(Epic epic);

    public void updateTask(Task task, int id);

    public void updateSubtask(Subtask subtask, int id);

    public void updateEpic(Epic epic, int id);

    public void removeById(int id);

    public void printTasks();

    public ArrayList<Integer> getSubtasksList(int epicId);

    public ArrayList<Task> getHistory();
}
