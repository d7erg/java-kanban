package tracker.server.handlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import tracker.model.Task;
import tracker.server.HttpTaskServerTest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrioritizedHandlerTest extends HttpTaskServerTest {

    @Test
    void shouldGetEmptyPrioritizedTasks() throws IOException, InterruptedException {

        taskManager.clearAll();

        HttpResponse<String> response = sendRequest(getPrioritizedUrl(), "GET", null);
        String responseBody = response.body();
        List<Task> prioritized = gson.fromJson(responseBody, new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");
        assertTrue(prioritized.isEmpty(), "Список не должен содержать элементы");
    }

    @Test
    void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest(getPrioritizedUrl(), "GET", null);
        String responseBody = response.body();
        List<Task> prioritized = gson.fromJson(responseBody, new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");
        assertFalse(prioritized.isEmpty(), "Список должен содержать элементы");

        prioritized.forEach(task -> {
            assertNotNull(task.getTitle(), "Имя не должно быть пустым");
            assertNotNull(task.getDescription(), "Описание не должно быть пустым");
        });
    }

    @Test
    void shouldGetInvalidRequestPrioritized() throws IOException, InterruptedException {
        String url = getBaseUrl() + "/invalid";
        HttpResponse<String> response = sendRequest(url, "GET", null);

        assertEquals(404, response.statusCode(),
                "При несуществующем пути запроса должен быть статус 400");

        HttpResponse<String> responseAfter = sendRequest(url, "GET", null);

        assertEquals(404, responseAfter.statusCode(),
                "При повторном запросе с несуществующим путём должен быть статус 404");
    }
}