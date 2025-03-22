package me.mistaste.chatbridge.websocket;

import lombok.Getter;
import me.mistaste.chatbridge.ChatBridge;
import me.mistaste.chatbridge.events.EventBus;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;

@Getter
public class WSServer extends WebSocketServer {
    private final EventBus eventBus;

    public WSServer(int port) {
        super(new InetSocketAddress(port));
        this.eventBus = ChatBridge.getInstance().getEventBus();
        setReuseAddr(true);
        setConnectionLostTimeout(60);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        ChatBridge.getInstance().getLogger().info("Authorized connection from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        ChatBridge.getInstance().getLogger().info("Received message: " + message);
        if (eventBus != null) {
            eventBus.postMessage(message);
        }
        broadcast(message); // Ensure the message is broadcasted to all connected clients
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        ChatBridge.getInstance().getLogger().info(
                "Connection closed: " + conn.getRemoteSocketAddress() + " (" + reason + ")"
        );
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ChatBridge.getInstance().getLogger().severe("WebSocket error: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        ChatBridge.getInstance().getLogger().info(
                "WebSocket server started on port " + getPort()
        );
    }
}
