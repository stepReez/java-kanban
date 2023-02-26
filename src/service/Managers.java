package service;

import service.server.HttpTaskManager;
import service.server.HttpTaskServer;

public class Managers {

    public static HttpTaskManager getDefault(String url) {
        return new HttpTaskManager(url);
    }

    public static FileBackedTasksManager getFileManager() {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
