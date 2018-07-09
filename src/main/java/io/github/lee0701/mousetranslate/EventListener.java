package io.github.lee0701.mousetranslate;

import io.github.ranolp.rattranslate.RatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        MousePlayer mousePlayer = MousePlayer.of(event.getPlayer());
        if(mousePlayer != null) {
            mousePlayer.setChatFormat(event.getFormat());
            mousePlayer.setNickname(event.getPlayer().getDisplayName());
        }

        String nickname = event.getPlayer().getName();
        String message = ChatColor.stripColor(event.getMessage());
        RatPlayer ratPlayer = RatPlayer.of(event.getPlayer());
        MouseTranslate.getInstance().sendTranslatedMessages("[Minecraft] " + nickname, ratPlayer.getLocale(), message);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String name = MouseTranslate.getInstance().getBotName();
        String message = event.getPlayer().getName() + " joined.";
        MouseTranslate.getInstance().sendDiscordMessages(name, message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String name = MouseTranslate.getInstance().getBotName();
        String message = event.getPlayer().getName() + " quit.";
        MouseTranslate.getInstance().sendDiscordMessages(name, message);
    }

}
