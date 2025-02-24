import tracker.controllers.FileBackedTaskManager;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.constants.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {


        File file = File.createTempFile("tasks", ".csv");

        FileBackedTaskManager tm = new FileBackedTaskManager(file);

        // Создаем эпик с тремя подзадачами
        tm.addEpic(new Epic(1,"переезд", "смена места жительства"));
        tm.addSubtask(new Subtask(1,2, "сборка", "упаковать вещи", Status.IN_PROGRESS));
        tm.addSubtask(new Subtask(1,3,"загрузка", "загрузить вещи в транспорт",
                Status.IN_PROGRESS));
        tm.addSubtask(new Subtask(1,4,"выгрузка", "выгрузить вещи из транспорт",
                Status.NEW));

        // Создаем пустой эпик
        tm.addEpic(new Epic(5,"покупки", "купить еду"));


        System.out.println("Просмотр");
        printAllTasks(tm);
        System.out.println();

        System.out.println("-----------------------------------------------");
        System.out.println("Просмотр файла");
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
