package io.github.lee0701.mousetranslate;

import org.bukkit.entity.Player;

public class Registration {
    private final String key;
    private final Player player;

    public Registration(String key, Player player) {
        this.key = key;
        this.player = player;
    }

    public String getKey() {
        return key;
    }

    public boolean isCommandMatches(String command) {
        return ("http:register/" + key).equals(command) || ("!register " + key).equals(command);
    }

    public Player getPlayer() {
        return player;
    }
}
