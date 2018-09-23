package io.github.lee0701.mousetranslate;

import io.github.lee0701.mousetranslate.message.MinecraftMessage;
import io.github.lee0701.mousetranslate.message.SimpleMessage;
import io.github.ranolp.rattranslate.RatPlayer;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    private final BotInstance bot = MouseTranslate.getInstance().getBot();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        MousePlayer.of(event.getPlayer()).ifPresent(mousePlayer -> {
            mousePlayer.setChatFormat(event.getFormat());
            mousePlayer.setNickname(event.getPlayer().getDisplayName());
        });

        String nickname = event.getPlayer().getName();
        String message = ChatColor.stripColor(event.getMessage());
        RatPlayer ratPlayer = RatPlayer.of(event.getPlayer());
        bot.sendDiscordMessages(channel -> new MinecraftMessage(
                "Minecraft",
                channel,
                nickname,
                message,
                ratPlayer.getLocale()
        ));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Message message = new MessageBuilder().setContent(event.getPlayer().getName() + " joined.").build();
        bot.sendDiscordMessages(channel -> new SimpleMessage(channel, message));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Message message = new MessageBuilder().setContent(event.getPlayer().getName() + " left.").build();
        bot.sendDiscordMessages(channel -> new SimpleMessage(channel, message));
    }

}
