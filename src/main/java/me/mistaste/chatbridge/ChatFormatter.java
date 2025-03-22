package me.mistaste.chatbridge;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ChatFormatter {
    private static final Gson gson = new Gson();

    public static String toJson(String playerName, String message, String serverName) {
        JsonObject json = new JsonObject();
        json.addProperty("player", playerName);
        json.addProperty("message", message);
        json.addProperty("server", serverName);
        return gson.toJson(json);
    }

    public static ChatMessage fromJson(String json) {
        return gson.fromJson(json, ChatMessage.class);
    }

    public static class ChatMessage {
        private String playerName;
        private String message;
        private String serverName;

        public String getPlayerName() { return playerName; }
        public String getMessage() { return message; }
        public String getServerName() { return serverName; }
    }
}
