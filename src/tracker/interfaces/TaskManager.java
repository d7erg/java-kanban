package tracker.interfaces;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.List;

public interface TaskManager {

    // Получение списка задач
    List<Task> getPrioritizedTasks();

    List<Task> getHistory();

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    // Получение по идентификатору
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    // Получение списка всех подзадач определённого эпика
    List<Subtask> getEpicSubtasks(int epicId);

    // создание
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    // Обновление
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    // Удаление по идентификатору
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(Integer id);

    // Удаление задач
    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    // Удаление всех задач, эпиков и подзадач
    void clearAll();
}
