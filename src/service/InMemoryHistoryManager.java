package service;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private ArrayList<Task> taskHistory = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
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
