package io.github.lee0701.mousetranslate;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MousePlayer implements ConfigurationSerializable {

    static final Map<String, MousePlayer> PLAYER_MAP = new HashMap<>();
    static final Map<String, Player> REGISTER_MAP = new HashMap<>();
    private String nickname;
    private String discordId;
    private String uuid;
    private String chatFormat;

    public MousePlayer(String discordId) {
        this.discordId = discordId;
    }

    public static MousePlayer of(Player player) {
        Objects.requireNonNull(player, "player");
        try {
            return PLAYER_MAP.values()
                    .stream()
                    .filter(e -> e.uuid.equals(player.getUniqueId().toString()))
                    .findFirst()
                    .orElse(null);
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public static MousePlayer of(String discordId) {
        Objects.requireNonNull(discordId, "discordId");
        return PLAYER_MAP.computeIfAbsent(discordId, MousePlayer::new);
    }

    public static MousePlayer deserialize(Map<String, Object> args) {
        MousePlayer result = MousePlayer.of((String) args.get("discordId"));
        Object chatFormat = args.get("chat-format");
        if (chatFormat instanceof String) { result.chatFormat = (String) chatFormat; }
        Object uuid = args.get("uuid");
        if (uuid instanceof String) { result.uuid = (String) uuid; }
        Object nickname = args.get("nickname");
        if (nickname instanceof String) { result.nickname = (String) nickname; }
        return result;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("discordId", discordId);
        if (uuid != null) { result.put("uuid", uuid); }
        if (nickname != null) { result.put("nickname", nickname); }
        if (chatFormat != null) { result.put("chat-format", chatFormat); }
        return result;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getChatFormat() {
        return chatFormat;
    }

    public void setChatFormat(String chatFormat) {
        this.chatFormat = chatFormat;
    }
}
