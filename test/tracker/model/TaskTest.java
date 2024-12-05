package tracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void tasksShouldBeEqualIfTheirIdIsEqual() {
        Task task1 = new Task(2,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        Task task2 = new Task(2,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        assertEquals(task1.getId(), task2.getId(), "Экземпляры класса Task не равны друг другу");
    }
}