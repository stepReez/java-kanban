import service.Manager;
import model.*;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        Epic learnJava = new Epic("Пройти 3 спринт на Практикуме", "");
        manager.createEpic(learnJava);

        Subtask learnTheory = new Subtask("Пройти теорию", "", TaskStatus.DONE);
        Subtask practicalPart = new Subtask("Пройти практическую часть", "", TaskStatus.IN_PROGRESS);

        manager.createSubtask(learnTheory, 0);
        manager.createSubtask(practicalPart, 0);

        Task washDishes = new Task("Помыть посуду", "Выполнить до вечера", TaskStatus.NEW);
        Task workout = new Task("Сделать зарядку", "", TaskStatus.NEW);

        manager.createTask(washDishes);
        manager.createTask(workout);

        Epic epic = new Epic("Задача", "Креативность законилась");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "", TaskStatus.NEW);
        manager.createSubtask(subtask, 5);

        manager.printTasks();

        subtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask, 6);

        manager.removeById(0);
        manager.removeById(3);

        manager.printTasks();
    }
}
