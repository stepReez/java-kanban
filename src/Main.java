import service.*;
import model.*;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Epic learnJava = new Epic("Пройти 4 спринт на Практикуме", "");
        manager.createEpic(learnJava);

        manager.getEpic(0);
        System.out.println(manager.getHistory());

        Subtask learnTheory = new Subtask("Пройти теорию", "", TaskStatus.DONE);
        Subtask practicalPart = new Subtask("Пройти практическую часть", "", TaskStatus.IN_PROGRESS);

        manager.createSubtask(learnTheory, 0);
        manager.createSubtask(practicalPart, 0);

        manager.getSubtask(1);
        manager.getSubtask(2);
        System.out.println(manager.getHistory());

        Task washDishes = new Task("Помыть посуду", "Выполнить до вечера", TaskStatus.NEW);
        Task workout = new Task("Сделать зарядку", "", TaskStatus.NEW);

        manager.createTask(washDishes);
        manager.createTask(workout);

        manager.getTask(3);
        manager.getTask(4);
        System.out.println(manager.getHistory());

        Epic epic = new Epic("Задача", "Креативность законилась");
        manager.createEpic(epic);

        manager.getEpic(5);
        System.out.println(manager.getHistory());

        Subtask subtask = new Subtask("Подзадача", "", TaskStatus.NEW);
        manager.createSubtask(subtask, 5);

        manager.getSubtask(6);
        System.out.println(manager.getHistory());

        manager.printTasks();

        subtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask, 6);

        manager.deleteEpic(0);
        manager.deleteTask(3);

        manager.printTasks();
    }
}
