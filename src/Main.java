import service.*;
import model.*;

public class Main {

    public static void main(String[] args) {

        FileBackedTasksManager manager = new FileBackedTasksManager();

        Task learnJava = new Task("Пройти 6 спринт на Практикуме", "думаем", TaskStatus.NEW);
        manager.createTask(learnJava);


        Epic epic = new Epic("Name", "Description");
        manager.createEpic(epic);

        manager.getEpic(1);
        manager.getTask(0);

        Subtask subtask = new Subtask("Subtask", "Des", TaskStatus.DONE);
        manager.createSubtask(subtask, 1);

        manager.getSubtask(2);

        Subtask subtask1 = new Subtask("Subtask1", "Description1", TaskStatus.NEW);
        manager.createSubtask(subtask1, 1);

        Task task = new Task("Task", "TaskDescription", TaskStatus.DONE);
        manager.createTask(task);

        manager.getTask(4);

        System.out.println(manager.getHistory());
        manager.printTasks();

        FileBackedTasksManager manager1 = new FileBackedTasksManager();
        manager1.readSave();
        System.out.println(manager1.getHistory());
        manager1.printTasks();

    }
}
