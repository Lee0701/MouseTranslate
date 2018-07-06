package io.github.lee0701.mousetranslate;

import io.github.ranolp.rattranslate.RatPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                MousePlayer mousePlayer = MousePlayer.of(event.getPlayer());
                if(mousePlayer != null) {
                    mousePlayer.setChatFormat(event.getFormat());
                    mousePlayer.setNickname(event.getPlayer().getDisplayName());
                }

                String nickname = event.getPlayer().getName();
                String message = event.getMessage();
                RatPlayer ratPlayer = RatPlayer.of(event.getPlayer());
                MouseTranslate.getInstance().sendTranslatedMessages("[Minecraft] " + nickname, ratPlayer.getLocale(), message);
            }
        }.runTaskAsynchronously(MouseTranslate.getInstance());
    }

}
