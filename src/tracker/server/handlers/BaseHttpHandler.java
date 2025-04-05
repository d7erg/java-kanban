package tracker.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import tracker.interfaces.TaskManager;
import tracker.server.handlers.adapters.DurationAdapter;
import tracker.server.handlers.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler extends AbstractRequestHandler {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = createGson();
    }

    public static Gson createGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }


    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    protected void sendJson(HttpExchange h, Object data, int responseCode) throws IOException {
        String json = gson.toJson(data);
        byte[] resp = json.getBytes(DEFAULT_CHARSET);

        h.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(responseCode, resp.length);

        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendBadRequest(HttpExchange h) throws IOException {
        h.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(400, 0);

        try (OutputStream os = h.getResponseBody()) {
            String errorMessage = "{\"error\": \"Некорректный запрос\"}";
            os.write(errorMessage.getBytes(StandardCharsets.UTF_8));
        }
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, 0);

        try (h; OutputStream os = h.getResponseBody()) {
            String errorMessage = "{\"error\": \"Ресурс не найден\"}";
            os.write(errorMessage.getBytes(DEFAULT_CHARSET));
        }
    }

    protected void sendIntersection(HttpExchange h) throws IOException {
        h.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(409, 0);

        try (OutputStream os = h.getResponseBody()) {
            String errorMessage = "{\"error\": \"Пересечение по времени\"}";
            os.write(errorMessage.getBytes(DEFAULT_CHARSET));
        }
    }

    protected void sendInternalError(HttpExchange h) throws IOException {
        h.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(500, 0);

        try (OutputStream os = h.getResponseBody()) {
            String errorMessage = "{\"error\": \"Внутренняя ошибка сервера\"}";
            os.write(errorMessage.getBytes(DEFAULT_CHARSET));
        }
    }
}
