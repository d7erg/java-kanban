package tracker.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void shouldReturnInitializeInMemoryTaskManager() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    public void shouldReturnInitializeInMemoryHistoryManager() {
        assertNotNull(Managers.getDefaultHistory());
    }
}