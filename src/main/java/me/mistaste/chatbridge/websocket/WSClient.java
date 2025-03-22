package me.mistaste.chatbridge.websocket;

import lombok.Getter;
import me.mistaste.chatbridge.ChatBridge;
import me.mistaste.chatbridge.events.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

@Getter
public class WSClient extends WebSocketClient {
    private final EventBus eventBus;

    public WSClient(String serverUri) {
        super(URI.create(serverUri));
        this.eventBus = ChatBridge.getInstance().getEventBus();
        setConnectionLostTimeout(30);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        ChatBridge.getInstance().getLogger().info("Successfully connected to server");
    }

    @Override
    public void onMessage(String message) {
        if (eventBus != null) {
            eventBus.postMessage(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        ChatBridge.getInstance().getLogger().warning(
                "Connection closed: " + reason + " (Code: " + code + ")"
        );
    }

    @Override
    public void onError(Exception ex) {
        ChatBridge.getInstance().getLogger().severe("WebSocket error: " + ex.getMessage());
    }
}
