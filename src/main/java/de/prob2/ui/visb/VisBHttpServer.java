package de.prob2.ui.visb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * This is the HTTP server that serves the Unity-WebGL visualization for ProB.
 * It handles requests from the Unity client and provides the necessary data
 * and resources (GLB Files) to enable interaction with the B model being visualized.
 */
public class VisBHttpServer {

    private static final Path ROOT_DIR = Paths
            .get("E:/User/Arbeiten/Studium/Master/Projektarbeit/prob2_ui/src/main/resources/de/prob2/ui/visb/visb3d");
    private static final int PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(VisBWebSocketServer.class);

    /**
     * Creates a local server
     */ 
    public static void startHTTPServer() {
        try {
            if (!Files.exists(ROOT_DIR))
                Files.createDirectories(ROOT_DIR);

            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Handlers for different paths
            server.createContext("/", new IndexHandler());
            server.createContext("/upload", new UploadHandler());

            server.start();
            LOGGER.info("VisBHttpServer: Server started successfully at http://localhost:" + PORT + "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle GET requests for index and static files
     */
    static class IndexHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Allow Cross-Origin Resource Sharing (CORS)
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            Path file = ROOT_DIR.resolve(path.substring(1)).normalize();
            if (!file.startsWith(ROOT_DIR) || !Files.exists(file)) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            exchange.sendResponseHeaders(200, Files.size(file));
            Files.copy(file, exchange.getResponseBody());
            exchange.close();
        }
    
    };

    /** 
     * Handle POST requests for /upload mit Header "X-Filename"
     */
    static class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String filename = exchange.getRequestHeaders().getFirst("X-Filename");
            if (filename == null || filename.isBlank()) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            Path file = ROOT_DIR.resolve(filename).normalize();
            if (!file.startsWith(ROOT_DIR)) {
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            try (InputStream in = exchange.getRequestBody();
                    OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING)) {
                in.transferTo(out);
            }

            LOGGER.info("VisBHttpServer: Received Data:" + file);
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().write("OK".getBytes());
            exchange.close();
        }
    }
    
        public static void sendGlbData(String filePath) {
        try {
            Path path = Paths.get(filePath);
            String filename = path.getFileName().toString();
            URL url = new URI("http://localhost:"+PORT+"/upload").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-Filename", filename);
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            try (OutputStream os = conn.getOutputStream()) {
                Files.copy(path, os);
            }
            
            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                LOGGER.info("VisBHttpServer: Uploaded file " + filename + " successfully.");
            } else {
                LOGGER.warn("VisBHttpServer: Could not upload file " + filename + ".");
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("VisBHttpServer: An error occurred while uploading {}: {}", filePath != null ? filePath : "Unknown", e);
        }
    }
}