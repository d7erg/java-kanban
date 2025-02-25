package tracker.controllers;

import tracker.constants.Status;
import tracker.constants.TaskType;
import tracker.exceptions.ManagerSaveException;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,epic-id\n");
            for (Task task : getTasks()) {
                bw.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                bw.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                bw.write(toString(subtask) + "\n");
            }
            bw.write("\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи файла.");
        }
    }

    private String toString(Task task) {
        String s = task.getId() + ","
                + task.getType().toString() + ","
                + task.getTitle() + ","
                + task.getStatus().toString() + ","
                + task.getDescription();
        if (task.getType() == TaskType.SUBTASK) {
            return s + "," + ((Subtask) task).getEpicId();
        }
        return s;
    }

    private static Task fromString(String value) {
        String[] elements = value.split(",");
        int id = Integer.parseInt(elements[0]);
        String type = elements[1];
        String title = elements[2];
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];
        Integer epicId = null;
        if (type.equals("SUBTASK")) {
            epicId = Integer.valueOf(elements[5]);
        }
        switch (type) {
            case "EPIC":
                Epic epic = new Epic(id, title, description);
                epic.setStatus(status);
                return epic;
            case "SUBTASK":
                return new Subtask(epicId, id, title, description, status);
            case "TASK":
                return new Task(id, title, description, status);
            default:
                return null;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty() || line.split(",")[0].equals("id")) {
                    continue;
                }
                Task task = fromString(line);
                if (task != null) {
                    if (task.getType() == TaskType.EPIC) {
                        manager.addEpic((Epic) task);
                    } else if (task.getType() == TaskType.SUBTASK) {
                        manager.addSubtask((Subtask) task);
                    } else {
                        manager.addTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла.");
        }
        return manager;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpicStatus(int id) {
        super.updateEpicStatus(id);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }
}

