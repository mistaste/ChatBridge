package me.mistaste.chatbridge.events;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.ToString;
import java.util.Date;

@Getter
@ToString
public class WSMessage {
    private static final Gson gson = new Gson();

    private final String playerName;
    private final String message;
    private final String serverName;
    private final long timestamp;

    public WSMessage(String playerName, String message, String serverName) {
        this.playerName = playerName;
        this.message = message;
        this.serverName = serverName;
        this.timestamp = new Date().getTime();
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static WSMessage fromJson(String json) throws JsonSyntaxException {
        return gson.fromJson(json, WSMessage.class);
    }

    public boolean isValid() {
        return playerName != null && !playerName.isEmpty()
                && message != null && !message.isEmpty()
                && serverName != null && !serverName.isEmpty();
    }
}
