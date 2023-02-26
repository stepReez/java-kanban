package service.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Key;

public class KVTaskClient {
    HttpRequest request;
    HttpClient client;
    HttpResponse.BodyHandler<String> handler;
    String url;
    public String token;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();
        this.url = url;
        URI register = URI.create(url + "register");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(register)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, handler);
        String tokenApi = "";
        if (response.statusCode() == 200) {
            JsonElement jsonElement = JsonParser.parseString(response.body());
            tokenApi = jsonElement.getAsString();
        }
        token = tokenApi;
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        String save = url + "save/" + key + "?API_TOKEN=" + token;

        request = HttpRequest.newBuilder()
                .uri(URI.create(save))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.send(request, handler);
    }

    public String load(String key) {
        String load = url + "load/" + key + "?API_TOKEN=" + token;
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(load))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        try {
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            return null;
        }
    }
}
