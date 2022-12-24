package service;

import model.Task;

import java.util.LinkedList;

public interface HistoryManager {

    public void addTask(Task task);

    public LinkedList<Task> getHistory();
}
