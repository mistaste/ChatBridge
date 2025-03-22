package me.mistaste.chatbridge;

import lombok.Getter;
import me.mistaste.chatbridge.events.EventBus;
import me.mistaste.chatbridge.events.WSMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@Getter
public class ChatListener implements Listener {
    private final ChatBridge plugin;
    private final EventBus eventBus;

    public ChatListener(ChatBridge plugin) {
        this.plugin = plugin;
        this.eventBus = plugin.getEventBus();
        eventBus.subscribe(this::handleIncomingMessage);
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String formattedMessage = plugin.getConfigManager().getFormat()
                .replace("%player%", event.getPlayer().getName())
                .replace("%message%", event.getMessage());

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (plugin.getConfigManager().isServerMode()) {
                plugin.broadcastMessageFromServer(event.getPlayer().getName(), formattedMessage);
            } else if (plugin.isClientConnected()) {
                plugin.broadcastMessage(event.getPlayer().getName(), formattedMessage);
            } else {
                plugin.getLogger().warning("WebSocket client is not connected. Message not sent.");
            }
        });
    }

    private void handleIncomingMessage(String message) {
        if (plugin != null && eventBus != null) {
            ChatFormatter.ChatMessage chatMessage = ChatFormatter.fromJson(message);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                String formattedMessage = chatMessage.getMessage();
                if (plugin.isServerPrefixEnabled()) {
                    formattedMessage = "[" + chatMessage.getServerName() + "] " + formattedMessage;
                }
                plugin.getServer().broadcastMessage(formattedMessage);
            });
        }
    }
}
