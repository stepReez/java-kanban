package service.server;

        import com.google.gson.*;
        import com.sun.net.httpserver.HttpExchange;
        import com.sun.net.httpserver.HttpServer;

        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.net.InetSocketAddress;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.regex.Pattern;

        import model.Epic;
        import model.Subtask;
        import model.Task;
        import service.FileBackedTasksManager;
        import service.Managers;

        import static java.nio.charset.StandardCharsets.UTF_8;
        import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

public class HttpTaskServer {
    public static final int PORT = 8080;
    public FileBackedTasksManager fm = Managers.getFileManager();
    public static Gson gson = new Gson();
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();
    public HttpTaskServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handle);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    public void start() throws IOException {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);

        server.start();

        System.out.println("Сервер запущен");
    }

    public void stop() {
        fm.clearTasks();
        this.server.stop(1);
        System.out.println("Остановлен сервер на порту:" + PORT);
    }

    private void load(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/load");
            if (!hasAuth(h)) {
                System.out.println("Запрос не авторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для загрузки пустой. key указывается в пути: /load/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = data.get(key);

                if(value.isBlank()) {
                    h.sendResponseHeaders(h.getResponseCode(), 0);
                } else {
                    byte[] bytes = value.getBytes(DEFAULT_CHARSET);
                    h.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = h.getResponseBody()) {
                        os.write(bytes);
                    }
                }
                h.close();
            } else {
                System.out.println("/save ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                System.out.println("Запрос не авторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

        public void handle(HttpExchange exchange) {
            try {
                String path = exchange.getRequestURI().getPath();
                String requestMethod = exchange.getRequestMethod();
                switch (requestMethod) {
                    case "GET":
                        if (Pattern.matches("^/tasks/task/\\d+$", path)) {
                            getTaskById(exchange);
                        } else if (Pattern.matches("^/tasks/epic/\\d+$", path)) {
                            getEpicById(exchange);
                        } else if (Pattern.matches("^/tasks/subtask/", path)) {
                            getSubtaskById(exchange);
                        } else if (Pattern.matches("^/tasks/task$", path)) {
                            getAllTask(exchange);
                        } else if (Pattern.matches("^/tasks/epic$", path)) {
                            getAllEpic(exchange);
                        } else if (Pattern.matches("^/tasks/subtask$", path)) {
                            getAllSubtask(exchange);
                        } else if (Pattern.matches("^/tasks/history$", path)) {
                            getHistory(exchange);
                        } else if (Pattern.matches("^/tasks/epic/subtask/\\d+$", path)) {
                            getSubtasksByEpic(exchange);
                        } else {
                            getPrioritizedTasks(exchange);
                        }
                        break;
                    case "POST":
                        if (Pattern.matches("^/tasks/task/", path)) {
                            updateTask(exchange);
                        } else if (Pattern.matches("^/tasks/subtask/", path)) {
                            updateSubtask(exchange);
                        } else if (Pattern.matches("^/tasks/epic$", path)) {
                            createEpic(exchange);
                        } else if (Pattern.matches("^/tasks/task$", path)) {
                            createTask(exchange);
                        } else if (Pattern.matches("^/tasks/subtask$", path)) {
                            createSubtask(exchange);
                        }
                        break;
                    case "DELETE":
                        if (Pattern.matches("^/tasks/task/\\d+$", path)) {
                            deleteTaskById(exchange);
                        } else if (Pattern.matches("^/tasks/epic/\\d+$", path)) {
                            deleteEpicById(exchange);
                        } else if (Pattern.matches("^/tasks/subtask/\\d+$", path)) {
                            deleteSubtaskById(exchange);
                        } else {
                            deleteAll(exchange);
                        }
                        break;
                    default:
                        System.out.println("Получен некорректный запрос");
                        exchange.sendResponseHeaders(405, 0);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseString,
                                   int responseCode) throws IOException {
            if(responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }

        private void createTask(HttpExchange exchange) throws IOException{
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Task task = gson.fromJson(body, Task.class);
                fm.createTask(task);
                writeResponse(exchange, "", 200);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорректный JSON", 400);
            }
        }

        private void createEpic(HttpExchange exchange) throws IOException{
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Epic epic = gson.fromJson(body, Epic.class);
                fm.createEpic(epic);
                writeResponse(exchange, "", 200);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорректный JSON", 400);
            }
        }

        private void createSubtask(HttpExchange exchange) throws IOException{
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Subtask subtask = gson.fromJson(body, Subtask.class);
                fm.createSubtask(subtask);
                writeResponse(exchange, "", 200);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорректный JSON", 400);
            }
        }

        private void getTaskById(HttpExchange exchange) throws IOException{
            String[] query = exchange.getRequestURI().getQuery().split("=");
            String[] id = query[1].split(",");
            ArrayList<Task> tasks = new ArrayList<>();
            for (String param : id) {
                int intId = Integer.parseInt(param);
                tasks.add(fm.getTask(intId));
            }
            writeResponse(exchange, gson.toJson(tasks), 200);
        }

        private void getEpicById(HttpExchange exchange) throws IOException{
            String[] query = exchange.getRequestURI().getQuery().split("=");
            String[] id = query[1].split(",");
            ArrayList<Epic> epics = new ArrayList<>();
            for (String param : id) {
                int intId = Integer.parseInt(param);
                epics.add(fm.getEpic(intId));
            }
            writeResponse(exchange, gson.toJson(epics), 200);
        }

        private void getSubtasksByEpic(HttpExchange exchange) throws IOException{
            String[] query = exchange.getRequestURI().getQuery().split("=");
            int id = Integer.parseInt(query[1]);
            ArrayList<Integer> subtasksList = fm.getSubtasksList(id);
            writeResponse(exchange, gson.toJson(subtasksList), 200);
        }

        private void getSubtaskById(HttpExchange exchange) throws IOException{
            String[] query = exchange.getRequestURI().getQuery().split("=");
            String[] id = query[1].split(",");
            ArrayList<Subtask> subtasks = new ArrayList<>();
            for (String param : id) {
                int intId = Integer.parseInt(param);
                subtasks.add(fm.getSubtask(intId));
            }
            writeResponse(exchange, gson.toJson(subtasks), 200);
        }

        private void updateTask(HttpExchange exchange) throws IOException{
            String[] query = exchange.getRequestURI().getQuery().split("=");
            int id = Integer.parseInt(query[1]);
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Task task = gson.fromJson(body, Task.class);
                fm.updateTask(task, id);
                writeResponse(exchange, "", 200);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорректный JSON", 400);
            }
        }

        private void updateSubtask(HttpExchange exchange) throws IOException{
            String[] query = exchange.getRequestURI().getQuery().split("=");
            int id = Integer.parseInt(query[1]);
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Subtask subtask = gson.fromJson(body, Subtask.class);
                fm.updateSubtask(subtask, id);
                writeResponse(exchange, "", 200);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорректный JSON", 400);
            }
        }

        private void getAllTask(HttpExchange exchange) throws IOException {
            String task = gson.toJson(fm.getTasksHashMap());
            writeResponse(exchange, task, 200);
        }

        private void getAllEpic(HttpExchange exchange) throws IOException {
            String epic = gson.toJson(fm.getEpicHashMap());
            writeResponse(exchange, epic, 200);
        }

        private void getAllSubtask(HttpExchange exchange) throws IOException {
            String subtask = gson.toJson(fm.getSubtaskHashMap());
            writeResponse(exchange, subtask, 200);
        }

        private void deleteTaskById(HttpExchange exchange) throws IOException {
            String[] query = exchange.getRequestURI().getQuery().split("=");
            String[] id = query[1].split(",");
            for (String param : id) {
                int intId = Integer.parseInt(param);
                fm.deleteTask(intId);
            }
            writeResponse(exchange, "", 200);
        }

        private void deleteEpicById(HttpExchange exchange) throws IOException {
            String[] query = exchange.getRequestURI().getQuery().split("=");
            String[] id = query[1].split(",");
            for (String param : id) {
                int intId = Integer.parseInt(param);
                fm.deleteEpic(intId);
            }
            writeResponse(exchange, "", 200);
        }

        private void deleteSubtaskById(HttpExchange exchange) throws IOException {
            String[] query = exchange.getRequestURI().getQuery().split("=");
            String[] id = query[1].split(",");
            for (String param : id) {
                int intId = Integer.parseInt(param);
                fm.deleteSubtask(intId);
            }
            writeResponse(exchange, "", 200);
        }

        private void deleteAll(HttpExchange exchange) throws IOException {
            fm.clearTasks();
            writeResponse(exchange, "", 200);
        }

        private void getHistory(HttpExchange exchange) throws IOException {
            String history = gson.toJson(fm.getHistory());
            writeResponse(exchange, history,200);
        }

        private void getPrioritizedTasks(HttpExchange exchange) throws IOException {
            String list = gson.toJson(fm.getPrioritizedTasks());
            writeResponse(exchange, list, 200);
        }
    }
