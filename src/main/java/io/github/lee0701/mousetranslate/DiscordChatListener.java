package io.github.lee0701.mousetranslate;

import io.github.ranolp.rattranslate.*;
import io.github.ranolp.rattranslate.lang.LangStorage;
import io.github.ranolp.rattranslate.lang.Variable;
import io.github.ranolp.rattranslate.translator.Translator;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class DiscordChatListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        JDA jda = event.getJDA();

        User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        String msg = message.getContentDisplay();

        boolean bot = author.isBot();

        if(event.isFromType(ChannelType.TEXT)) {
            Guild guild = event.getGuild();
            TextChannel textChannel = event.getTextChannel();
            Member member = event.getMember();

            if(!guild.getId().equals(MouseTranslate.getInstance().getServerId())) return;
            if(member.equals(guild.getSelfMember())) return;

            String name;
            if(message.isWebhookMessage()) name = author.getName();
            else name = member.getEffectiveName();

            Bukkit.getLogger().info(String.format("(%s)[%s]<%s>: %s", guild.getName(), textChannel.getName(), name, msg));

            if(MouseTranslate.getInstance().getChannels().contains(textChannel.getId())) {
                broadcastTranslatedChat(name, msg);
                message.delete().queue();
                MouseTranslate.getInstance().sendTranslatedMessage(textChannel, name, null, msg);
            }

        }
    }

    public void broadcastTranslatedChat(String username, String message) {
        Set<RatPlayer> recipients = new HashSet<>();
        for (Player bukkitPlayer: Bukkit.getServer().getOnlinePlayers()) {
            recipients.add(RatPlayer.of(bukkitPlayer));
        }

        LangStorage langStorage = RatTranslate.getInstance().getLangStorage();
        Translator translator = RatTranslate.getInstance().getTranslator();

        String format = "[Discord] %s: %s";

        String originalMessage = String.format(format, username, message);
        Collector<Locale, ?, Map<Locale, String>> collector = Collectors.toMap(locale -> locale,
                locale -> String.format(format,
                        username,
                        translator.translateAuto(message, locale)));

        Map<Locale, String> translateMap = recipients.stream().map(RatPlayer::getLocale).distinct().collect(collector);
        for (RatPlayer recipient: recipients) {
            if(recipient.getTranslateMode()) {
                String translated = translateMap.get(recipient.getLocale());
                if (RatTranslate.getInstance().isJsonMessageAvailable()) {
                    String hover = recipient.format(langStorage,
                            "chat.original",
                            Variable.ofAny("hover", "text", message),
                            Variable.ofAny("hover",
                                    "lang",
                                    "auto"));
                    recipient.sendHoverableMessage(translated, hover);
                } else {
                    recipient.sendMessage(translated);
                }
            }
            else {
                recipient.sendMessage(originalMessage);
            }
        }
    }

}
