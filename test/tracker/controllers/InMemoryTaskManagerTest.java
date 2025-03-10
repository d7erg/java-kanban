package tracker.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.constants.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static TaskManager tm;

    @BeforeEach
    public void beforeEach() {
        tm = Managers.getDefault();
    }

    @Test
    void shouldAddTaskAndTasks() {
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        tm.addTask(task);
        Task sameTask = tm.getTask(task.getId());
        assertNotNull(sameTask, "Задача не найдена");
        assertEquals(task, sameTask, "Задачи не совпадают");

        List<Task> tasks = tm.getTasks();
        assertNotNull(tasks, "Задачи не найдены");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают");
    }

    @Test
    void shouldAddEpicAndSubtask() {
        Epic epic = new Epic(1,"переезд", "смена места жительства");
        Subtask subtask = new Subtask(1,2, "сборка", "упаковать вещи", Status.IN_PROGRESS);
        tm.addEpic(epic);
        tm.addSubtask(subtask);
        Epic sameEpic = tm.getEpic(epic.getId());
        Subtask sameSubtask = tm.getSubtask(subtask.getId());

        assertNotNull(sameEpic, "Эпик не найден");
        assertEquals(epic, sameEpic, "Эпики не совпадают");
        assertNotNull(sameSubtask, "Подзадача не найден");
        assertEquals(subtask, sameSubtask, "Подзадачи не совпадают");
    }

    @Test
    public void shouldUnchangedFieldsWhenAddTask() {
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        tm.addTask(task);
        List<Task> tasks = tm.getTasks();
        Task sameTask = tasks.getFirst();
        assertEquals(task.getId(), sameTask.getId(),"ID не совпадает");
        assertEquals(task.getTitle(), sameTask.getTitle(),"Название не совпадает");
        assertEquals(task.getDescription(), sameTask.getDescription(),"Описание не совпадает");
        assertEquals(task.getStatus(), sameTask.getStatus(),"Status не совпадает");
    }

    @Test
    public void shouldUpdateSubtaskAndChangeEpicStatus() {
        tm.addEpic(new Epic(1, "переезд", "смена места жительства"));
        Subtask subtask = new Subtask(1, 2, "сборка", "упаковать вещи", Status.IN_PROGRESS);
        tm.addSubtask(subtask);
        assertEquals(subtask.getStatus(), tm.getEpic(1).getStatus(),
                "Статус эпика должен быть как у подзадачи");
        tm.updateSubtask(new Subtask(1, 2, "сборка", "упаковать вещи", Status.DONE));
        assertEquals(subtask.getId(), tm.getEpicSubtasks(1).getFirst().getId(),
                "ID подзадач должны совпадать");
        assertNotEquals(subtask.getStatus(), tm.getEpicSubtasks(1).getFirst().getStatus(),
                "Статусы должны быть разными");
        assertEquals(tm.getEpicSubtasks(1).getFirst().getStatus(), tm.getEpic(1).getStatus(),
                "Статус эпика должен быть как у подзадачи");
    }

    @Test
    public void shouldReturnUpdatedEpicStatus() {
        Epic epic = new Epic(1,"переезд", "смена места жительства");
        tm.addEpic(epic);
        Subtask subtask = new Subtask(1,2, "сборка", "упаковать вещи", Status.IN_PROGRESS);
        tm.addSubtask(subtask);
        assertNotNull(tm.getEpic(epic.getId()).getStatus(), "Статус не должен быть пуст");
        assertEquals(tm.getSubtask(subtask.getId()).getStatus(),tm.getEpic(epic.getId()).getStatus(),
                "Статусы должны быть одинаковыми");
    }

    @Test
    public void shouldDeleteTasks() {
        tm.addTask(new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS));
        tm.deleteTasks();
        assertEquals(0, tm.getTasks().size(), "Количество задач не совпадает");
    }

    @Test
    public void shouldDeleteTaskByID() {
        tm.addTask(new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS));
        tm.deleteTaskById(1);
        assertEquals(0, tm.getTasks().size(), "Количество задач не совпадает");
    }

    @Test
    public void shouldDeleteEpicAndSubtasks() {
        tm.addEpic(new Epic(1, "переезд", "смена места жительства"));
        tm.addSubtask(new Subtask(1, 2, "сборка", "упаковать вещи", Status.IN_PROGRESS));
        tm.addSubtask(new Subtask(1, 3, "загрузка", "загрузить вещи в транспорт",
                Status.IN_PROGRESS));
        assertEquals(1, tm.getEpics().size(), "Количество эпиков в списке не совпадает");
        assertEquals(2, tm.getEpicSubtasks(1).size(),
                "Количество подзадач в списке не совпадает");
        tm.deleteEpicById(1);
        assertEquals(0, tm.getEpics().size(), "Список эпиков должен быть пуст");
        assertEquals(0, tm.getSubtasks().size(), "Список подзадач должен быть пуст");
    }

    @Test
    public void shouldDeleteEpics() {
        tm.addEpic(new Epic(1, "переезд", "смена места жительства"));
        tm.addEpic(new Epic(6,"покупки", "купить еду"));
        tm.deleteEpics();
        assertEquals(0, tm.getEpics().size(), "Количество эпиков не совпадает");
    }

    @Test
    public void shouldDeleteSubtaskByID() {
        tm.addEpic(new Epic(1, "переезд", "смена места жительства"));
        tm.addSubtask(new Subtask(1, 2, "сборка", "упаковать вещи", Status.IN_PROGRESS));
        assertNotNull(tm.getSubtasks(), "Список подзадач не должен быть пуст");
        tm.deleteSubtaskById(2);
        assertEquals(0, tm.getSubtasks().size(), "Список подзадач должен быть пуст");
    }

    @Test
    public void shouldNotSaveDeletedSubtasksID() {
        tm.addEpic(new Epic(1, "переезд", "смена места жительства"));
        tm.addSubtask(new Subtask(1, 2, "сборка", "упаковать вещи", Status.IN_PROGRESS));
        tm.addSubtask(new Subtask(1, 3, "загрузка", "загрузить вещи в транспорт",
                Status.IN_PROGRESS));
        tm.deleteSubtaskById(2);
        assertEquals(1, tm.getEpic(1).getSubtasksIds().size(),
                "Количество ID подзадач должен быть пуст");
        tm.deleteSubtasks();
        assertEquals(0, tm.getEpic(1).getSubtasksIds().size(),
                "Список ID подзадач должен быть пуст");
    }
}