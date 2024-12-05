package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;

public interface TaskManager {

    // Получение списка задач
    ArrayList<Task> getHistory();

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    // Удаление задач
    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    // Получение по идентификатору
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    // Получение списка всех подзадач определённого эпика
    ArrayList<Subtask> getEpicSubtasks(int epicId);

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
}
