package io.github.lee0701.mousetranslate;

import net.dv8tion.jda.core.entities.Guild;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DiscordMessageHandler extends BukkitRunnable {

    private Queue<DiscordMessage> messageQueue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while(true) {
            if(!messageQueue.isEmpty()) {
                Guild guild = null;
                while(!messageQueue.isEmpty()) {
                    DiscordMessage message = messageQueue.poll();
                    guild = message.getTextChannel().getGuild();
                    guild.getController().setNickname(guild.getSelfMember(), message.getName()).complete();
                    message.getTextChannel().sendMessage(message.getMessage()).complete();
                }
                if(guild != null) guild.getController().setNickname(guild.getSelfMember(), MouseTranslate.getInstance().getBotName()).complete();
            }
        }
    }

    public void offerMessage(DiscordMessage message) {
        messageQueue.offer(message);
    }

}
