package tracker.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.constants.Status;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
        Task task = new Task(1,"прогулка", "ходьба в парке", Status.IN_PROGRESS);
        Epic epic = new Epic(2,"переезд", "смена места жительства");
        Subtask subtask = new Subtask(2,3, "сборка", "упаковать вещи", Status.IN_PROGRESS);
        tm.addTask(task);
        tm.addEpic(epic);
        tm.addSubtask(subtask);

        FileBackedTaskManager load = FileBackedTaskManager.loadFromFile(file);
        assertEquals(tm.getTasks(), load.getTasks());
        assertEquals(tm.getEpics(), load.getEpics());
        assertEquals(tm.getSubtasks(), load.getSubtasks());
    }
}