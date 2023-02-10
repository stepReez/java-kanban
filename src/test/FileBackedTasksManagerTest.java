package test;

import model.Epic;
import model.Task;
import model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;


import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest {

    FileBackedTasksManager manager;

    @BeforeEach
    void beforeEach() {
        manager = new FileBackedTasksManager();
    }

    @Test
    void emptyHistorySaveTest() {
        Task task = new Task("Task", "Des", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 10, 12, 0), Duration.ofHours(3));
        manager.createTask(task);

        FileBackedTasksManager manager1 = new FileBackedTasksManager();
        manager1.readSave();

        assertEquals(0, manager1.getHistory().size());
    }

    @Test
    void saveEpicWithoutSubtask() {
        Epic epic = new Epic("Epic", "Des",
                LocalDateTime.of(2023, 2, 10, 12, 0));
        manager.createEpic(epic);
        manager.getEpic(0);

        FileBackedTasksManager manager1 = new FileBackedTasksManager();
        manager1.readSave();
        assertEquals(1, manager1.getHistory().size());
    }

    @Test
    void emptySaveTest() {
        Task task = new Task("Task", "Des", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 10, 12, 0), Duration.ofHours(3));
        manager.createTask(task);
        manager.deleteTask(0);

        FileBackedTasksManager manager1 = new FileBackedTasksManager();
        manager1.readSave();

        assertEquals(0, manager1.getHistory().size());
    }
}