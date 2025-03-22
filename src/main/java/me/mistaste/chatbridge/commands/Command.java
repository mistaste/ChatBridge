package me.mistaste.chatbridge.commands;

import me.mistaste.chatbridge.ChatBridge;
import me.mistaste.chatbridge.ConfigManager;
import me.mistaste.mstapi.commands.BaseCommand;
import org.bukkit.command.CommandSender;

public class Command extends BaseCommand {
    public Command() {
        super("chatbridge", "chatb");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage("Usage: /chatbridge <reload/restart>");
            return;
        }

        if (strings[0].equalsIgnoreCase("reload")) {
            ChatBridge.getInstance().reloadConfig();
        }

        if (strings[0].equalsIgnoreCase("restart")) {
            try {
                ChatBridge.getInstance().reloadConfiguration();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
