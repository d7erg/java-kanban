package tracker.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.constants.Status;
import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManagerTest;
import tracker.model.Task;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    HistoryManager hm;

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    public void beforeEach() {
        super.setUp();
        hm = Managers.getDefaultHistory();
    }

    @Test
    void shouldBeEmptyHistory() {
        assertTrue(hm.getHistory().isEmpty());
    }

    @Test
    public void shouldNotBeAddedNullToList() {
        hm.add(null);
        hm.add(task);
        assertEquals(1, hm.getHistory().size(), "Значение не совпадают");
    }

    @Test
    public void shouldAddTask() {
        tm.addTask(task);
        tm.getTask(task.getId());
        assertEquals(1, tm.getHistory().size(), "Значение не совпадают");
    }

    @Test
    public void shouldAddSave20Tasks() {
        for (int i = 1; i <= 20; i++) {
            hm.add(new Task(i, "задача", "описание"));
        }
        assertEquals(20, hm.getHistory().size(), "Количество просмотренных задач не совпадает");
    }


    @Test
    public void shouldRemoveTask() {
        tm.addTask(task);
        tm.getTask(task.getId());
        tm.deleteTaskById(task.getId());
        assertEquals(0, tm.getHistory().size(), "Значение не совпадают");
    }


    @Test
    public void shouldNotSavePreviousTaskVersion() {
        tm.addTask(task);
        hm.add(task);

        task.setTitle("рыбалка");
        task.setDescription("отдых на природе");
        task.setStatus(Status.DONE);
        task.setDuration(Duration.ofMinutes(180));

        hm.add(tm.getTasks().getFirst());
        assertEquals(1, hm.getHistory().size(), "Количество задач не совпадает");
        assertEquals(hm.getHistory().getFirst().getStatus(), hm.getHistory().getLast().getStatus(),
                "Статусы должны совпадать");
    }

    @Test
    public void shouldSaveLastViewedTask() {
        tm.addTask(task);
        tm.getTask(task.getId());
        tm.getTask(task.getId());
        tm.getTask(task.getId());
        assertEquals(1, tm.getHistory().size(), "Количество просмотренных задач не совпадает");
    }
}