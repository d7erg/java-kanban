package tracker.model;

import org.junit.jupiter.api.Test;
import tracker.constants.Status;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    public void subtasksShouldBeEqualIfTheirIdIsEqual() {
        Subtask subtask1 = new Subtask(1,2, "сборка", "упаковать вещи", Status.IN_PROGRESS,
                Duration.ofMinutes(90), LocalDateTime.now());
        Subtask subtask2 = new Subtask(1,2, "сборка", "упаковать вещи", Status.IN_PROGRESS,
                Duration.ofMinutes(90), LocalDateTime.now());
        assertEquals(subtask1.getId(), subtask2.getId(), "Экземпляры класса Subtask не равны друг другу");
    }
}