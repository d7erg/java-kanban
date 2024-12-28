import tracker.controllers.InMemoryTaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager tm = new InMemoryTaskManager();

        // Создаем эпик с двумя подзадачами
        tm.addEpic(new Epic(1,"переезд", "смена места жительства"));
        tm.addSubtask(new Subtask(1,2, "сборка", "упаковать вещи", Status.IN_PROGRESS));
        tm.addSubtask(new Subtask(1,3,"загрузка", "загрузить вещи в транспорт",
                Status.IN_PROGRESS));
        tm.addSubtask(new Subtask(1,4,"выгрузка", "выгрузить вещи из транспорт",
                Status.NEW));

        // Создаем эпик с одной подзадачей
        tm.addEpic(new Epic(5,"покупки", "купить еду"));

        tm.getEpic(1);
        tm.getEpic(5);

        printAllTasks(tm);
        System.out.println();

        tm.getEpic(1);
        tm.getEpic(5);
        tm.getSubtask(2);
        tm.getSubtask(3);
        tm.getSubtask(4);

        printAllTasks(tm);
        System.out.println();

        tm.deleteEpicById(5);

        printAllTasks(tm);
        System.out.println();

        tm.deleteEpicById(1);

        printAllTasks(tm);
        System.out.println();
    }

    private static void printAllTasks(InMemoryTaskManager manager) {
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
