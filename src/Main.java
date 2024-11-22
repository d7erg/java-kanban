import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager tm = new TaskManager();
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

        System.out.println("Список всех задач:");
        System.out.println(tm.getTasks());
        System.out.println();

        System.out.println("Список всех эпиков:");
        System.out.println(tm.getEpics());
        System.out.println();

        System.out.println("Список всех подзадач:");
        System.out.println(tm.getSubtasks());
        System.out.println();

        System.out.println("Изменение статуса задачи:");
        System.out.println("Было");
        System.out.println(tm.getTaskById(2));
        System.out.println("Стало");
        tm.updateTask(new Task(2,"прогулка", "ходьба в парке", Status.DONE));
        System.out.println(tm.getTaskById(2));
        System.out.println();

        System.out.println("Изменение статуса подзадачи:");
        System.out.println("Было");
        System.out.println(tm.getSubtaskById(7));
        System.out.println("Статус эпика");
        System.out.println(tm.getEpicById(6));
        System.out.println("Стало");
        tm.updateSubtask(new Subtask(6,7, "список", "написать список покупок", Status.DONE));
        System.out.println(tm.getSubtaskById(7));
        System.out.println("Статус эпика");
        System.out.println(tm.getEpicById(6));
        System.out.println();

        System.out.println("Удалить задачу:");
        System.out.println("Список задач до удаления");
        System.out.println(tm.getTasks());
        System.out.println("Список задач после удаления");
        tm.deleteTaskById(1);
        System.out.println(tm.getTasks());
        System.out.println();

        System.out.println("Удалить эпик:");
        System.out.println("Эпик и список подзадач до удаления");
        System.out.println(tm.getEpicById(3));
        System.out.println(tm.getSubtasksByEpic(3));
        System.out.println("Список эпиков после удаления");
        tm.deleteEpicById(3);
        System.out.println(tm.getEpics());
        System.out.println("Список подзадач после удаления");
        System.out.println(tm.getSubtasks());
        System.out.println();

        System.out.println("Удалить подзадачу:");
        System.out.println("Эпик и список подзадач до удаления");
        System.out.println(tm.getEpicById(6));
        System.out.println(tm.getSubtasksByEpic(6));
        System.out.println("Статус эпика после удаления подзадачи");
        tm.deleteSubtaskById(7);
        System.out.println(tm.getEpicById(6));
    }
}
