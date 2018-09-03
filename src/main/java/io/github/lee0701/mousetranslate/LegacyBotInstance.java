package io.github.lee0701.mousetranslate;

import io.github.lee0701.mousetranslate.legacy.DiscordMessage;
import io.github.lee0701.mousetranslate.legacy.TranslatingDiscordMessage;
import io.github.ranolp.rattranslate.Locale;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;

public class LegacyBotInstance {
    private JDA jda;
    private LegacyDiscordMessageHandler discordMessageHandler;

    public void setToken(String token) {
        try {
            if (jda != null) {
                jda.shutdown();
            }
            jda = new JDABuilder(AccountType.BOT).setToken(token).buildAsync();
            jda.addEventListener(new DiscordChatListener());

            discordMessageHandler = new LegacyDiscordMessageHandler();
            discordMessageHandler.runTaskAsynchronously(MouseTranslate.getInstance());
        } catch (LoginException ex) {
            Bukkit.getLogger().warning("Error loading Discord bot.");
            ex.printStackTrace();
        }

    }

    public void sendDiscordMessage(TextChannel textChannel, String nickname, String message) {
        discordMessageHandler.offerMessage(new DiscordMessage(textChannel, nickname, message));
    }

    public void sendDiscordMessages(String nickname, String messge) {
        String serverId = MouseTranslate.getInstance().getServerId();
        for (String channelId : MouseTranslate.getInstance().getChannels()) {
            TextChannel textChannel = jda.getGuildById(serverId).getTextChannelById(channelId);
            sendDiscordMessage(textChannel, nickname, messge);
        }
    }

    public void sendMinecraftMessage(TextChannel textChannel, String nickname, Locale fromLocale, String message) {
        discordMessageHandler.offerMessage(new TranslatingDiscordMessage(textChannel, nickname, fromLocale, message));
    }

    public void sendMinecraftMessage(String nickname, Locale fromLocale, String message) {
        String serverId = MouseTranslate.getInstance().getServerId();
        for (String channelId : MouseTranslate.getInstance().getChannels()) {
            TextChannel textChannel = jda.getGuildById(serverId).getTextChannelById(channelId);
            sendMinecraftMessage(textChannel, nickname, fromLocale, message);
        }
    }

    public JDA getJda() {
        return jda;
    }
}
