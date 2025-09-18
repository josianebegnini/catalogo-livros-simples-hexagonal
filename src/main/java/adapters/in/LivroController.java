package adapters.in;

import app.LivroService;
import domain.Livro;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class LivroController {
    private final LivroService service;

    public LivroController(LivroService service) {
        this.service = service;
    }

    public void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/livros", new LivroHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor iniciado na porta 8080");
    }

    class LivroHandler implements HttpHandler {
        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            try {
                if ("POST".equalsIgnoreCase(method) && path.matches("^/livros/?$")) {
                    System.out.println("Recebendo requisição POST para /livros");
                    handlePost(exchange);
                } else if ("GET".equalsIgnoreCase(method) && parts.length == 3) {
                    handleGetById(exchange, Long.parseLong(parts[2]));
                } else if ("GET".equalsIgnoreCase(method) && parts.length == 2) {
                    handleGetAll(exchange);
                }  else {
                    sendResponse(exchange, 404, "Rota não encontrada");
                }
            } catch (Exception e) {
                sendResponse(exchange, 500, "Erro interno: " + e.getMessage());
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            Livro livro = mapper.readValue(exchange.getRequestBody(), Livro.class);
            service.cadastrarLivro(livro);
            sendResponse(exchange, 201, "Livro cadastrado com sucesso");
        }

        private void handleGetById(HttpExchange exchange, Long id) throws IOException {
            Livro livro = service.buscarLivroPorId(id);
            if (livro != null) {
                sendJson(exchange, 200, livro);
            } else {
                sendResponse(exchange, 404, "Livro não encontrado");
            }
        }

        private void handleGetAll(HttpExchange exchange) throws IOException {
            List<Livro> livros = service.listarTodosLivros();
            sendJson(exchange, 200, livros);
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {
            String json = mapper.writeValueAsString(data);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
