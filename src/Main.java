import tracker.constants.Status;
import tracker.interfaces.TaskManager;
import tracker.managers.FileBackedTaskManager;
import tracker.managers.Managers;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        TaskManager tm = Managers.getDefault(file);

        tm.addTask(new Task(1, "прогулка", "ходьба в парке", Status.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 14, 10, 0)));

        tm.addEpic(new Epic(2, "переезд", "смена места жительства"));
        tm.addSubtask(new Subtask(2, 3, "сборка", "упаковать вещи", Status.IN_PROGRESS,
                Duration.ofMinutes(90), LocalDateTime.now()));

        System.out.println("Приоритетные задачи:");
        tm.getPrioritizedTasks().forEach(System.out::println);

        System.out.println();
        System.out.println("========== просмотр файла ========== ");
        System.out.println();

        FileBackedTaskManager load = FileBackedTaskManager.loadFromFile(file);
        printAllTasks(load);

        file.deleteOnExit();
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        int count = 1;
        for (Task task : manager.getHistory()) {
            System.out.println(count + ". " + task);
            if (count <= 10) {
                count++;
            }
        }
    }
}
