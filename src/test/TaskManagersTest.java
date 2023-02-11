package test;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import service.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagersTest {
    TaskManager taskManager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        task = new Task("Test Task", "Test description", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 10, 12, 0), Duration.ofHours(3));

        epic = new Epic("Test Epic", "Test description",
                LocalDateTime.of(2023, 2, 11, 12, 0));

        subtask = new Subtask("Test Subtask", "Test description", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 12, 12, 0), Duration.ofHours(3));
    }


    @Test
    void createTaskTest() {
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTask(0);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final HashMap<Integer, Task> tasks = taskManager.getTasksHashMap();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createEpicTest() {

        taskManager.createEpic(epic);

        final Epic savedEpic = taskManager.getEpic(0);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final HashMap<Integer, Epic> epicHashMap = taskManager.getEpicHashMap();

        assertNotNull(epicHashMap, "Задачи на возвращаются.");
        assertEquals(1, epicHashMap.size(), "Неверное количество задач.");
        assertEquals(epic, epicHashMap.get(0), "Задачи не совпадают.");
    }

    @Test
    void createSubtaskTest() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask, 0);

        final Subtask savedSubtask = taskManager.getSubtask(1);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final HashMap<Integer, Subtask> subtaskHashMap = taskManager.getSubtaskHashMap();

        assertNotNull(subtaskHashMap, "Задачи на возвращаются.");
        assertEquals(1, subtaskHashMap.size(), "Неверное количество задач.");
        assertEquals(subtask, subtaskHashMap.get(1), "Задачи не совпадают.");
    }

    @Test
    void checkSubtaskEpicId() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask, 0);

        assertEquals(0, taskManager.getSubtask(1).getEpicId(), "У Subtask отсутствует EpicId");
    }

    @Test
    void addTest() {
        taskManager.createTask(task);
        taskManager.getTask(0);
        final ArrayList<Task> history = taskManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void clearTasksTest() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask, 0);
        taskManager.createTask(task);

        taskManager.clearTasks();

        assertEquals(0, taskManager.getTasksHashMap().size(), "Не все Task удалены");
        assertEquals(0, taskManager.getEpicHashMap().size(), "Не все Epic удалены");
        assertEquals(0, taskManager.getSubtaskHashMap().size(), "Не все Subtask удалены");
    }

    @Test
    void clearTasksEmptyListTest() {
        taskManager.clearTasks();

        assertEquals(0, taskManager.getTasksHashMap().size(), "Не все Task удалены");
        assertEquals(0, taskManager.getEpicHashMap().size(), "Не все Epic удалены");
        assertEquals(0, taskManager.getSubtaskHashMap().size(), "Не все Subtask удалены");
    }

    @Test
    void updateTaskTest() {
        taskManager.createTask(task);

        Task updatedTask = new Task("Updated Task", "Updated Description", TaskStatus.DONE,
                LocalDateTime.of(2023, 2, 10, 12, 0), Duration.ofHours(3));

        taskManager.updateTask(updatedTask, 0);

        final Task savedTask = taskManager.getTask(0);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(updatedTask, savedTask, "Task не обновляется");
    }

    @Test
    void updateEpicTest() {
        taskManager.createEpic(epic);

        Epic updateEpic = new Epic("Updated Epic", "Updated Description",
                LocalDateTime.of(2023, 2, 16, 12, 0));
        taskManager.updateEpic(updateEpic, 0);

        final Epic savedEpic = taskManager.getEpic(0);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(updateEpic, savedEpic, "Epic не обновляется");
    }

    @Test
    void updateSubtaskTest() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask, 0);
        Subtask updateSubtask = new Subtask("Updated Subtask", "Updated Description", TaskStatus.DONE,
                LocalDateTime.of(2023, 2, 15, 12, 0), Duration.ofHours(3));
        taskManager.updateSubtask(updateSubtask, 1);

        final Subtask savedSubtask = taskManager.getSubtask(1);
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(updateSubtask, savedSubtask, "Subtask не обновляется");
    }

    @Test
    void deleteTaskTest() {
        taskManager.createTask(task);
        taskManager.deleteTask(0);
        assertEquals(0, taskManager.getTasksHashMap().size(), "Task не удаляется");
    }

    @Test
    void deleteEpicTest() {
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask NEW", "NEW", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 17, 12, 0), Duration.ofHours(3));
        taskManager.createSubtask(subtask1, 0);

        Subtask subtask2 = new Subtask("Subtask DONE", "DONE", TaskStatus.DONE,
                LocalDateTime.of(2023, 2, 18, 12, 0), Duration.ofHours(3));
        taskManager.createSubtask(subtask2, 0);

        taskManager.deleteEpic(0);
        assertEquals(0, taskManager.getEpicHashMap().size(), "Epic не удаляется");
        assertEquals(0, taskManager.getSubtaskHashMap().size(), "Subtask не удаляется");
    }

    @Test
    void deleteSubtaskTest() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask, 0);
        taskManager.deleteSubtask(1);
        assertEquals(0, taskManager.getSubtaskHashMap().size(), "Subtask не удаляется");
    }

    @Test
    void taskEndTimeCalculationTest() {
        LocalDateTime testLocalDate = LocalDateTime.of(2023, 2, 10, 15, 0);

        assertEquals(testLocalDate, task.getEndTime(), "Время окончания задачи высчитывается неверно");
    }

    @Test
    void subtaskEndTimeCalculationTest() {
        LocalDateTime testLocalDate = LocalDateTime.of(2023, 2, 12, 15, 0);

        assertEquals(testLocalDate, subtask.getEndTime(), "Время окончания задачи высчитывается неверно");
    }

    @Test
    void checkIntersections() {
        taskManager.createTask(task);
        Task taskIntersections = new Task("Test Task", "Test description", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 10, 13, 0), Duration.ofHours(3));

        TimeIntersectionsException ex = assertThrows(
                TimeIntersectionsException.class,
                () -> taskManager.addPrioritizedTasks(taskIntersections)
        );

        assertEquals("Время выполнения задач не может пересекаться", ex.getMessage());
    }
}