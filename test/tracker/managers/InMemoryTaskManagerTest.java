package tracker.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.constants.Status;
import tracker.interfaces.TaskManagerTest;
import tracker.model.Subtask;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void shouldUpdateSubtaskAndChangeEpicStatus() {
        tm.addEpic(epic);
        tm.addSubtask(subtask);
        assertEquals(subtask.getStatus(), tm.getEpic(epic.getId()).getStatus(),
                "Статус эпика должен быть как у подзадачи");

        Subtask newSubtask = new Subtask(1, 2, "новая подзадача", "описание новой подзадачи",
                Status.DONE,
                Duration.ofMinutes(90),
                LocalDateTime.now().plusHours(2));

        tm.updateSubtask(newSubtask);

        assertEquals(subtask.getId(), tm.getEpicSubtasks(epic.getId()).getFirst().getId(),
                "ID подзадач должны совпадать");
        assertEquals(tm.getSubtask(subtask.getId()).getStatus(),
                tm.getEpicSubtasks(epic.getId()).getFirst().getStatus(), "Статусы должны совпадать");
        assertEquals(tm.getEpicSubtasks(epic.getId()).getFirst().getStatus(), tm.getEpic(epic.getId()).getStatus(),
                "Статус эпика должен быть как у подзадачи");
    }

    @Test
    public void shouldReturnUpdatedEpicStatus() {
        tm.addEpic(epic);
        tm.addSubtask(subtask);
        assertNotNull(tm.getEpic(epic.getId()).getStatus(), "Статус не должен быть пуст");
        assertEquals(tm.getSubtask(subtask.getId()).getStatus(), tm.getEpic(epic.getId()).getStatus(),
                "Статусы должны быть одинаковыми");
    }

    @Test
    public void shouldNotSaveDeletedSubtasksID() {
        tm.addEpic(epic);
        tm.addSubtask(subtask);
        tm.addSubtask(anotherSubtask);
        tm.deleteSubtaskById(anotherSubtask.getId());
        assertEquals(1, tm.getEpic(epic.getId()).getSubtasksIds().size(),
                "Количество ID подзадач не равен ожидаемому");
        tm.deleteSubtasks();
        assertEquals(0, tm.getEpic(1).getSubtasksIds().size(),
                "Список ID подзадач должен быть пуст");
    }

    @Test
    void testIntervalIntersection() {
        // ========== Проверка непересекающихся интервалов ==========

        // Установлены разные дни: в приоритет добавлено две задачи
        tm.addTask(new Task(1, "задача", "описание", Status.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 14, 14, 0)));
        tm.addTask(new Task(2, "другая задача", "другое описание", Status.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 15, 15, 0)));

        assertTrue(tm.getPrioritizedTasks().containsAll(Arrays.asList(tm.getTask(1), tm.getTask(2))));
        tm.deleteTasks();

        // Установлено разное время: в приоритет добавлено две задачи
        tm.addTask(new Task(3, "утренняя задача", "описание", Status.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 15, 16, 0)));
        tm.addTask(new Task(4, "вечерняя задача", "другое описание", Status.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 15, 17, 0)));

        assertTrue(tm.getPrioritizedTasks().containsAll(Arrays.asList(tm.getTask(3), tm.getTask(4))));
        tm.deleteTasks();

        // Задача без времени начала не добавлена
        tm.addTask(new Task(5, "задача", "описание", Status.IN_PROGRESS,
                Duration.ofMinutes(60), null));

        assertFalse(tm.getPrioritizedTasks().contains(tm.getTask(5)));
    }
}