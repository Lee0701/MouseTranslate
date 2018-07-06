package io.github.lee0701.mousetranslate;

import io.github.ranolp.rattranslate.RatTranslate;
import io.github.ranolp.rattranslate.Locale;
import io.github.ranolp.rattranslate.translator.Translator;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MouseTranslate extends JavaPlugin {

    public static MouseTranslate getInstance() {
        return getPlugin(MouseTranslate.class);
    }

    private JDA jda;
    private String botToken;
    private String serverId;
    private List<String> channels = new ArrayList<>();
    private List<String> languages = new ArrayList<>();
    private String botName;

    private final File dataFile = new File(getDataFolder(), "data.yml");
    private YamlConfiguration dataConfiguration;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        saveDefaultConfig();

        reload();

        getCommand("mousetranslate").setExecutor(new CommandHandler());
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

    }

    public void reload() {
        reloadConfig();

        channels.clear();
        languages.clear();

        FileConfiguration config = getConfig();
        botToken = config.getString("bot-token");
        serverId = config.getString("server");
        channels = config.getStringList("channels");
        languages = config.getStringList("languages");

        if(botToken != null) {
            try {
                jda = new JDABuilder(AccountType.BOT)
                        .setToken(botToken)
                        .buildAsync();
                jda.addEventListener(new DiscordChatListener());
            } catch(LoginException ex) {
                getLogger().warning("Error loading Discord bot.");
                ex.printStackTrace();
            }
        } else {
            getLogger().warning("Discord bot token is not set. Disabling Discord bot.");
        }

        MousePlayer.PLAYER_MAP.clear();
        dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        if(dataConfiguration.isList("players")) {
            dataConfiguration.getList("players");
        }

    }

    public void save() {
        dataConfiguration.set("players", new ArrayList<>(MousePlayer.PLAYER_MAP.values()));
        try {
            dataConfiguration.save(dataFile);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        save();
    }

    public void sendDiscordMessage(TextChannel textChannel, String nickname, String message) {
        Guild guild = textChannel.getGuild();
        guild.getController().setNickname(guild.getSelfMember(), nickname).complete();
        textChannel.sendMessage(message).complete();
        guild.getController().setNickname(guild.getSelfMember(), botName).complete();
    }

    public void sendTranslatedMessage(TextChannel textChannel, String nickname, Locale fromLocale, String message) {
        StringBuilder translatedMessage = new StringBuilder();
        Translator translator = RatTranslate.getInstance().getTranslator();
        boolean auto = true;
        for(String locale : languages) {
            Locale toLocale = Locale.getByCode(locale);
            translatedMessage.append(auto ? translator.translateAuto(message, toLocale) : translator.translate(message, fromLocale, toLocale));
            translatedMessage.append('\n');
        }
        translatedMessage.deleteCharAt(translatedMessage.lastIndexOf("\n"));

        sendDiscordMessage(textChannel, nickname, translatedMessage.toString());
    }

    public void sendTranslatedMessages(String nickname, Locale fromLocale, String message) {
        for(String channelId : channels) {
            TextChannel textChannel = jda.getGuildById(serverId).getTextChannelById(channelId);
            sendTranslatedMessage(textChannel, nickname, fromLocale, message);
        }
    }

    public JDA getJda() {
        return jda;
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
