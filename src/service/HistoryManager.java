package service;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    public void addTask(Task task);

    public ArrayList<Task> getHistory();
}
