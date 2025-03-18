package tracker.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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