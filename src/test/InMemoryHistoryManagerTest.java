package test;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;
    Task task;
    Epic epic;
    Subtask subtask;
    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Task", "Description", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 10, 12, 0), Duration.ofHours(3));
        epic = new Epic("Epic", "Description",
                LocalDateTime.of(2023, 2, 11, 12, 0));
        subtask = new Subtask(1, "Subtask", "Description", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 12, 12, 0), Duration.ofHours(3));
    }

    @Test
    void addTaskNormalTest() {

        task.setId(0);
        historyManager.addTask(task);


        epic.setId(1);
        historyManager.addTask(epic);


        subtask.setId(2);
        subtask.setEpicId(1);
        historyManager.addTask(subtask);


        assertEquals(3, historyManager.getHistory().size(), "Не все задачи добавлены в историю");
    }

    @Test
    void addTaskDuplicateTest() {
        task.setId(0);
        epic.setId(1);

        historyManager.addTask(task);
        historyManager.addTask(epic);
        historyManager.addTask(task);

        assertEquals(2, historyManager.getHistory().size(), "История дублируется");
    }

    @Test
    void addTaskEmptyListTest() {
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void removeFirstTask() {
        task.setId(0);
        historyManager.addTask(task);

        epic.setId(1);
        historyManager.addTask(epic);

        subtask.setId(2);
        subtask.setEpicId(1);
        historyManager.addTask(subtask);

        historyManager.remove(0);

        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void removeMiddleTask() {
        task.setId(0);
        historyManager.addTask(task);

        epic.setId(1);
        historyManager.addTask(epic);

        Task task1 = new Task("Task", "Description", TaskStatus.NEW,
                LocalDateTime.of(2023, 3, 10, 12, 0), Duration.ofHours(3));
        task1.setId(2);
        historyManager.addTask(task1);

        historyManager.remove(1);

        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void removeLastTask() {
        task.setId(0);
        historyManager.addTask(task);

        epic.setId(1);
        historyManager.addTask(epic);

        subtask.setId(2);
        subtask.setEpicId(1);
        historyManager.addTask(subtask);

        historyManager.remove(2);

        assertEquals(2, historyManager.getHistory().size());
    }
}