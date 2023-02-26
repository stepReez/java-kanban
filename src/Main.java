
import model.*;
import service.server.HttpTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        HttpTaskManager httpTaskManager = new HttpTaskManager("http://localhost:8080/");
        Task learnJava = new Task("Пройти 6 спринт на Практикуме", "думаем", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 10, 12, 0), Duration.ofHours(3));

        Epic epic = new Epic("Name", "Description", LocalDateTime.of(2022, 2, 10, 18, 0));

        Subtask subtask = new Subtask(1, "Subtask", "Des", TaskStatus.DONE,
                LocalDateTime.of(2023, 2, 10, 14, 0), Duration.ofHours(3));

        Subtask subtask1 = new Subtask(1, "Subtask1", "Description1", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 10, 15, 0), Duration.ofHours(3));

        Task task = new Task("Task", "TaskDescription", TaskStatus.DONE,
                LocalDateTime.of(2023, 2, 10, 16, 0), Duration.ofHours(3));

        httpTaskManager.createTask(learnJava);
        httpTaskManager.createEpic(epic);
        httpTaskManager.createSubtask(subtask);
        httpTaskManager.createSubtask(subtask1);
        httpTaskManager.createTask(task);

        System.out.println(httpTaskManager.getSubtask(2));

    }
}
