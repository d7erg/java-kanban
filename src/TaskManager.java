import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer,Task> tasks = new HashMap<>();
    private final HashMap<Integer,Epic> epics = new HashMap<>();
    private final HashMap<Integer,Subtask> subtasks = new HashMap<>();
    int nextId = 1;

    // Получение списка задач
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    // Удаление задач
    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
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
    public HashMap<Integer, Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        HashMap<Integer, Subtask> listOfSubtasks = new HashMap<>();
        for (Integer subtaskId : epic.subtasksIds) {
            listOfSubtasks.put(subtaskId, subtasks.get(subtaskId));
        }
        return listOfSubtasks;
    }

    // создание
    public void addTask(Task task) {
        task.id = nextId++;
        tasks.put(task.id, task);
    }

    public void addEpic(Epic epic) {
        epic.id = nextId++;
        epics.put(epic.id, epic);
    }

    public void addSubtask(Subtask subtask) {
        subtask.id = nextId++;
        subtasks.put(subtask.id, subtask);

        String epicStatus = String.valueOf(Status.IN_PROGRESS);
        Epic epic = epics.get(subtask.epicId);
        epic.subtasksIds.add(subtask.id);

        int countDone = 0;
        int countNew = 0;
        for (Integer subtaskId : epic.subtasksIds) {
            Subtask nSubtask = subtasks.get(subtaskId);
            if (nSubtask.status.equals(Status.DONE)) {
                countDone++;
            } else if (nSubtask.status.equals(Status.NEW)) {
                countNew++;
            }
        }

        if (countDone == epic.subtasksIds.size()) {
            epicStatus = String.valueOf(Status.DONE);
        } else if (countNew == epic.subtasksIds.size()) {
            epicStatus = String.valueOf(Status.NEW);
        }
        epic.status = Status.valueOf(epicStatus);
    }

    // Обновление
    public void updateTask(Task task) {
        tasks.put(task.id, task);
    }


    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.id, subtask);

        String epicStatus = String.valueOf(Status.IN_PROGRESS);
        Epic epic = epics.get(subtask.epicId);

        int countDone = 0;
        int countNew = 0;
        for (Integer subtaskId : epic.subtasksIds) {
            Subtask nSubtask = subtasks.get(subtaskId);
            if (nSubtask.status.equals(Status.DONE)) {
                countDone++;
            } else if (nSubtask.status.equals(Status.NEW)) {
                countNew++;
            }
        }

        if (countDone == epic.subtasksIds.size()) {
            epicStatus = String.valueOf(Status.DONE);
        } else if (countNew == epic.subtasksIds.size()) {
            epicStatus = String.valueOf(Status.NEW);
        }
        epic.status = Status.valueOf(epicStatus);
    }

    // Удаление по идентификатору
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.subtasksIds) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {
        subtasks.remove(id);
    }
}
