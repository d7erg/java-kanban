package tracker.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import tracker.interfaces.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.equals("/prioritized")) {
                sendJson(exchange, taskManager.getPrioritizedTasks(), 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String path) {
        throw new UnsupportedOperationException("Метод не поддерживается");
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String path) {
        throw new UnsupportedOperationException("Метод не поддерживается");
    }
}
