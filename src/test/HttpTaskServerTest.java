package test;

import com.google.gson.*;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.server.HttpTaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    HttpTaskManager httpTaskManager;
    Task task;
    Epic epic;
    Subtask subtask;
    Gson gson = new Gson();

    @BeforeEach
    void beforeEach() {
        httpTaskManager = new HttpTaskManager("http://localhost:8080/");
        epic = new Epic("Test Epic", "Test description",
                LocalDateTime.of(2023, 2, 11, 12, 0));

        subtask = new Subtask(0, "Test Subtask", "Test description", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 12, 12, 0), Duration.ofHours(3));

        task = new Task("Test Task", "Test description", TaskStatus.NEW,
                LocalDateTime.of(2023, 2, 10, 12, 0), Duration.ofHours(3));
    }

    @AfterEach
    void afterEach() {
        httpTaskManager.stop();
    }

    @Test
    public void createTaskTest() throws IOException, InterruptedException {

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task task1 = httpTaskManager.getTask(0);
        assertEquals(task, task1);
    }


    @Test
    public void createEpicTest() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic epic1 = httpTaskManager.getEpic(0);
        assertEquals(epic, epic1);
    }

    @Test
    public void createSubtaskTest() throws IOException, InterruptedException {
        HttpClient httpClient1 = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        httpTaskManager.createEpic(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        HttpResponse<String> response = httpClient1.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        subtask.setId(1);

        Subtask subtask1 = httpTaskManager.getSubtask(1);
        assertEquals(subtask, subtask1);
    }

    @Test
    public void getTaskByIdTest() throws IOException, InterruptedException {
        httpTaskManager.createTask(task);

        HttpClient httpClientGet = HttpClient.newHttpClient();
        URI uriGet = URI.create("http://localhost:8080/tasks/task/?id=0");

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(uriGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = httpClientGet.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());

        String taskString = "[" + gson.toJson(task) + "]";
        assertEquals(taskString, responseGet.body());
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        httpTaskManager.createEpic(epic);

        HttpClient httpClientGet = HttpClient.newHttpClient();
        URI uriGet = URI.create("http://localhost:8080/tasks/epic/?id=0");

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(uriGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = httpClientGet.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());

        String epicString = "[" + gson.toJson(epic) + "]";
        assertEquals(epicString, responseGet.body());

    }

    @Test
    public void getSubtaskByIdTest() throws IOException, InterruptedException {
        httpTaskManager.createEpic(epic);
        httpTaskManager.createSubtask(subtask);

        HttpClient httpClientGet = HttpClient.newHttpClient();
        URI uriGet = URI.create("http://localhost:8080/tasks/subtask/?id=1");

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(uriGet)
                .GET()
                .build();

        HttpResponse<String> responseGet = httpClientGet.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());

        String taskString = "[" + gson.toJson(subtask) + "]";
        assertEquals(taskString, responseGet.body());
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        httpTaskManager.createTask(task);

        HttpClient httpClient = HttpClient.newHttpClient();

        Task task1 = new Task("Updated task", "UPD",
                TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(1));
        String updTask = gson.toJson(task1);

        URI uriUpdate = URI.create("http://localhost:8080/tasks/task/?id=0");
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .uri(uriUpdate)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        HttpResponse<String> responseUpdate = httpClient.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseUpdate.statusCode());

        Task getTask = httpTaskManager.getTask(0);
        assertEquals(updTask, gson.toJson(getTask));
    }

    @Test
    public void updateSubtaskTest() throws IOException, InterruptedException {
        httpTaskManager.createEpic(epic);
        httpTaskManager.createSubtask(subtask);

        HttpClient httpClient = HttpClient.newHttpClient();

        Subtask task1 = new Subtask(0, "Updated subtask", "UPD",
                TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(1));
        task1.setId(1);

        String updTask = gson.toJson(task1);

        URI uriUpdate = URI.create("http://localhost:8080/tasks/subtask/?id=1");
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .uri(uriUpdate)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        HttpResponse<String> responseUpdate = httpClient.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseUpdate.statusCode());
        assertEquals(updTask, gson.toJson(httpTaskManager.getSubtask(1)));

    }

    @Test
    public void getAllTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("NEW task", "NEW",
                TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(1));

        httpTaskManager.createTask(task);
        httpTaskManager.createTask(task1);
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> responseGet = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());

        JsonElement jsonElement = JsonParser.parseString(responseGet.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        assertEquals(2, jsonObject.size());
    }

    @Test
    public void getAllEpicTest() throws IOException, InterruptedException {
        Epic task1 = new Epic("NEW subtask", "NEW", LocalDateTime.now());
        httpTaskManager.createEpic(epic);
        httpTaskManager.createEpic(task1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> responseGet = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());

        JsonElement jsonElement = JsonParser.parseString(responseGet.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        assertEquals(2, jsonObject.size());
    }

    @Test
    public void getAllSubtaskTest() throws IOException, InterruptedException {
        Subtask task1 = new Subtask(0, "NEW subtask", "NEW",
                TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(1));
        httpTaskManager.createEpic(epic);
        httpTaskManager.createSubtask(subtask);
        httpTaskManager.createSubtask(task1);
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> responseGet = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());

        JsonElement jsonElement = JsonParser.parseString(responseGet.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        assertEquals(2, jsonObject.size());
    }

    @Test
    public void deleteTaskByIdTest() throws IOException, InterruptedException {
        httpTaskManager.createTask(task);

        HttpClient httpClient = HttpClient.newHttpClient();

        URI uriDelete = URI.create("http://localhost:8080/tasks/task/?id=0");

        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(uriDelete)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, response.body().length());
    }

    @Test
    public void deleteEpicByIdTest() throws IOException, InterruptedException {
        httpTaskManager.createEpic(epic);

        HttpClient httpClient = HttpClient.newHttpClient();

        URI uriDelete = URI.create("http://localhost:8080/tasks/epic/?id=0");

        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(uriDelete)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, response.body().length());
    }

    @Test
    public void deleteSubtaskByIdTest() throws IOException, InterruptedException {
        httpTaskManager.createEpic(epic);
        httpTaskManager.createSubtask(subtask);

        HttpClient httpClient = HttpClient.newHttpClient();

        URI uriDelete = URI.create("http://localhost:8080/tasks/subtask/?id=1");

        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(uriDelete)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, response.body().length());
    }

    @Test
    public void deleteAllTest() throws IOException, InterruptedException {
        httpTaskManager.createEpic(epic);
        httpTaskManager.createSubtask(subtask);
        httpTaskManager.createTask(task);

        HttpClient httpClient = HttpClient.newHttpClient();

        URI uriDelete = URI.create("http://localhost:8080/tasks/");
        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(uriDelete)
                .DELETE()
                .build();
        HttpResponse<String> responseDelete = httpClient.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDelete.statusCode());

        assertEquals(0, httpTaskManager.getTasksHashMap().size());
        assertEquals(0, httpTaskManager.getEpicHashMap().size());
        assertEquals(0, httpTaskManager.getSubtaskHashMap().size());
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        httpTaskManager.createTask(task);
        httpTaskManager.getTask(0);
        httpTaskManager.createEpic(epic);
        httpTaskManager.getEpic(1);

        HttpClient httpClient = HttpClient.newHttpClient();

        URI historyURI = URI.create("http://localhost:8080/tasks/history");
        HttpRequest requestHistory = HttpRequest.newBuilder()
                .uri(historyURI)
                .GET()
                .build();

        HttpResponse<String> historyResponse = httpClient.send(requestHistory, HttpResponse.BodyHandlers.ofString());

        JsonElement jsonElement = JsonParser.parseString(historyResponse.body());
        JsonArray jsonObject = jsonElement.getAsJsonArray();

        assertEquals(200, historyResponse.statusCode());
        assertEquals(2, jsonObject.size());
    }

    @Test
    public void getPrioritizedTasksTest() throws IOException, InterruptedException {
        httpTaskManager.createEpic(epic);
        httpTaskManager.createSubtask(subtask);
        httpTaskManager.createTask(task);

        HttpClient httpClient = HttpClient.newHttpClient();

        URI historyURI = URI.create("http://localhost:8080/tasks");
        HttpRequest requestHistory = HttpRequest.newBuilder()
                .uri(historyURI)
                .GET()
                .build();

        HttpResponse<String> historyResponse = httpClient.send(requestHistory, HttpResponse.BodyHandlers.ofString());

        JsonElement jsonElement = JsonParser.parseString(historyResponse.body());
        JsonArray jsonObject = jsonElement.getAsJsonArray();

        assertEquals(200, historyResponse.statusCode());
        assertEquals(3, jsonObject.size());
    }

    @Test
    public void readSaveTest() {
        httpTaskManager.createEpic(epic);
        httpTaskManager.createSubtask(subtask);
        httpTaskManager.createTask(task);
        httpTaskManager.getEpic(0);
        httpTaskManager.getSubtask(1);
        httpTaskManager.getTask(2);

        ArrayList<Task> historyBefore = httpTaskManager.getHistory();
        HashMap<Integer, Task> taskListBefore = httpTaskManager.getTasksHashMap();
        HashMap<Integer, Epic> epicListBefore = httpTaskManager.getEpicHashMap();
        HashMap<Integer, Subtask> subtaskListBefore = httpTaskManager.getSubtaskHashMap();

        httpTaskManager.clearTasks();
        httpTaskManager.readSave();

        ArrayList<Task> historyAfter = httpTaskManager.getHistory();
        HashMap<Integer, Task> taskListAfter = httpTaskManager.getTasksHashMap();
        HashMap<Integer, Epic> epicListAfter = httpTaskManager.getEpicHashMap();
        HashMap<Integer, Subtask> subtaskListAfter = httpTaskManager.getSubtaskHashMap();

        assertEquals(historyBefore, historyAfter);
        assertEquals(taskListBefore, taskListAfter);
        assertEquals(epicListBefore, epicListAfter);
        assertEquals(subtaskListBefore, subtaskListAfter);
    }

}