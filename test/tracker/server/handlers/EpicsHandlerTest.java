package tracker.server.handlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import tracker.exceptions.NotFoundException;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.server.HttpTaskServerTest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicsHandlerTest extends HttpTaskServerTest {

    @Test
    void shouldGetAllEpicsEmpty() throws IOException, InterruptedException {
        taskManager.deleteEpics();

        HttpResponse<String> response = sendRequest(getEpicsUrl(), "GET", null);
        String responseBody = response.body();
        List<Epic> epics = gson.fromJson(responseBody, new TypeToken<List<Epic>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");
        assertEquals(0, epics.size(), "Список эпиков должен быть пустым");
        assertTrue(epics.isEmpty(), "Список эпиков должен быть пустым");
    }

    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest(getEpicsUrl(), "GET", null);
        String responseBody = response.body();
        List<Epic> epics = gson.fromJson(responseBody, new TypeToken<List<Epic>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");
        assertEquals(2, epics.size(), "Неверное количество эпиков");
        assertTrue(epics.contains(epic), "Эпик отсутствует в списке");
        assertTrue(epics.contains(anotherEpic), "Другой эпик отсутствует в списке");

        epics.forEach(e -> {
            assertNotNull(e.getTitle(), "Имя эпика не должно быть пустым");
            assertNotNull(e.getDescription(), "Описание эпика не должно быть пустым");
        });
    }


    @Test
    void shouldGetSingleEpic() throws IOException, InterruptedException {
        String url = getEpicsUrl() + "/" + epic.getId();
        HttpResponse<String> response = sendRequest(url, "GET", null);
        String responseBody = response.body();
        Epic responseEpic = gson.fromJson(responseBody, Epic.class);

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");

        assertNotNull(responseEpic, "Эпик не должен быть null");
        assertEquals(epic.getId(), responseEpic.getId(), "ID эпика не совпадает");
        assertEquals(epic.getTitle(), responseEpic.getTitle(), "Название эпика не совпадает");
        assertEquals(epic.getDescription(), responseEpic.getDescription(), "Описание эпика не совпадает");
        assertEquals(epic.getStatus(), responseEpic.getStatus(), "Статус эпика не совпадает");
        assertEquals(epic.getStartTime(), responseEpic.getStartTime(), "Время начала эпика не совпадает");
        assertEquals(epic.getDuration(), responseEpic.getDuration(), "Длительность эпика не совпадает");
    }

    @Test
    void shouldGetEpicSubtasks() throws IOException, InterruptedException {
        HttpResponse<String> epicResponse = sendRequest(getEpicsUrl(), "GET", null);
        List<Epic> epics = gson.fromJson(epicResponse.body(), new TypeToken<List<Epic>>() {
        }.getType());
        Epic existingEpic = epics.getFirst();

        String epicSubtasksUrl = getEpicsUrl() + "/" + existingEpic.getId() + "/subtasks";
        HttpResponse<String> response = sendRequest(epicSubtasksUrl, "GET", null);
        String responseBody = response.body();
        List<Subtask> subtasks = gson.fromJson(responseBody, new TypeToken<List<Subtask>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");
        assertFalse(subtasks.isEmpty(), "Список подзадач должен содержать элементы");

        subtasks.forEach(subtask -> {
            assertTrue(subtask.getId() > 0, "ID подзадачи должен быть больше нуля");
            assertNotNull(subtask.getTitle(), "Название подзадачи не должно быть пустым");
            assertNotNull(subtask.getStatus(), "Статус подзадачи не должен быть пустым");
            assertTrue(subtask.getEpicId() > 0, "ID связанного эпика должен быть больше нуля");
            assertEquals(existingEpic.getId(), subtask.getEpicId(), "ID связанного эпика должен совпадать");
        });
    }

    @Test
    void shouldCreateEpic() throws IOException, InterruptedException {
        Epic newEpic = new Epic("новый эпик", "описание нового эпика");

        String requestBody = gson.toJson(newEpic);
        HttpResponse<String> response = sendRequest(getEpicsUrl(), "POST", requestBody);
        String responseBody = response.body();

        assertEquals(201, response.statusCode(), "Неверный HTTP-статус");

        Epic createdEpic = gson.fromJson(responseBody, Epic.class);

        assertNotNull(createdEpic, "Созданный эпик не должен быть null");
        assertTrue(createdEpic.getId() > 0, "ID созданного эпика должен быть больше 0");
        assertEquals(newEpic.getTitle(), createdEpic.getTitle(), "Название эпика не совпадает");
        assertEquals(newEpic.getDescription(), createdEpic.getDescription(), "Описание эпика не совпадает");
        assertNotEquals(newEpic.getStatus(), createdEpic.getStatus(), "Статус нового эпика 'NEW'");
        assertEquals(newEpic.getStartTime(), createdEpic.getStartTime(), "Время начала эпика не совпадает");
        assertEquals(newEpic.getDuration(), createdEpic.getDuration(), "Длительность эпика не совпадает");
    }

    @Test
    void shouldDeleteEpic() throws IOException, InterruptedException {
        String url = getEpicsUrl() + "/" + epic.getId();
        HttpResponse<String> response = sendRequest(url, "DELETE", null);

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");

        try {
            taskManager.getEpic(epic.getId());
            fail("Должна быть выброшена NotFoundException");
        } catch (NotFoundException e) {
            // Ожидаемое поведение
        }

        HttpResponse<String> responseAfterDelete = sendRequest(url, "GET", null);
        assertEquals(404, responseAfterDelete.statusCode(),
                "При получении удаленного эпика должен быть статус 404");
    }

    // Проверка исключений

    @Test
    void shouldDeleteNonExistingEpic() throws IOException, InterruptedException {
        String url = getEpicsUrl() + "/999";
        HttpResponse<String> response = sendRequest(url, "DELETE", null);

        assertEquals(404, response.statusCode(),
                "При попытке удалить несуществующий эпик должен быть статус 404");

        String responseBody = response.body();
        assertTrue(responseBody.contains("\"error\": \"Ресурс не найден\""),
                "Ожидалось сообщение об ошибке 'Ресурс не найден'");

        HttpResponse<String> responseAfterDelete = sendRequest(url, "GET", null);

        assertEquals(404, responseAfterDelete.statusCode(),
                "При получении несуществующего эпика должен быть статус 404");
    }

    @Test
    void shouldGetInvalidEpicId() throws IOException, InterruptedException {
        String url = getEpicsUrl() + "/id";
        HttpResponse<String> response = sendRequest(url, "GET", null);

        assertEquals(400, response.statusCode(),
                "При некорректном ID эпика должен быть статус 400");

        String responseBody = response.body();
        assertTrue(responseBody.contains("\"error\": \"Некорректный запрос\""),
                "Ожидалось сообщение об ошибке 'Некорректный запрос'");

        HttpResponse<String> responseAfter = sendRequest(url, "GET", null);

        assertEquals(400, responseAfter.statusCode(),
                "При повторном запросе с некорректным ID должен быть статус 400");
    }

    @Test
    void shouldPostInvalidEpicWithEmptyBody() throws IOException, InterruptedException {
        String url = getEpicsUrl();
        HttpResponse<String> response = sendRequest(url, "POST", null);

        assertEquals(400, response.statusCode(),
                "При некорректном эпике должен быть статус 400");

        String responseBody = response.body();
        assertTrue(responseBody.contains("\"error\": \"Некорректный запрос\""),
                "Ожидалось сообщение об ошибке 'Некорректный запрос'");

        HttpResponse<String> responseAfter = sendRequest(url, "POST", null);

        assertEquals(400, responseAfter.statusCode(),
                "При повторном запросе с некорректным эпиком должен быть статус 400");
    }

}