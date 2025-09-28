package MultiThreaded;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {

    private final int port;
    private static final String WEB_ROOT = "www";

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("HTTP Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread thread = new Thread(() -> handleClient(clientSocket));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server startup error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream outputStream = clientSocket.getOutputStream()
        ) {
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return; 
            }

            System.out.println("Request Received: " + requestLine);
            String[] requestParts = requestLine.split(" ");
            
            if (requestParts.length < 2 || !requestParts[0].equals("GET")) {
                sendErrorResponse(outputStream, 501, "Not Implemented");
                return;
            }

            String filePath = requestParts[1];
            if (filePath.equals("/")) {
                filePath = "/index.html";
            }
            
            serveFile(outputStream, filePath);

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private void serveFile(OutputStream outputStream, String filePath) throws IOException {
        Path path = Paths.get(WEB_ROOT + filePath).toAbsolutePath();
        File file = path.toFile();

        if (!path.startsWith(Paths.get(WEB_ROOT).toAbsolutePath())) {
            sendErrorResponse(outputStream, 403, "Forbidden");
            return;
        }

        if (file.exists() && !file.isDirectory()) {
            byte[] fileBytes = Files.readAllBytes(path);
            String contentType = getContentType(file.getName());
            
            String header = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + contentType + "\r\n" +
                            "Content-Length: " + fileBytes.length + "\r\n" +
                            "Connection: close\r\n\r\n";
            
            outputStream.write(header.getBytes());
            outputStream.write(fileBytes);
        } else {
            // File does not exist, send a 404 Not Found response
            sendErrorResponse(outputStream, 404, "Not Found");
        }
        outputStream.flush();
    }

    private void sendErrorResponse(OutputStream outputStream, int statusCode, String statusText) throws IOException {
        String body = "<h1>" + statusCode + " " + statusText + "</h1>";
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                          "Content-Type: text/html\r\n" +
                          "Content-Length: " + body.length() + "\r\n" +
                          "Connection: close\r\n\r\n" +
                          body;
        outputStream.write(response.getBytes());
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream"; // Default binary stream
    }
    
    public static void main(String[] args) {
        Server server = new Server(8080);
        server.start();
    }
}