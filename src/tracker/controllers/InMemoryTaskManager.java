package tracker.controllers;

import tracker.model.Task;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int nextId = 1;

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    // Получение списка задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Удаление задач
    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    // обновления статуса эпика
    private void updateEpicStatus(int id) {
        Epic epic = epics.get(id);

        if (epic.getSubtasksIds().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            Status epicStatus = Status.IN_PROGRESS;
            int countDone = 0;
            int countNew = 0;
            for (Integer subtaskId : epic.getSubtasksIds()) {
                Subtask nSubtask = subtasks.get(subtaskId);
                if (nSubtask.getStatus().equals(Status.DONE)) {
                    countDone++;
                } else if (nSubtask.getStatus().equals(Status.NEW)) {
                    countNew++;
                }
            }

            if (countDone == epic.getSubtasksIds().size()) {
                epicStatus = Status.DONE;
            } else if (countNew == epic.getSubtasksIds().size()) {
                epicStatus = Status.NEW;
            }
            epic.setStatus(epicStatus);
        }
    }

    // Получение по идентификатору
    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    // Получение списка всех подзадач определённого эпика
    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> listOfSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasksIds()) {
            listOfSubtasks.add(subtasks.get(subtaskId));
        }
        return listOfSubtasks;
    }

    // создание
    @Override
    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksIds().add(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic.getId());
    }

    // Обновление
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic.getId());
    }

    // Удаление по идентификатору
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getSubtasksIds().remove(id);
        updateEpicStatus(epic.getId());
        subtasks.remove(id);
    }

}
