package me.mistaste.chatbridge;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class ConfigManager {
    private final ChatBridge plugin;
    private FileConfiguration config;

    public ConfigManager(ChatBridge plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public boolean isServerMode() {
        return config.getBoolean("isServer", false);
    }

    public int getPort() {
        return config.getInt("port", 8888);
    }

    public String getServerAddress() {
        return config.getString("serverAddress", "ws://localhost:8888");
    }

    public String getServerName() {
        return config.getString("serverName", "Unnamed Server");
    }

    public boolean isServerPrefixEnabled() {
        return config.getBoolean("serverPrefixEnabled", true);
    }

    public String getFormat() {
        return config.getString("format", "%player% -> %message%");
    }
}
