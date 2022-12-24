package service;

import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager{
    private LinkedList<Task> taskHistory = new LinkedList<>();

    @Override
    public LinkedList<Task> getHistory() {
        return taskHistory;
    }

    @Override
    public void addTask(Task task) {
        if (taskHistory.size() < 10) {
            taskHistory.add(task);
        } else {
            taskHistory.remove(0);
            taskHistory.add(task);
        }
    }
}
