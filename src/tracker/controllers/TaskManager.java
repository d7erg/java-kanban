package tracker.controllers;

import tracker.model.Task;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    // Получение списка задач
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Удаление задач
    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

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
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    // Получение списка всех подзадач определённого эпика
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasksIds()) {
            listOfSubtasks.add(subtasks.get(subtaskId));
        }
        return listOfSubtasks;
    }

    // создание
    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksIds().add(subtask.getId());
        updateEpicStatus(epic.getId());
    }

    // Обновление
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }


    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic.getId());
    }

    // Удаление по идентификатору
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void deleteSubtaskById(Integer id) {
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getSubtasksIds().remove(id);
        updateEpicStatus(epic.getId());
        subtasks.remove(id);
    }
}
