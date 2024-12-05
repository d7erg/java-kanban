package tracker.controllers;

import tracker.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_STORAGE = 10;
    private final ArrayList<Task> tasksHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if(tasksHistory.size() == MAX_HISTORY_STORAGE) {
            tasksHistory.removeFirst();
        }
        tasksHistory.add(task);
    }

    // Получение истории задач
    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(tasksHistory);
    }
}
