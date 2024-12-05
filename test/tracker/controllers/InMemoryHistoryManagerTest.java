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
    public void shouldSavePreviousTaskVersion() {
        TaskManager tm = Managers.getDefault();
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        tm.addTask(task);
        hm.add(task);
        tm.updateTask(new Task(1,"рыбалка", "отдых на природе", Status.DONE));
        hm.add(tm.getTasks().getFirst());
        assertEquals(2, hm.getHistory().size(), "Количество задач не совпадает");
        assertNotEquals(hm.getHistory().getFirst().getStatus(),hm.getHistory().getLast().getStatus(),
                "Статусы не должны совпадать");
    }

    @Test
    public void shouldSaveOnly10Tasks() {
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        for (int i = 0; i < 12; i++) {
            hm.add(task);
        }
        assertEquals(10, hm.getHistory().size(), "Количество задач не совпадает");
    }
}