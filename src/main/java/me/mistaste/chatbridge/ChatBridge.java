package me.mistaste.chatbridge;

import lombok.Getter;
import lombok.Setter;
import me.mistaste.chatbridge.commands.Command;
import me.mistaste.chatbridge.commands.CommandCompleter;
import me.mistaste.chatbridge.events.EventBus;
import me.mistaste.chatbridge.events.WSMessage;
import me.mistaste.chatbridge.websocket.WSClient;
import me.mistaste.chatbridge.websocket.WSServer;
import me.mistaste.mstapi.commands.BaseCommand;
import me.mistaste.mstapi.commands.BaseCompleter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public final class ChatBridge extends JavaPlugin {
    private static ChatBridge instance;
    private ConfigManager configm;
    private EventBus eventBus;
    private WSServer wsServer;
    private WSClient wsClient;

    @Override
    public void onEnable() {
        instance = this;
        configm = new ConfigManager(this);
        eventBus = new EventBus();

        Command command = new Command();
        CommandCompleter commandCompleter = new CommandCompleter();
        BaseCompleter.register(this, command, commandCompleter);
        BaseCommand.register(this, command);

        if (configm.isServerMode()) {
            startServer();
        } else {
            startClient();
        }

        new ChatListener(this).register();
        getLogger().info("Plugin enabled in " + (configm.isServerMode() ? "SERVER" : "CLIENT") + " mode");
    }

    public boolean isServerPrefixEnabled() {
        return configm.isServerPrefixEnabled();
    }

    private void startServer() {
        try {
            wsServer = new WSServer(configm.getPort());
            wsServer.start();
        } catch (Exception e) {
            getLogger().severe("Failed to start WebSocket server: " + e.getMessage());
        }
    }

    private void startClient() {
        try {
            wsClient = new WSClient(configm.getServerAddress());
            wsClient.connect();
        } catch (Exception e) {
            getLogger().severe("Failed to connect to WebSocket server: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        try {
            if (wsServer != null) wsServer.stop();
            if (wsClient != null) wsClient.close();
            if (eventBus != null) eventBus.shutdown();
        } catch (Exception e) {
            getLogger().severe("Error during shutdown: " + e.getMessage());
        }
        getLogger().info("Plugin disabled");
    }

    public void reloadConfiguration() throws InterruptedException {
        configm.reload();
        if (configm.isServerMode()) {
            if (wsClient != null) {
                wsClient.close();
                wsClient = null;
            }
            startServer();
        } else {
            if (wsServer != null) {
                wsServer.stop();
                wsServer = null;
            }
            startClient();
        }
        getLogger().info("Configuration reloaded and WebSocket " + (configm.isServerMode() ? "server" : "client") + " restarted");
    }

    public void broadcastMessage(String playerName, String message) {
        WSMessage wsMessage = new WSMessage(playerName, message, configm.getServerName());
        String json = wsMessage.toJson();

        if (configm.isServerMode() && wsServer != null) {
            getLogger().info("Broadcasting message to all clients: " + json);
            wsServer.broadcast(json);
        } else if (wsClient != null && wsClient.isOpen()) {
            getLogger().info("Sending message to server: " + json);
            wsClient.send(json);
        } else {
            getLogger().warning("No active WebSocket connection to broadcast message.");
        }
    }

    public void broadcastMessageFromServer(String playerName, String message) {
        WSMessage wsMessage = new WSMessage(playerName, message, configm.getServerName());
        String json = wsMessage.toJson();

        if (wsServer != null) {
            getLogger().info("Broadcasting message to all clients: " + json);
            wsServer.broadcast(json);
        } else {
            getLogger().warning("WebSocket server is not running. Message not broadcasted.");
        }
    }

    public boolean isClientConnected() {
        return wsClient != null && wsClient.isOpen();
    }

    public ConfigManager getConfigManager() {
        return configm;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public WSClient getWebSocketClient() {
        return wsClient;
    }

    public WSServer getWebSocketServer() {
        return wsServer;
    }

    public static ChatBridge getInstance() {
        return instance;
    }
}
