package tracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void subtasksShouldBeEqualIfTheirIdIsEqual() {
        Subtask subtask1 = new Subtask(1,2, "сборка", "упаковать вещи", Status.IN_PROGRESS);
        Subtask subtask2 = new Subtask(1,2, "сборка", "упаковать вещи", Status.IN_PROGRESS);
        assertEquals(subtask1.getId(), subtask2.getId(), "Экземпляры класса Subtask не равны друг другу");
    }
}