package tracker.interfaces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.constants.Status;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T tm;

    protected abstract T createTaskManager();

    protected Task task;
    protected Task anotherTask;
    protected Epic epic;
    protected Epic anotherEpic;
    protected Subtask subtask;
    protected Subtask anotherSubtask;


    @BeforeEach
    public void setUp() {
        tm = createTaskManager();

        task = new Task(1, "прогулка", "ходьба в парке", Status.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 14, 10, 0));
        anotherTask = new Task(2, "другая задача", "другое описание", Status.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 15, 10, 0));
        epic = new Epic(3, "переезд", "смена места жительства");
        subtask = new Subtask(1, 2, "сборка", "упаковать вещи", Status.IN_PROGRESS,
                Duration.ofMinutes(90), LocalDateTime.now());
        anotherSubtask = new Subtask(1, 3, "разбор", "разобрать вещи", Status.DONE,
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(3));
        anotherEpic = new Epic(2, "название эпика", "описание эпика");
    }

    // Тесты по задачам
    @Test
    void shouldAddAndGetTaskAndTasks() {
        tm.addTask(task);
        Task retrievedTask = tm.getTask(task.getId());
        assertNotNull(retrievedTask, "Задача не найдена");
        assertEquals(task, retrievedTask, "Задачи не совпадают");

        tm.addTask(anotherTask);
        List<Task> tasks = tm.getTasks();
        assertNotNull(tasks, "Задачи не найдены");
        assertEquals(2, tasks.size(), "Неверное количество задач");
    }

    @Test
    public void shouldUnchangedFieldsWhenAddTask() {
        tm.addTask(task);
        Task retrievedTask = tm.getTask(task.getId());
        assertEquals(task.getId(), retrievedTask.getId(), "ID не совпадает");
        assertEquals(task.getTitle(), retrievedTask.getTitle(), "Название не совпадает");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Описание не совпадает");
        assertEquals(task.getStatus(), retrievedTask.getStatus(), "Status не совпадает");
    }

    @Test
    void shouldUpdateTask() {
        tm.addTask(task);

        Task newTask = new Task(task.getId(), "новая прогулка", "прогулка в другом парке",
                Status.IN_PROGRESS,
                Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 14, 12, 0));

        tm.updateTask(newTask);

        assertEquals("новая прогулка", tm.getTask(task.getId()).getTitle(), "название не совпадает");
        assertEquals("прогулка в другом парке", tm.getTask(task.getId()).getDescription(),
                "описание не совпадает");
        assertEquals(1, tm.getTasks().size(), "Количество задач не совпадает");
        assertEquals(1, tm.getPrioritizedTasks().size(), "Количество задач не совпадает");
        assertTrue(tm.getPrioritizedTasks().contains(newTask));
    }

    @Test
    void shouldDeleteTasks() {
        tm.addTask(task);
        tm.addTask(anotherTask);
        tm.deleteTasks();
        assertEquals(0, tm.getTasks().size(), "Количество задач не совпадает");
    }

    @Test
    public void shouldDeleteTaskByID() {
        tm.addTask(task);
        tm.deleteTaskById(task.getId());
        assertNull(tm.getTask(task.getId()), "задача не удалена");
    }


    // Тесты по эпикам
    @Test
    void shouldAddAndGetEpicAndEpics() {
        tm.addEpic(epic);
        Epic retrievedEpic = tm.getEpic(epic.getId());
        assertNotNull(retrievedEpic, "Эпик не найдена");
        assertEquals(epic, retrievedEpic, "Эпики не совпадают");

        tm.addEpic(anotherEpic);
        List<Epic> epics = tm.getEpics();
        assertNotNull(epics, "Эпики не найдены");
        assertEquals(2, epics.size(), "Неверное количество эпиков");
    }

    @Test
    void shouldGetEpicSubtasks() {
        tm.addEpic(epic);
        tm.addSubtask(subtask);
        List<Subtask> subtasks = tm.getEpicSubtasks(epic.getId());
        assertNotNull(subtasks, "Подзадачи не найдены");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач");
    }

    @Test
    void shouldDeleteEpicById() {
        tm.addEpic(epic);
        tm.deleteEpicById(epic.getId());
        assertNull(tm.getEpic(epic.getId()), "эпик не удалён");
    }

    @Test
    void shouldDeleteEpics() {
        tm.addEpic(epic);
        tm.addEpic(anotherEpic);
        tm.deleteEpics();
        assertEquals(0, tm.getEpics().size(), "Количество эпиков не совпадает");
    }

    @Test
    void shouldDeleteEpicWithSubtask() {
        tm.addEpic(epic);
        tm.addSubtask(subtask);
        tm.deleteEpicById(epic.getId());
        assertNull(tm.getEpic(epic.getId()), "эпик не удалён");
        assertEquals(0, tm.getSubtasks().size(), "Подзадача не удалена");
    }

    // Тесты по подзадачам
    @Test
    void shouldAddAndGetSubtaskAndSubtasks() {
        tm.addEpic(epic);
        tm.addSubtask(subtask);
        Subtask retrievedSubtask = tm.getSubtask(subtask.getId());
        assertNotNull(retrievedSubtask, "Подзадача не найдена");
        assertEquals(subtask, retrievedSubtask, "Подзадачи не совпадают");

        tm.addSubtask(anotherSubtask);
        List<Subtask> subtasks = tm.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не найдены");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач");
    }

    @Test
    void shouldUpdateSubtask() {
        tm.addEpic(epic);
        tm.addSubtask(subtask);

        Subtask newSubtask = new Subtask(1, 2, "новая подзадача", "описание новой подзадачи",
                Status.IN_PROGRESS,
                Duration.ofMinutes(90),
                LocalDateTime.now().plusHours(2));

        tm.updateSubtask(newSubtask);

        assertEquals("новая подзадача", tm.getSubtask(subtask.getId()).getTitle(),
                "название не совпадает");
        assertEquals("описание новой подзадачи", tm.getSubtask(subtask.getId()).getDescription(),
                "описание не совпадает");
        assertEquals(1, tm.getSubtasks().size(), "Количество подзадач не совпадает");
        assertEquals(1, tm.getPrioritizedTasks().size(), "Количество подзадач не совпадает");
        assertSame(tm.getSubtask(subtask.getId()), tm.getPrioritizedTasks().getFirst(), "Подзадачи должны совпадать");
    }

    @Test
    void shouldDeleteSubtaskById() {
        tm.addEpic(epic);
        tm.addSubtask(subtask);
        tm.deleteSubtaskById(subtask.getId());
        assertNull(tm.getSubtask(subtask.getId()), "подзадача не удалена");
    }

    @Test
    void shouldDeleteSubtasks() {
        tm.addEpic(epic);
        tm.addSubtask(subtask);
        tm.deleteSubtasks();
        assertEquals(0, tm.getSubtasks().size(), "Количество подзадач не совпадает");
    }

    @Test
    void getPrioritizedTasks() {
        tm.addEpic(epic);
        tm.addSubtask(subtask);
        tm.addSubtask(anotherSubtask);
        tm.addTask(task);
        tm.addTask(anotherTask);
        assertNotNull(tm.getPrioritizedTasks());
    }

    @Test
    public void shouldReturnViewHistory() {
        tm.addEpic(epic);
        tm.addSubtask(subtask);
        tm.addTask(task);

        tm.getEpic(epic.getId());
        tm.getSubtask(subtask.getId());
        tm.getTask(task.getId());
        assertEquals(3, tm.getHistory().size(), "Количество просмотренных задач должно совпадать");
    }
}
