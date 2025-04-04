package tracker.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import tracker.exceptions.NotFoundException;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.equals("/subtasks")) {
                sendJson(exchange, taskManager.getSubtasks(), 200);
            } else if (path.startsWith("/subtasks/")) {
                String subtaskIdStr = path.substring("/subtasks/".length());

                if (subtaskIdStr.isEmpty()) {
                    sendBadRequest(exchange);
                    return;
                }

                int subtaskId = Integer.parseInt(subtaskIdStr);
                sendJson(exchange, taskManager.getSubtask(subtaskId), 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.equals("/subtasks")) {
                String requestBody = readText(exchange);

                if (requestBody == null || requestBody.isEmpty()) {
                    sendBadRequest(exchange);
                    return;
                }

                Subtask subtask = gson.fromJson(requestBody, Subtask.class);

                Epic epic = taskManager.getEpic(subtask.getEpicId());
                if (epic == null) {
                    sendNotFound(exchange);
                    return;
                }

                if (subtask.getId() != 0) {
                    try {
                        taskManager.updateSubtask(subtask);
                        sendJson(exchange, subtask, 200);
                    } catch (Exception e) {
                        sendIntersection(exchange);
                    }
                } else {
                    taskManager.addSubtask(subtask);
                    exchange.getResponseHeaders().set("Location", "/subtasks/" + subtask.getId());
                    sendJson(exchange, subtask, 201);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        try {
            if (!path.startsWith("/subtasks/")) {
                sendNotFound(exchange);
                return;
            }

            String subtaskIdStr = path.substring("/subtasks/".length());
            if (subtaskIdStr.isEmpty()) {
                sendBadRequest(exchange);
                return;
            }

            int taskId = Integer.parseInt(subtaskIdStr);
            taskManager.deleteSubtaskById(taskId);
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
