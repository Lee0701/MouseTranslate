package io.github.lee0701.mousetranslate;

import io.github.lee0701.mousetranslate.message.MouseMessage;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;
import java.util.function.Function;

public class BotInstance {
    private JDA jda;
    private DiscordMessageHandler discordMessageHandler;

    public void setToken(String token) {
        try {
            if (jda != null) {
                jda.shutdown();
            }
            jda = new JDABuilder(AccountType.BOT).setToken(token).buildAsync();
            jda.addEventListener(new DiscordChatListener());

            discordMessageHandler = new DiscordMessageHandler();
            discordMessageHandler.runTaskAsynchronously(MouseTranslate.getInstance());
        } catch (LoginException ex) {
            Bukkit.getLogger().warning("Error loading Discord bot.");
            ex.printStackTrace();
        }

    }

    public void sendDiscordMessages(Function<TextChannel, MouseMessage> messageGenerator) {
        String serverId = MouseTranslate.getInstance().getServerId();
        for (String channelId : MouseTranslate.getInstance().getChannels()) {
            TextChannel textChannel = jda.getGuildById(serverId).getTextChannelById(channelId);
            sendDiscordMessage(messageGenerator.apply(textChannel));
        }
    }

    public void sendDiscordMessage(MouseMessage message) {
        discordMessageHandler.offerMessage(message);
    }

    public JDA getJda() {
        return jda;
    }

    public void dispose() {
        jda.shutdownNow();
    }
}
