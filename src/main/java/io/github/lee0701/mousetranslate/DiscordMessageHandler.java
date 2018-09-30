package io.github.lee0701.mousetranslate;

import io.github.lee0701.mousetranslate.message.MouseMessage;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DiscordMessageHandler extends BukkitRunnable {
    private Queue<MouseMessage> messageQueue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while (true) {
            while (!messageQueue.isEmpty()) {
                MouseMessage message = messageQueue.poll();
                message.send();
            }
        }
    }

    public void offerMessage(MouseMessage message) {
        messageQueue.offer(message);
    }
}
