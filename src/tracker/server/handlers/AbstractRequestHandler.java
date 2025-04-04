package tracker.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public abstract class AbstractRequestHandler implements HttpHandler {

    protected abstract void handleGet(HttpExchange exchange, String path) throws IOException;

    protected abstract void handlePost(HttpExchange exchange, String path) throws IOException;

    protected abstract void handleDelete(HttpExchange exchange, String path) throws IOException;

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String requestMethod = exchange.getRequestMethod().toUpperCase();
            String path = exchange.getRequestURI().getPath();

            switch (requestMethod) {
                case "GET" -> handleGet(exchange, path);
                case "POST" -> handlePost(exchange, path);
                case "DELETE" -> handleDelete(exchange, path);
                default -> exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }

    }

}
