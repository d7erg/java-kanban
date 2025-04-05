package tracker.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractRequestHandler implements HttpHandler {
    private static final Logger logger = Logger.getLogger(AbstractRequestHandler.class.getName());

    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    private void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, 0);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
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
            logger.log(Level.SEVERE, "Ошибка при обработке HTTP запроса", e);
            throw e;
        }
    }

}
