package me.mistaste.chatbridge.commands;

import me.mistaste.mstapi.commands.BaseCompleter;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandCompleter extends BaseCompleter {
    public CommandCompleter() {
        super("chatbridge", "chatb");
    }

    @Override
    public List<String> complete(CommandSender commandSender, String[] strings) {
        if (strings.length == 1) {
            return Arrays.asList("reload", "restart");
        }
        return Collections.emptyList();
    }
}
