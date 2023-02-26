package test;

import model.Epic;
import model.Subtask;
import model.TaskStatus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import service.server.HttpTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    HttpTaskManager taskManager;
    Epic epic;
    Subtask subtaskNew;
    Subtask subtaskDone;
    Subtask subtaskInProgress;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault("http://localhost:8080/");
        epic = new Epic("Test Epic", "Test description",
                LocalDateTime.of(2023, 2, 10, 12, 0));
        subtaskNew = new Subtask(0, "Subtask NEW", "NEW", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 20, 12, 0), Duration.ofHours(3));
        subtaskDone = new Subtask(0, "Subtask DONE", "DONE", TaskStatus.DONE,
                LocalDateTime.of(2023, 2, 6, 12, 0), Duration.ofHours(10));
        subtaskInProgress = new Subtask(0, "Subtask IN_PROGRESS", "IN_PROGRESS", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 2, 10, 5, 0), Duration.ofHours(3));
    }

    @AfterEach
    void afterEach() {
        taskManager.clearTasks();
        taskManager.stop();
    }

    @Test
    void epicEmptySubtaskListTest() {

        taskManager.createEpic(epic);

        assertEquals(TaskStatus.NEW, taskManager.getEpic(0).getStatus(), "Статус пустого Epic не NEW");
    }

    @Test
    void epicNewSubtaskListTest() {

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtaskNew);

        assertEquals(TaskStatus.NEW, taskManager.getEpic(0).getStatus(), "Статус Epic не NEW");
    }

    @Test
    void epicDoneSubtaskListTest() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtaskDone);

        assertEquals(TaskStatus.DONE, taskManager.getEpic(0).getStatus(), "Статус Epic не DONE");
    }

    @Test
    void epicNewAndDoneSubtaskListTest() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtaskNew);
        taskManager.createSubtask(subtaskDone);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(0).getStatus(),
                "Статус Epic не IN_PROGRESS");
    }

    @Test
    void epicInProgressSubtaskListTest() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtaskInProgress);

        assertEquals(TaskStatus.IN_PROGRESS,
                taskManager.getEpic(0).getStatus(), "Статус Epic не IN_PROGRESS");
    }

    @Test
    void epicEndTimeCalculation() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtaskNew);
        taskManager.createSubtask(subtaskDone);
        taskManager.createSubtask(subtaskInProgress);

        LocalDateTime testDateTime = LocalDateTime.of(2023, 2, 11, 4, 0);

        assertEquals(16, epic.getDuration().toHours(), "Время выполнения Epic считается неправильно");
        assertEquals(testDateTime, epic.getEndTime(), "Время окончания высчитывается неверно");
    }
}
