package io.github.lee0701.mousetranslate;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MouseTranslate extends JavaPlugin {
    private final File dataFile = new File(getDataFolder(), "data.yml");
    private LegacyBotInstance bot = new LegacyBotInstance();
    private String serverId;
    private List<String> channels = new ArrayList<>();
    private List<String> languages = new ArrayList<>();
    private String botName;

    private YamlConfiguration dataConfiguration;

    public static MouseTranslate getInstance() {
        return getPlugin(MouseTranslate.class);
    }

    @Override
    public void onEnable() {
        //noinspection ResultOfMethodCallIgnored
        getDataFolder().mkdirs();
        saveDefaultConfig();

        reload();

        getCommand("mousetranslate").setExecutor(new CommandHandler());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

    }

    public void reload() {
        reloadConfig();

        channels.clear();
        languages.clear();

        FileConfiguration config = getConfig();
        String botToken = config.getString("bot-token");
        serverId = config.getString("server");
        channels = config.getStringList("channels");
        languages = config.getStringList("languages");

        if (botToken != null) {
            bot.setToken(botToken);
        } else {
            getLogger().warning("Discord bot token is not set. Disabling Discord bot.");
        }

        MousePlayer.PLAYER_MAP.clear();
        dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        if (dataConfiguration.isList("players")) {
            dataConfiguration.getList("players");
        }

    }

    public void save() {
        dataConfiguration.set("players", new ArrayList<>(MousePlayer.PLAYER_MAP.values()));
        try {
            dataConfiguration.save(dataFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        save();
    }

    public LegacyBotInstance getBot() {
        return bot;
    }

    public String getServerId() {
        return serverId;
    }

    public List<String> getChannels() {
        return channels;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }
}
