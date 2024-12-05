import tracker.controllers.InMemoryTaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        InMemoryTaskManager tm = new InMemoryTaskManager();
        // Создаем две задачи
        tm.addTask(new Task(1,"рыбалка", "отдых на природе", Status.NEW));
        tm.addTask(new Task(2,"прогулка", "ходьба в парке", Status.IN_PROGRESS));

        // Создаем эпик с двумя подзадачами
        tm.addEpic(new Epic(3,"переезд", "смена места жительства"));
        tm.addSubtask(new Subtask(3,4, "сборка", "упаковать вещи", Status.IN_PROGRESS));
        tm.addSubtask(new Subtask(3,5,"загрузка", "загрузить вещи в транспорт", Status.IN_PROGRESS));

        // Создаем эпик с одной подзадачей
        tm.addEpic(new Epic(6,"покупки", "купить еду"));
        tm.addSubtask(new Subtask(6,7, "список", "написать список покупок", Status.IN_PROGRESS));

        tm.getTask(2);
        tm.getTask(1);
        tm.getEpic(3);
        tm.getSubtask(4);
        tm.getSubtask(5);
        tm.getSubtask(4);
        tm.getEpic(3);
        tm.getTask(1);
        tm.getTask(2);
        tm.getTask(2);

        printAllTasks(tm);
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
