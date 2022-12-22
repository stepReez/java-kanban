import service.*;
import model.*;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Epic learnJava = new Epic("Пройти 4 спринт на Практикуме", "");
        manager.createEpic(learnJava);

        manager.getTask(0);
        System.out.println(manager.getHistory());

        Subtask learnTheory = new Subtask("Пройти теорию", "", TaskStatus.DONE);
        Subtask practicalPart = new Subtask("Пройти практическую часть", "", TaskStatus.IN_PROGRESS);

        manager.createSubtask(learnTheory, 0);
        manager.createSubtask(practicalPart, 0);

        manager.getTask(1);
        manager.getTask(2);
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

        manager.getTask(5);
        System.out.println(manager.getHistory());

        Subtask subtask = new Subtask("Подзадача", "", TaskStatus.NEW);
        manager.createSubtask(subtask, 5);

        manager.getTask(6);
        System.out.println(manager.getHistory());

        manager.printTasks();

        subtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask, 6);

        manager.removeById(0);
        manager.removeById(3);

        manager.printTasks();
    }
}
