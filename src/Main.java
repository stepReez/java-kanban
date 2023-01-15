import service.*;
import model.*;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Epic learnJava = new Epic("Пройти 5 спринт на Практикуме", "");
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

        manager.getEpic(0);
        System.out.println(manager.getHistory());

        Epic epic = new Epic("Задача", "");
        manager.createEpic(epic);

        manager.getEpic(3);
        System.out.println(manager.getHistory());

        manager.deleteEpic(3);
        System.out.println(manager.getHistory());

        manager.deleteEpic(0);
        System.out.println(manager.getHistory());
    }
}
