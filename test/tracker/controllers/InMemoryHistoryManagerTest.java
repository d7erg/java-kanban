package tracker.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Status;
import tracker.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager hm;

    @BeforeEach
    public void beforeEach() {
        hm = Managers.getDefaultHistory();
    }

    @Test
    public void shouldNotBeAddedNullToList() {
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        hm.add(null);
        hm.add(task);
        assertEquals(1, hm.getHistory().size(), "Значение не совпадают");
    }

    @Test
    public void shouldAddTask() {
        TaskManager tm = Managers.getDefault();
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        tm.addTask(task);
        tm.getTask(1);
        assertEquals(1, tm.getHistory().size(), "Значение не совпадают");
    }

    @Test
    public void shouldAddSave20Tasks() {
        TaskManager tm = Managers.getDefault();
        for (int i = 1; i <= 20; i++) {
            tm.addTask(new Task(i,"прогулка", "ходьба в парке", Status.IN_PROGRESS));
            tm.getTask(i);
        }
        assertEquals(20, tm.getHistory().size(), "Количество просмотренных задач не совпадает");
    }


    @Test
    public void shouldRemoveTask() {
        TaskManager tm = Managers.getDefault();
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        tm.addTask(task);
        tm.getTask(1);
        tm.deleteTaskById(1);
        assertEquals(0, tm.getHistory().size(), "Значение не совпадают");
    }

    @Test
    public void shouldReturnViewHistory() {
        TaskManager tm = Managers.getDefault();
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        tm.addTask(task);
        hm.add(task);
        assertEquals(1, hm.getHistory().size(), "Количество просмотренных задач должно совпадать");
    }

    @Test
    public void shouldNotSavePreviousTaskVersion() {
        TaskManager tm = Managers.getDefault();
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        tm.addTask(task);
        hm.add(task);
        tm.updateTask(new Task(1,"рыбалка", "отдых на природе", Status.DONE));
        hm.add(tm.getTasks().getFirst());
        assertEquals(1, hm.getHistory().size(), "Количество задач не совпадает");
        assertEquals(hm.getHistory().getFirst().getStatus(),hm.getHistory().getLast().getStatus(),
                "Статусы должны совпадать");
    }

    @Test
    public void shouldSaveLastViewedTask( ){
        TaskManager tm = Managers.getDefault();
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        tm.addTask(task);
        tm.getTask(1);
        tm.getTask(1);
        tm.getTask(1);
        assertEquals(1, tm.getHistory().size(), "Количество просмотренных задач не совпадает");
    }

}