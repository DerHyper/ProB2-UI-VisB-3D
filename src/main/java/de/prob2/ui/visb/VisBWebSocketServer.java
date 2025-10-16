package de.prob2.ui.visb;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Modified verion of the "VisBWebSocketServe Example" from 
/// https://github.com/TooTallNate/Java-WebSocket/wiki#server-example, 05.09.2025, Java-WebSocket Server Example
public class VisBWebSocketServer extends WebSocketServer {

	private static VisBWebSocketServer instance;
	private static final Logger LOGGER = LoggerFactory.getLogger(VisBWebSocketServer.class);

	public VisBWebSocketServer(InetSocketAddress address) {
		super(address);
		this.instance = this;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conn.send("Welcome to the server!"); //This method sends a message to the new client
		broadcast("new connection: " + handshake.getResourceDescriptor()); //This method sends a message to all clients connected
		LOGGER.info("VisBWebSocketServe: New connection to {}", conn.getRemoteSocketAddress());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		LOGGER.info(
			"VisBWebSocketServe: Closed connection to {} with exit code {} additional info: {}",
			conn.getRemoteSocketAddress(), code, reason);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		LOGGER.info("VisBWebSocketServe: Received message from {}: {}", conn.getRemoteSocketAddress(), message);
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message ) {
		LOGGER.info("VisBWebSocketServe: Received ByteBuffer from {}", conn.getRemoteSocketAddress());
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		LOGGER.error("VisBWebSocketServe: An error occurred on connection {}: {}", conn != null ? conn.getRemoteSocketAddress() : "Unknown", ex);
	}
	
	@Override
	public void onStart() {
		LOGGER.info("VisBWebSocketServe: Server started successfully");
	}

	/// WebSocket-Server (ws://localhost:8081/)
	public static void startServerThread() {
		String host = "localhost";
		int port = 8081;

		// Start WebSocket server as daemon thread
		WebSocketServer server = new VisBWebSocketServer(new InetSocketAddress(host, port));
		Thread serverThread = new Thread(server::run);
		serverThread.setDaemon(true);
		serverThread.start();
	}

	public static void broadcastMessage(String message) {
		// Broadcast message to all connected clients
		if (instance != null)
			instance.broadcast(message);
	}
}