package tracker.model;

import org.junit.jupiter.api.Test;
import tracker.constants.Status;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    public void tasksShouldBeEqualIfTheirIdIsEqual() {
        Task task1 = new Task(2, "прогулка", "ходьба в парке", Status.IN_PROGRESS,
                Duration.ofMinutes(90), LocalDateTime.now());
        Task task2 = new Task(2, "прогулка", "ходьба в парке", Status.IN_PROGRESS,
                Duration.ofMinutes(90), LocalDateTime.now());
        assertEquals(task1.getId(), task2.getId(), "Экземпляры класса Task не равны друг другу");
    }

}