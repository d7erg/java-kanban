package tracker.managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.constants.Status;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {

    File file;
    FileBackedTaskManager tm;

    @BeforeEach
    public void setUp() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        tm = new FileBackedTaskManager(file);
    }

    @AfterEach
    public void clear() {
        file.deleteOnExit();
    }

    @Test
    public void shouldSaveEmptyFile() {
        System.out.println(file.getAbsolutePath());
        assertTrue(tm.getTasks().isEmpty());
    }

    @Test
    public void shouldLoadEmptyFile() {
        FileBackedTaskManager load = FileBackedTaskManager.loadFromFile(file);
        assertTrue(load.getTasks().isEmpty());
    }

    @Test
    public void shouldSaveAndLoadTasks() {
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS,
                Duration.ofMinutes(90), LocalDateTime.of(2025, 3, 14, 10, 0));
        Epic epic = new Epic(2,"переезд", "смена места жительства");
        Subtask subtask = new Subtask(2,3, "сборка", "упаковать вещи", Status.IN_PROGRESS,
                Duration.ofMinutes(90), LocalDateTime.of(2025, 3, 14, 10, 0));
        tm.addTask(task);
        tm.addEpic(epic);
        tm.addSubtask(subtask);

        FileBackedTaskManager load = FileBackedTaskManager.loadFromFile(file);
        assertEquals(tm.getTasks(), load.getTasks());
        assertEquals(tm.getEpics(), load.getEpics());
        assertEquals(tm.getSubtasks(), load.getSubtasks());
    }
}