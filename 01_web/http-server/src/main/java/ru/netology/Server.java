package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final List<String> validPaths = List.of("/index.html", "/spring.svg",
            "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html",
            "/forms.html", "/classic.html", "/events.html", "/events.js");
    private ServerSocket serverSocket;
    private final HashMap<String, Handler> getPaths = new HashMap<>();
    private final HashMap<String, Handler> postPaths = new HashMap<>();

    public Server() {
    }

    public void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        ExecutorService executorService = Executors.newFixedThreadPool(64);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            if (!clientSocket.isClosed()) {
                executorService.execute(() -> {
                    try {
                        this.acceptNewUser(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

        }
    }

    public void acceptNewUser(Socket clientSocket) throws IOException {

        try (clientSocket; BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())) {
            final var requestLine = in.readLine();
            System.out.println(requestLine);

            final var parts = requestLine.split(" ");
            Request request = new Request(parts[0], parts[1]);
            request.setBodyFromInput(in);
            System.out.println(request.getPostParams());
            System.out.println(request.getQueryParams().toString());
            System.out.println(request.getPostParam("password"));
            var path = parts[1];
            if(path.contains("?")) {
                path = path.substring(0,path.indexOf('?'));
            }
            if (request.getMethod().equals("GET")) {
                if (getPaths.containsKey(request.getHeaders())) {
                    getPaths.get(request.getHeaders()).handle(request, out);
                }

            } else if (request.getMethod().equals("POST")) {
                if (postPaths.containsKey(request.getHeaders())) {
                    postPaths.get(request.getHeaders()).handle(request, out);
                }
            }

            if (!validPaths.contains(path)) {
                    out.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.flush();
                }
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);

                if (path.equals("/classic.html")) {
                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    out.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.write(content);
                    out.flush();

                }
                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public void addHandler(String method, String header, Handler handler) {
        switch (method) {
            case "GET":
                getPaths.put(header, handler);
                break;
            case "POST":
                postPaths.put(header, handler);
                break;
            default:
                System.out.println("No such method on Server");

        }

    }
}
