package tracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.constants.Status;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager tm;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    public void beforeEach() {
        tm = Managers.getDefault();
        epic = new Epic(1,"переезд", "смена места жительства");
        subtask = new Subtask(1,2, "сборка", "упаковать вещи", Status.IN_PROGRESS);
        tm.addEpic(epic);
        tm.addSubtask(subtask);
    }


    @Test
    public void epicsShouldBeEqualIfTheirIdIsEqual() {
        Epic sameEpic = tm.getEpic(epic.getId());
        assertEquals(epic.getId(), sameEpic.getId(), "Экземпляры класса Epic не равны друг другу");
    }

    @Test
    public void shouldReturnSubtasksIds() {
        assertEquals(tm.getSubtasks().size(), epic.getSubtasksIds().size(), "Количество подзадач и " +
                "количество ID подзадач не совпадают");
    }

    @Test
    public void shouldReturnCleanSubtasksIdsList() {
        assertEquals(1, epic.getSubtasksIds().size(), "Количество подзадач не совпадает");
        tm.deleteSubtasks();
        assertEquals(0, epic.getSubtasksIds().size(), "Количество подзадач не совпадает");
    }
}