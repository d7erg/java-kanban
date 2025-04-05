package tracker.server.handlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import tracker.constants.Status;
import tracker.exceptions.NotFoundException;
import tracker.model.Task;
import tracker.server.HttpTaskServerTest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TasksHandlerTest extends HttpTaskServerTest {

    @Test
    void shouldGetAllTasksEmpty() throws IOException, InterruptedException {
        taskManager.deleteTasks();

        HttpResponse<String> response = sendRequest(getTasksUrl(), "GET", null);
        String responseBody = response.body();
        List<Task> tasks = gson.fromJson(responseBody, new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");
        assertEquals(0, tasks.size(), "Список задач должен быть пустым");
        assertTrue(tasks.isEmpty(), "Список задач должен быть пустым");
    }


    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest(getTasksUrl(), "GET", null);
        String responseBody = response.body();
        List<Task> tasks = gson.fromJson(responseBody, new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");
        assertEquals(2, tasks.size(), "Неверное количество задач");
        assertTrue(tasks.contains(task), "Задача отсутствует в списке");
        assertTrue(tasks.contains(anotherTask), "Другая задача отсутствует в списке");

        tasks.forEach(t -> {
            assertNotNull(t.getTitle(), "Имя задачи не должно быть пустым");
            assertNotNull(t.getDescription(), "Описание задачи не должно быть пустым");
            assertNotNull(t.getStartTime(), "Дата начала не должна быть пустой");
        });
    }

    @Test
    void shouldGetSingleTask() throws IOException, InterruptedException {
        String url = getTasksUrl() + "/" + task.getId();
        HttpResponse<String> response = sendRequest(url, "GET", null);
        String responseBody = response.body();
        Task responseTask = gson.fromJson(responseBody, Task.class);

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");

        assertNotNull(responseTask, "Задача не должна быть null");
        assertEquals(task.getId(), responseTask.getId(), "ID задачи не совпадает");
        assertEquals(task.getTitle(), responseTask.getTitle(), "Название задачи не совпадает");
        assertEquals(task.getDescription(), responseTask.getDescription(), "Описание задачи не совпадает");
        assertEquals(task.getStatus(), responseTask.getStatus(), "Статус задачи не совпадает");
        assertEquals(task.getStartTime(), responseTask.getStartTime(), "Время начала задачи не совпадает");
        assertEquals(task.getDuration(), responseTask.getDuration(), "Длительность задачи не совпадает");
    }

    @Test
    void shouldCreateTask() throws IOException, InterruptedException {
        Task newTask = new Task("новая задача", "описание новой задачи", Status.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 16, 10, 0));

        String requestBody = gson.toJson(newTask);
        HttpResponse<String> response = sendRequest(getTasksUrl(), "POST", requestBody);
        String responseBody = response.body();

        assertEquals(201, response.statusCode(), "Неверный HTTP-статус");

        Task createdTask = gson.fromJson(responseBody, Task.class);

        assertNotNull(createdTask, "Созданная задача не должна быть null");
        assertTrue(createdTask.getId() > 0, "ID созданной задачи должен быть больше 0");
        assertEquals(newTask.getTitle(), createdTask.getTitle(), "Название задачи не совпадает");
        assertEquals(newTask.getDescription(), createdTask.getDescription(), "Описание задачи не совпадает");
        assertEquals(newTask.getStatus(), createdTask.getStatus(), "Статус задачи не совпадает");
        assertEquals(newTask.getStartTime(), createdTask.getStartTime(), "Время начала задачи не совпадает");
        assertEquals(newTask.getDuration(), createdTask.getDuration(), "Длительность задачи не совпадает");
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {

        Task updatedTask = new Task(task.getId(), "Обновленная задача",
                "Новое описание", Status.DONE,
                Duration.ofMinutes(120), LocalDateTime.of(2025, 3, 13, 10, 0));

        String requestBody = gson.toJson(updatedTask);
        HttpResponse<String> response = sendRequest(getTasksUrl(), "POST", requestBody);
        String responseBody = response.body();

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");

        Task resultTask = gson.fromJson(responseBody, Task.class);

        assertNotNull(resultTask, "Обновленная задача не должна быть null");
        assertEquals(updatedTask.getId(), resultTask.getId(), "ID задачи не совпадает");
        assertEquals(updatedTask.getTitle(), resultTask.getTitle(), "Название задачи не совпадает");
        assertEquals(updatedTask.getDescription(), resultTask.getDescription(), "Описание задачи не совпадает");
        assertEquals(updatedTask.getStatus(), resultTask.getStatus(), "Статус задачи не совпадает");
        assertEquals(updatedTask.getStartTime(), resultTask.getStartTime(), "Время начала задачи не совпадает");
        assertEquals(updatedTask.getDuration(), resultTask.getDuration(), "Длительность задачи не совпадает");
    }

    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        String url = getTasksUrl() + "/" + task.getId();
        HttpResponse<String> response = sendRequest(url, "DELETE", null);

        assertEquals(200, response.statusCode(), "Неверный HTTP-статус");

        try {
            taskManager.getTask(task.getId());
            fail("Должна быть выброшена NotFoundException");
        } catch (NotFoundException e) {
            // Ожидаемое поведение
        }

        HttpResponse<String> responseAfterDelete = sendRequest(url, "GET", null);
        assertEquals(404, responseAfterDelete.statusCode(),
                "При получении удаленной задачи должен быть статус 404");
    }

    // Проверка исключений

    @Test
    void shouldDeleteNonExistingTask() throws IOException, InterruptedException {
        String url = getTasksUrl() + "/999";
        HttpResponse<String> response = sendRequest(url, "DELETE", null);

        assertEquals(404, response.statusCode(),
                "При попытке удалить несуществующую задачу должен быть статус 404");

        String responseBody = response.body();
        assertTrue(responseBody.contains("\"error\": \"Ресурс не найден\""),
                "Ожидалось сообщение об ошибке 'Ресурс не найден'");

        HttpResponse<String> responseAfterDelete = sendRequest(url, "GET", null);

        assertEquals(404, responseAfterDelete.statusCode(),
                "При получении несуществующей задачи должен быть статус 404");
    }

    @Test
    void shouldGetInvalidTaskId() throws IOException, InterruptedException {
        String url = getTasksUrl() + "/id";
        HttpResponse<String> response = sendRequest(url, "GET", null);

        assertEquals(400, response.statusCode(),
                "При некорректном ID задачи должен быть статус 400");

        String responseBody = response.body();
        assertTrue(responseBody.contains("\"error\": \"Некорректный запрос\""),
                "Ожидалось сообщение об ошибке 'Некорректный запрос'");

        HttpResponse<String> responseAfter = sendRequest(url, "GET", null);

        assertEquals(400, responseAfter.statusCode(),
                "При повторном запросе с некорректным ID должен быть статус 400");
    }

    @Test
    void shouldPostInvalidTaskWithEmptyBody() throws IOException, InterruptedException {
        String url = getTasksUrl();
        HttpResponse<String> response = sendRequest(url, "POST", null);

        assertEquals(400, response.statusCode(),
                "При некорректной задаче должен быть статус 400");

        String responseBody = response.body();
        assertTrue(responseBody.contains("\"error\": \"Некорректный запрос\""),
                "Ожидалось сообщение об ошибке 'Некорректный запрос'");

        HttpResponse<String> responseAfter = sendRequest(url, "POST", null);

        assertEquals(400, responseAfter.statusCode(),
                "При повторном запросе с некорректной задачей должен быть статус 400");
    }


    @Test
    void shouldUpdateTaskWithIntersection() throws IOException, InterruptedException {
        // Создаем вторую задачу с пересекающимся временем
        Task updateTask = new Task(task.getId(), "прогулка", "ходьба в парке", Status.IN_PROGRESS,
                Duration.ofHours(2), LocalDateTime.of(2025, 3, 14, 10, 0)
        );

        String requestBody = gson.toJson(updateTask);

        HttpResponse<String> response = sendRequest(getTasksUrl(), "POST", requestBody);

        assertEquals(409, response.statusCode(), "Должен быть статус 409 при пересечении задач");

        String responseBody = response.body();
        assertTrue(responseBody.contains("\"error\": \"Пересечение по времени\""),
                "Ожидалось сообщение об ошибке 'Пересечение по времени'");
    }

}