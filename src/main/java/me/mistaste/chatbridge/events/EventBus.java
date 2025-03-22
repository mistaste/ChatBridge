package me.mistaste.chatbridge.events;

import lombok.Getter;
import me.mistaste.chatbridge.ChatBridge;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Getter
public class EventBus {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ConcurrentLinkedQueue<Consumer<String>> messageHandlers = new ConcurrentLinkedQueue<>();

    public void subscribe(Consumer<String> handler) {
        messageHandlers.add(handler);
    }

    public void postMessage(String message) {
        executor.submit(() -> {
            if (messageHandlers != null) {
                for (Consumer<String> handler : messageHandlers) {
                    try {
                        handler.accept(message);
                    } catch (Exception e) {
                        ChatBridge.getInstance().getLogger().severe("Error handling message: " + e.getMessage());
                    }
                }
            }
        });
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
