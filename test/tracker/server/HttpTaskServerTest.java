package tracker.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tracker.constants.Status;
import tracker.interfaces.TaskManager;
import tracker.managers.Managers;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.server.handlers.BaseHttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class HttpTaskServerTest {

    protected final int PORT = 8080;
    protected TaskManager taskManager;
    protected HttpTaskServer server;
    protected Gson gson;

    protected Task task;
    protected Task anotherTask;
    protected Epic epic;
    protected Epic anotherEpic;
    protected Subtask subtask;
    protected Subtask anotherSubtask;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        server = new HttpTaskServer(taskManager);
        gson = BaseHttpHandler.createGson();

        task = new Task(1, "прогулка", "ходьба в парке", Status.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 14, 10, 0));
        anotherTask = new Task(2, "другая задача", "другое описание", Status.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 15, 10, 0));
        epic = new Epic(3, "переезд", "смена места жительства");
        subtask = new Subtask(3, 4, "сборка", "упаковать вещи", Status.IN_PROGRESS,
                Duration.ofMinutes(90), LocalDateTime.now());
        anotherSubtask = new Subtask(3, 5, "разбор", "разобрать вещи", Status.DONE,
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(3));
        anotherEpic = new Epic(6, "название эпика", "описание эпика");


        taskManager.addTask(task);
        taskManager.addTask(anotherTask);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(anotherSubtask);
        taskManager.addEpic(anotherEpic);

        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
        taskManager.clearAll();
    }

    protected HttpResponse<String> sendRequest(String url, String method, String body)
            throws IOException, InterruptedException {

        if (url == null) {
            throw new IllegalArgumentException("URL не может быть null");
        }
        if (method == null) {
            throw new IllegalArgumentException("Метод не может быть null");
        }

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method(method,
                            body != null ? HttpRequest.BodyPublishers.ofString(body)
                                    : HttpRequest.BodyPublishers.noBody())
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    protected String getBaseUrl() {
        return "http://localhost:" + PORT;
    }

    protected String getTasksUrl() {
        return getBaseUrl() + "/tasks";
    }

    protected String getEpicsUrl() {
        return getBaseUrl() + "/epics";
    }

    protected String getSubtasksUrl() {
        return getBaseUrl() + "/subtasks";
    }

    protected String getHistoryUrl() {
        return getBaseUrl() + "/history";
    }

    protected String getPrioritizedUrl() {
        return getBaseUrl() + "/prioritized";
    }
}