package io.github.lee0701.mousetranslate;

import io.github.ranolp.rattranslate.Locale;
import io.github.ranolp.rattranslate.RatTranslate;
import io.github.ranolp.rattranslate.translator.Translator;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
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
                    if(message instanceof TranslatingDiscordMessage) {
                        Locale fromLocale = ((TranslatingDiscordMessage) message).getFromLocale();
                        StringBuilder translatedMessage = new StringBuilder();
                        Translator translator = RatTranslate.getInstance().getTranslator();
                        boolean auto = true;
                        translatedMessage.append(message.getName());
                        translatedMessage.append(":\n");
                        for(String locale : MouseTranslate.getInstance().getLanguages()) {
                            Locale toLocale = Locale.getByCode(locale);
                            translatedMessage.append(auto ? translator.translateAuto(message.getMessage(), toLocale) : translator.translate(message.getMessage(), fromLocale, toLocale));
                            translatedMessage.append('\n');
                        }
                        translatedMessage.deleteCharAt(translatedMessage.lastIndexOf("\n"));
                        message.getTextChannel().sendMessage(translatedMessage.toString()).complete();
                    } else {
                        message.getTextChannel().sendMessage(message.getMessage()).complete();
                    }
                }
                guild.getController().setNickname(guild.getSelfMember(), MouseTranslate.getInstance().getBotName()).complete();
            }
        }
    }

    public void offerMessage(DiscordMessage message) {
        messageQueue.offer(message);
    }

}
