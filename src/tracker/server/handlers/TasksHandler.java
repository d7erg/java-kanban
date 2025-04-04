package tracker.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import tracker.exceptions.NotFoundException;
import tracker.interfaces.TaskManager;
import tracker.model.Task;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.equals("/tasks")) {
                sendJson(exchange, taskManager.getTasks(), 200);
            } else if (path.startsWith("/tasks/")) {
                String taskIdStr = path.substring("/tasks/".length());

                if (taskIdStr.isEmpty()) {
                    sendBadRequest(exchange);
                    return;
                }

                int taskId = Integer.parseInt(taskIdStr);
                sendJson(exchange, taskManager.getTask(taskId), 200);
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
            if (path.equals("/tasks")) {
                String requestBody = readText(exchange);
                Task task = gson.fromJson(requestBody, Task.class);

                if (task == null) {
                    sendBadRequest(exchange);
                    return;
                }

                if (task.getId() != 0) {
                    try {
                        taskManager.updateTask(task);
                        sendJson(exchange, task, 200);
                    } catch (Exception e) {
                        sendIntersection(exchange);
                    }
                } else {
                    taskManager.addTask(task);
                    exchange.getResponseHeaders().set("Location", "/tasks/" + task.getId());
                    sendJson(exchange, task, 201);
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
            if (!path.startsWith("/tasks/")) {
                sendNotFound(exchange);
                return;
            }

            String taskIdStr = path.substring("/tasks/".length());
            if (taskIdStr.isEmpty()) {
                sendBadRequest(exchange);
                return;
            }

            int taskId = Integer.parseInt(taskIdStr);
            taskManager.deleteTaskById(taskId);
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
