package tracker.managers;

import tracker.constants.Status;
import tracker.constants.TaskType;
import tracker.exceptions.ManagerSaveException;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,duration,start-time,epic-id\n");

            // Объединяем все задачи в один поток
            Stream.of(getTasks(), getEpics(), getSubtasks())
                    .flatMap(Collection::stream)
                    .map(this::toString)
                    .forEach(line -> {
                        try {
                            bw.write(line + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка записи строки в файл.");
                        }
                    });

            bw.write("\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи файла.");
        }
    }

    private String toString(Task task) {
        String[] s = {Integer.toString(task.getId()),
                task.getType().toString(),
                task.getTitle(),
                task.getStatus().toString(),
                task.getDescription(),
                String.valueOf(task.getDuration()),
                task.getStartTime() != null ? task.getStartTime().format(FORMATTER) : "",
        };
        String nString = String.join(",", s);
        if (task.getType() == TaskType.SUBTASK) {
            return nString + "," + ((Subtask) task).getEpicId();
        }
        return nString;
    }


    private static Task fromString(String value) {
        String[] elements = value.split(",");
        int id = Integer.parseInt(elements[0]);
        String type = elements[1];
        String title = elements[2];
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];
        Duration duration = elements[5].isEmpty() ? null : Duration.parse(elements[5]);
        LocalDateTime startTime = elements.length > 6 && !elements[6].isEmpty() ?
                LocalDateTime.parse(elements[6], FORMATTER) : null;

        Integer epicId = null;
        if (type.equals("SUBTASK")) {
            epicId = Integer.valueOf(elements[7]);
        }

        switch (type) {
            case "EPIC":
                Epic epic = new Epic(id, title, description);
                epic.setStatus(status);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                return epic;
            case "SUBTASK":
                return new Subtask(epicId, id, title, description, status, duration, startTime);
            case "TASK":
                return new Task(id, title, description, status, duration, startTime);
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

