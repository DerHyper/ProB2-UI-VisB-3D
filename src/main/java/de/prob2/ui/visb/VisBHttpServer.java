package de.prob2.ui.visb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final int PORT = 8080;
    private static final Path ROOT_DIR = Paths
            .get(System.getProperty("user.dir")+"/src/main/resources/de/prob2/ui/visb/visb3d");
    private static final Logger LOGGER = LoggerFactory.getLogger(VisBWebSocketServer.class);
    private static byte[] glbData = null;
    private static String glbFileName = null;
    /** Maps the Name of the File to the glb data contained in the file.*/
    private static Map<String, byte[]> modelFiles;

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

            server.start();
            LOGGER.info("VisBHttpServer: Server started successfully at http://localhost:" + PORT + "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Caches a GLB file to be served by the HTTP server
     * @param filePath the path to the GLB file
     */
    public static void sendGlbData(Path filePath) {
        try {
            glbFileName = filePath.getFileName().toString();
            glbData = Files.readAllBytes(filePath);
        } catch (IOException e) {
            LOGGER.error("VisBHttpServer: An error occurred while uploading {}: {}",
                    filePath != null ? filePath : "Unknown", e);
        }
    }

    /**
     * Gets the data URI for the cached GLB file
     */
    public static String getGlbDataUri()
    {
        if (glbFileName == null) {
            return null;
        }
        return "http://localhost:" + PORT + "/" + glbFileName;
    }

    /**
     * Caches GLB files to be served by the HTTP server
     * @param models the paths to the GLB files
     */
    public static void sendModelsData(Path[] models) {
        modelFiles = new HashMap<>();
        
        for (int i = 0; i < models.length; i++) {
            try {
                modelFiles.put(models[i].getFileName().toString(), Files.readAllBytes(models[i]));
            } catch (IOException e) {
                LOGGER.error("VisBHttpServer: An error occurred while uploading {}: {}",
                        models[i] != null ? models[i] : "Unknown", e);
            }
        }
    }

    /**
     * Gets the data URI for all the models cached GLB files
     */
    public static List<String> getModelsDataUris()
    {
        if (modelFiles == null || modelFiles.size() == 0) {
            return null;
        }

        List<String> uris = new ArrayList<>();

        for (String fileName : modelFiles.keySet()) {
            if (fileName == null) {
                continue;
            }

            uris.add("http://localhost:" + PORT + "/" + fileName);
        }

        return uris;
    }

    /**
     * Handle GET requests for index and static files
     */
    static class IndexHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Allow Cross-Origin Resource Sharing (CORS)
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            // Only handle GET requests
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            // Determine requested path
            String path = exchange.getRequestURI().getPath();
            String pathFile = path.substring(1);
            if (path.equals("/")) {
                path = "/index.html";

            } else if (path.equals("/" + glbFileName) && glbData != null) {
                exchange.getResponseHeaders().add("Content-Type", "model/gltf-binary");
                exchange.sendResponseHeaders(200, glbData.length);
                exchange.getResponseBody().write(glbData);
                exchange.close();
                return;
            } else if (modelFiles.containsKey(pathFile) && modelFiles.get(pathFile) != null) {
                exchange.getResponseHeaders().add("Content-Type", "model/gltf-binary");
                exchange.sendResponseHeaders(200, modelFiles.get(pathFile).length);
                exchange.getResponseBody().write(modelFiles.get(pathFile));
                exchange.close();
                return;
            }

            // Serve static files from ROOT_DIR
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
    
    }
}