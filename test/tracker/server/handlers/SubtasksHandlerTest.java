package tracker.server.handlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import tracker.constants.Status;
import tracker.exceptions.NotFoundException;
import tracker.model.Subtask;
import tracker.server.HttpTaskServerTest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtasksHandlerTest extends HttpTaskServerTest {

    @Test
    void shouldGetAllSubtasksEmpty() throws IOException, InterruptedException {
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();

        HttpResponse<String> response = sendRequest(getSubtasksUrl(), "GET", null);
        String responseBody = response.body();
        List<Subtask> subtasks = gson.fromJson(responseBody, new TypeToken<List<Subtask>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");
        assertEquals(0, subtasks.size(), "Список подзадач должен быть пустым");
        assertTrue(subtasks.isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void shouldGetAllSubtasks() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest(getSubtasksUrl(), "GET", null);
        String responseBody = response.body();
        List<Subtask> subtasks = gson.fromJson(responseBody, new TypeToken<List<Subtask>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач");
        assertTrue(subtasks.contains(subtask), "Подзадача отсутствует в списке");
        assertTrue(subtasks.contains(anotherSubtask), "Другая подзадача отсутствует в списке");

        subtasks.forEach(s -> {
            assertNotNull(s.getTitle(), "Имя подзадачи не должно быть пустым");
            assertNotNull(s.getDescription(), "Описание подзадачи не должно быть пустым");
            assertNotNull(s.getStartTime(), "Дата начала не должна быть пустой");
        });
    }

    @Test
    void shouldGetSingleSubtask() throws IOException, InterruptedException {
        String url = getSubtasksUrl() + "/" + subtask.getId();
        HttpResponse<String> response = sendRequest(url, "GET", null);
        String responseBody = response.body();
        Subtask responseSubtask = gson.fromJson(responseBody, Subtask.class);

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");

        assertNotNull(responseSubtask, "Подзадача не должна быть null");
        assertEquals(subtask.getId(), responseSubtask.getId(), "ID подзадачи не совпадает");
        assertEquals(subtask.getTitle(), responseSubtask.getTitle(), "Название подзадачи не совпадает");
        assertEquals(subtask.getDescription(), responseSubtask.getDescription(),
                "Описание подзадачи не совпадает");
        assertEquals(subtask.getStatus(), responseSubtask.getStatus(), "Статус подзадачи не совпадает");
        assertEquals(subtask.getStartTime(), responseSubtask.getStartTime(),
                "Время начала подзадачи не совпадает");
        assertEquals(subtask.getDuration(), responseSubtask.getDuration(),
                "Длительность подзадачи не совпадает");
    }

    @Test
    void shouldCreateSubtaskForEpic() throws IOException, InterruptedException {

        Subtask newSubtask = new Subtask(
                anotherEpic.getId(), "новая подзадача", "описание новой подзадачи",
                Status.IN_PROGRESS,
                Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 20, 10, 0));

        String subtaskRequestBody = gson.toJson(newSubtask);
        HttpResponse<String> subtaskResponse = sendRequest(getSubtasksUrl(), "POST", subtaskRequestBody);
        Subtask createdSubtask = gson.fromJson(subtaskResponse.body(), Subtask.class);

        assertEquals(201, subtaskResponse.statusCode(), "Неверный HTTP-статус для создания подзадачи");
        assertNotNull(createdSubtask, "Созданная подзадача не должна быть null");
        assertTrue(createdSubtask.getId() > 0, "ID созданной подзадачи должен быть больше 0");
        assertEquals(newSubtask.getTitle(), createdSubtask.getTitle(), "Название подзадачи не совпадает");
        assertEquals(newSubtask.getDescription(), createdSubtask.getDescription(),
                "Описание подзадачи не совпадает");
        assertEquals(anotherEpic.getId(), createdSubtask.getEpicId(), "ID связанного эпика не совпадает");
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {

        Subtask updatedSubtask = new Subtask(epic.getId(), epic.getSubtasksIds().getFirst(),
                "обновленная подзадача",
                "обновленное описание",
                Status.DONE,
                Duration.ofMinutes(90),
                LocalDateTime.of(2025, 3, 14, 12, 0));

        String updatedSubtaskRequestBody = gson.toJson(updatedSubtask);
        HttpResponse<String> updateResponse = sendRequest(getSubtasksUrl(), "POST", updatedSubtaskRequestBody);
        Subtask updatedCreatedSubtask = gson.fromJson(updateResponse.body(), Subtask.class);

        assertEquals(200, updateResponse.statusCode(),
                "Неверный HTTP-статус для обновления подзадачи");
        assertNotNull(updatedCreatedSubtask, "Обновленная подзадача не должна быть null");
        assertEquals(updatedSubtask.getTitle(), updatedCreatedSubtask.getTitle(),
                "Название подзадачи не совпадает");
        assertEquals(updatedSubtask.getDescription(), updatedCreatedSubtask.getDescription(),
                "Описание подзадачи не совпадает");
        assertEquals(updatedSubtask.getStatus(), updatedCreatedSubtask.getStatus(),
                "Статус подзадачи не совпадает");
        assertEquals(updatedSubtask.getEpicId(), updatedCreatedSubtask.getEpicId(),
                "ID связанного эпика не совпадает");
    }

    @Test
    void shouldDeleteSubtask() throws IOException, InterruptedException {
        String url = getSubtasksUrl() + "/" + subtask.getId();
        HttpResponse<String> response = sendRequest(url, "DELETE", null);

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");

        try {
            taskManager.getSubtask(subtask.getId());
            fail("Должна быть выброшена NotFoundException");
        } catch (NotFoundException e) {
            // Ожидаемое поведение
        }

        HttpResponse<String> responseAfterDelete = sendRequest(url, "GET", null);
        assertEquals(404, responseAfterDelete.statusCode(),
                "При получении удаленной подзадачи должен быть статус 404");
    }

    // Проверка исключений

    @Test
    void shouldDeleteNonExistingSubtask() throws IOException, InterruptedException {
        String url = getSubtasksUrl() + "/999";
        HttpResponse<String> response = sendRequest(url, "DELETE", null);

        assertEquals(404, response.statusCode(),
                "При попытке удалить несуществующую подзадачу должен быть статус 404");

        String responseBody = response.body();
        assertTrue(responseBody.contains("\"error\": \"Ресурс не найден\""),
                "Ожидалось сообщение об ошибке 'Ресурс не найден'");

        HttpResponse<String> responseAfterDelete = sendRequest(url, "GET", null);

        assertEquals(404, responseAfterDelete.statusCode(),
                "При получении несуществующей подзадачи должен быть статус 404");
    }

    @Test
    void shouldGetInvalidSubtaskId() throws IOException, InterruptedException {
        String url = getSubtasksUrl() + "/id";
        HttpResponse<String> response = sendRequest(url, "GET", null);

        assertEquals(400, response.statusCode(),
                "При некорректном ID подзадачи должен быть статус 400");

        String responseBody = response.body();
        assertTrue(responseBody.contains("\"error\": \"Некорректный запрос\""),
                "Ожидалось сообщение об ошибке 'Некорректный запрос'");

        HttpResponse<String> responseAfter = sendRequest(url, "GET", null);

        assertEquals(400, responseAfter.statusCode(),
                "При повторном запросе с некорректным ID должен быть статус 400");
    }

    @Test
    void shouldPostInvalidSubtaskWithEmptyBody() throws IOException, InterruptedException {
        String url = getSubtasksUrl();
        HttpResponse<String> response = sendRequest(url, "POST", null);

        assertEquals(400, response.statusCode(),
                "При некорректной подзадаче должен быть статус 400");

        String responseBody = response.body();
        assertTrue(responseBody.contains("\"error\": \"Некорректный запрос\""),
                "Ожидалось сообщение об ошибке 'Некорректный запрос'");

        HttpResponse<String> responseAfter = sendRequest(url, "POST", null);

        assertEquals(400, responseAfter.statusCode(),
                "При повторном запросе с некорректной подзадачей должен быть статус 400");
    }

    @Test
    void shouldUpdateSubtaskWithIntersection() throws IOException, InterruptedException {
        // Создаем вторую подзадачу с пересекающимся временем
        Subtask updateSubtask = new Subtask(epic.getId(), epic.getSubtasksIds().getFirst(),
                "обновленная подзадача",
                "обновленное описание",
                Status.DONE,
                Duration.ofMinutes(90),
                LocalDateTime.now().plusMinutes(30)
        );

        String requestBody = gson.toJson(updateSubtask);

        HttpResponse<String> response = sendRequest(getTasksUrl(), "POST", requestBody);

        assertEquals(409, response.statusCode(), "Должен быть статус 409 при пересечении задач");

        String responseBody = response.body();
        assertTrue(responseBody.contains("\"error\": \"Пересечение по времени\""),
                "Ожидалось сообщение об ошибке 'Пересечение по времени'");
    }

}