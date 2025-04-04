package tracker.server.handlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import tracker.model.Task;
import tracker.server.HttpTaskServerTest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest extends HttpTaskServerTest {


    @Test
    void shouldGetEmptyHistory() throws IOException, InterruptedException {

        HttpResponse<String> response = sendRequest(getHistoryUrl(), "GET", null);
        String responseBody = response.body();
        List<Task> history = gson.fromJson(responseBody, new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");
        assertTrue(history.isEmpty(), "История не должна содержать элементы");
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {

        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());

        HttpResponse<String> response = sendRequest(getHistoryUrl(), "GET", null);
        String responseBody = response.body();
        List<Task> history = gson.fromJson(responseBody, new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");
        assertFalse(history.isEmpty(), "История должна содержать элементы");

        history.forEach(task -> {
            assertNotNull(task.getTitle(), "Имя не должно быть пустым");
            assertNotNull(task.getDescription(), "Описание не должно быть пустым");
        });
    }

    @Test
    void shouldGetInvalidRequestHistory() throws IOException, InterruptedException {
        String url = getBaseUrl() + "/invalid";
        HttpResponse<String> response = sendRequest(url, "GET", null);

        assertEquals(404, response.statusCode(),
                "При несуществующем пути запроса должен быть статус 400");

        HttpResponse<String> responseAfter = sendRequest(url, "GET", null);

        assertEquals(404, responseAfter.statusCode(),
                "При повторном запросе с несуществующим путём должен быть статус 404");
    }

}