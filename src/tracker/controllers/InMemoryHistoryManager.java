package tracker.controllers;

import tracker.Node;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedNodeList tasksHistory = new LinkedNodeList();

    @Override
    public void add(Task task) {
        if (task != null) {
            tasksHistory.linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        if (tasksHistory.history.containsKey(id)) {
            tasksHistory.removeNode(tasksHistory.getNode(id));
        }
    }

    // Получение истории задач
    @Override
    public List<Task> getHistory() {
        return tasksHistory.getTasks();
    }

    private static class LinkedNodeList {
        private final Map<Integer, Node<Task>> history = new HashMap<>();
        private Node<Task> head;
        private Node<Task> tail;

        private void linkLast(Task task) {
            if (history.containsKey(task.getId())) {
                removeNode(history.get(task.getId()));
            }
            Node<Task> oldTail = tail;
            Node<Task> newNode = new Node<>(oldTail, task, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            history.put(task.getId(), tail);
        }

        private void removeNode(Node<Task> node) {
            if (node.prev == null && node.next == null) {
                head = null;
                tail = null;
            } else if (node.prev == null) {
                head = node.next;
                node.next.prev = null;
            } else if (node.next == null) {
                tail = node.prev;
                node.prev.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
        }

        private Node<Task> getNode(int id) {
            return history.get(id);
        }

        private List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            if (head != null) {
                Node<Task> node = head;
                while (node != null) {
                    tasks.add(node.data);
                    node = node.next;
                }
            }
            return tasks;
        }
    }
}
