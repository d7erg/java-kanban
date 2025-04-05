package tracker.server;

import com.sun.net.httpserver.HttpServer;
import tracker.interfaces.TaskManager;
import tracker.managers.Managers;
import tracker.server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(taskManager));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer();
            server.start();
            System.out.println("Сервер запущен по адресу: http://localhost:" + PORT);

            System.out.println("Доступные пути:");
            System.out.println("Задачи:");
            System.out.println("- /tasks");
            System.out.println("- /tasks/{id}");
            System.out.println("Эпики:");
            System.out.println("- /epics");
            System.out.println("- /epics/{id}");
            System.out.println("- /epics/{id}/subtasks");
            System.out.println("Подзадачи:");
            System.out.println("- /subtasks");
            System.out.println("- /subtasks/{id}");
            System.out.println("История:");
            System.out.println("- /history");
            System.out.println("Приоритетный задачи:");
            System.out.println("- /prioritized");

            server.stop();
            System.out.println("Сервер остановлен на порту: " + PORT);
        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }

    public void start() throws IOException {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}

