import service.*;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        FileBackedTasksManager manager = new FileBackedTasksManager();

        Task learnJava = new Task("Пройти 6 спринт на Практикуме", "думаем", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 10, 12, 0), Duration.ofHours(3));
        manager.createTask(learnJava);


        Epic epic = new Epic("Name", "Description", LocalDateTime.of(2022, 2, 10, 18, 0));
        manager.createEpic(epic);

        manager.getEpic(1);
        manager.getTask(0);

        Subtask subtask = new Subtask("Subtask", "Des", TaskStatus.DONE,
                LocalDateTime.of(2023, 2, 10, 14, 0), Duration.ofHours(3));
        manager.createSubtask(subtask, 1);



        Subtask subtask1 = new Subtask("Subtask1", "Description1", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 10, 15, 0), Duration.ofHours(3));
        manager.createSubtask(subtask1, 1);
        manager.getSubtask(2);

        Task task = new Task("Task", "TaskDescription", TaskStatus.DONE,
                LocalDateTime.of(2023, 2, 10, 16, 0), Duration.ofHours(3));
        manager.createTask(task);


        System.out.println(manager.getHistory());
        manager.printTasks();

        System.out.println(manager.getPrioritizedTasks());
        System.out.println();

        FileBackedTasksManager manager1 = new FileBackedTasksManager();
        manager1.readSave();
        System.out.println(manager1.getHistory());
        manager1.printTasks();

    }
}
