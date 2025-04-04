package tracker.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import tracker.exceptions.NotFoundException;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.equals("/epics")) {
                sendJson(exchange, taskManager.getEpics(), 200);
            } else if (path.startsWith("/epics/")) {
                if (path.contains("/subtasks")) {
                    String epicIdStr = path.substring("/epics/".length(), path.indexOf("/subtasks"));
                    int epicId = Integer.parseInt(epicIdStr);
                    sendJson(exchange, taskManager.getEpicSubtasks(epicId), 200);
                } else {
                    String epicIdStr = path.substring("/epics/".length());
                    int epicId = Integer.parseInt(epicIdStr);
                    sendJson(exchange, taskManager.getEpic(epicId), 200);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.equals("/epics")) {
                String requestBody = readText(exchange);
                Epic epic = gson.fromJson(requestBody, Epic.class);

                if (epic == null) {
                    sendBadRequest(exchange);
                    return;
                }

                taskManager.addEpic(epic);
                exchange.getResponseHeaders().set("Location", "/epics/" + epic.getId());
                sendJson(exchange, epic, 201);
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        try {
            if (!path.startsWith("/epics/")) {
                sendNotFound(exchange);
                return;
            }

            String epicIdStr = path.substring("/epics/".length());
            if (epicIdStr.isEmpty()) {
                sendBadRequest(exchange);
                return;
            }

            int epicId = Integer.parseInt(epicIdStr);
            taskManager.deleteEpicById(epicId);
            sendJson(exchange, null, 200);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
