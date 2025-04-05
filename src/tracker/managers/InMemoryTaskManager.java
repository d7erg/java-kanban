package tracker.managers;

import tracker.constants.Status;
import tracker.exceptions.NotFoundException;
import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));
    private int nextId = 1;

    // Получение приоритетных задач
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // Проверка пересечения по времени
    private boolean hasNoIntersection(Task task) {
        return prioritizedTasks.stream()
                .noneMatch(pTask -> {
                    if (pTask.getStartTime() == null || pTask.getEndTime() == null ||
                            task.getStartTime() == null || task.getEndTime() == null) {
                        return true;
                    }
                    // Находим максимальное из начал
                    LocalDateTime maxStart = pTask.getStartTime().isAfter(task.getStartTime())
                            ? pTask.getStartTime() : task.getStartTime();
                    // Находим минимальное из концов
                    LocalDateTime minEnd = pTask.getEndTime().isBefore(task.getEndTime())
                            ? pTask.getEndTime() : task.getEndTime();

                    // Отрезки пересекаются, если:
                    // max(начало1, начало2) < min(конец1, конец2)
                    // maxStart строго меньше minEnd
                    return maxStart.isBefore(minEnd);
                });
    }

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

    // обновления статуса эпика
    protected void updateEpicStatus(int id) {
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

    // обновления времени эпика
    private void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        LocalDateTime startTime = epic.getSubtasksIds().stream()
                .map(subtasksId -> subtasks.get(subtasksId).getStartTime())
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo).orElse(null);

        LocalDateTime endTime = epic.getSubtasksIds().stream()
                .map(subtasksId -> subtasks.get(subtasksId).getEndTime())
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo).orElse(null);

        Duration duration = epic.getSubtasksIds().stream()
                .map(subtasksId -> subtasks.get(subtasksId).getDuration())
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);
    }

    // Получение по идентификатору
    @Override
    public Task getTask(int id) {
        if (tasks.get(id) == null) {
            throw new NotFoundException("Задача с ID " + id + " не найдена");
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) == null) {
            throw new NotFoundException("Эпик с ID " + id + " не найдена");
        }
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.get(id) == null) {
            throw new NotFoundException("Подзадача с ID " + id + " не найдена");
        }
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    // Получение списка всех подзадач определённого эпика

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик не найдена");
        }

        return epic.getSubtasksIds()
                .stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    // создание
    @Override
    public void addTask(Task task) {
        if (hasNoIntersection(task)) {
            task.setId(nextId++);
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        } else {
            throw new IllegalArgumentException("Пересечение по времени выполнения.");
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (hasNoIntersection(subtask)) {
            subtask.setId(nextId++);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic == null) {
                throw new NotFoundException("Эпик не найдена");
            }
            epic.getSubtasksIds().add(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
        } else {
            throw new IllegalArgumentException("Пересечение по времени выполнения.");
        }
    }

    // Обновление
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            if (hasNoIntersection(task)) {
                prioritizedTasks.remove(getTask(task.getId()));
                prioritizedTasks.add(task);
                tasks.put(task.getId(), task);
            } else {
                throw new IllegalArgumentException("Пересечение по времени выполнения.");
            }
        } else {
            throw new NoSuchElementException("Задача отсутствует");
        }
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            if (hasNoIntersection(subtask)) {
                prioritizedTasks.remove(getSubtask(subtask.getId()));
                prioritizedTasks.add(subtask);
                subtasks.put(subtask.getId(), subtask);
            } else {
                throw new IllegalArgumentException("Пересечение по времени выполнения.");
            }

            Epic epic = epics.get(subtask.getEpicId());
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
        } else {
            throw new NoSuchElementException("Подзадача отсутствует");
        }
    }

    // Удаление по идентификатору
    @Override
    public void deleteTaskById(int id) {
        prioritizedTasks.remove(getTask(id));
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        historyManager.remove(id);
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик не найдена");
        }

        for (Integer subtaskId : epic.getSubtasksIds()) {
            prioritizedTasks.remove(getSubtask(subtaskId));
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        if (subtasks.get(id) == null) {
            throw new NotFoundException("Подзадача не найдена");
        }
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getSubtasksIds().remove(id);
        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());
        prioritizedTasks.remove(getSubtask(id));
        historyManager.remove(id);
        subtasks.remove(id);
    }

    // Удаление задач
    @Override
    public void deleteTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(getTask(id));
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();

        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(getSubtask(id));
        }
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
        }

        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(getSubtask(id));
        }
        subtasks.clear();
    }

    @Override
    public void clearAll() {
        // Очищаем подзадачи
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
        }

        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(getSubtask(id));
        }
        subtasks.clear();

        // Очищаем эпики
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();

        // Очищаем обычные задачи
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(getTask(id));
        }
        tasks.clear();

        // Очищаем историю и приоритетные задачи
        historyManager.clear();
        prioritizedTasks.clear();
    }

}
