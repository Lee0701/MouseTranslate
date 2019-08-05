package io.github.lee0701.mousetranslate;

import io.github.lee0701.mousetranslate.message.MinecraftMessage;
import io.github.ranolp.rattranslate.Locale;
import io.github.ranolp.rattranslate.RatPlayer;
import io.github.ranolp.rattranslate.RatTranslate;
import io.github.ranolp.rattranslate.lang.LangStorage;
import io.github.ranolp.rattranslate.lang.Variable;
import io.github.ranolp.rattranslate.translator.Translator;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class DiscordChatListener extends ListenerAdapter {

    private static final String COMMAND_PREFIX = "/";

    private final BotInstance bot = MouseTranslate.getInstance().getBot();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }

        Guild guild = event.getGuild();

        if (!guild.getId().equals(MouseTranslate.getInstance().getServerId())) {
            return;
        }

        Member member = event.getMember();

        if (guild.getSelfMember().equals(member)) {
            return;
        }

        Message message = event.getMessage();

        if (message.isWebhookMessage()) {
            return;
        }

        if(handleCommand(message)) return;

        User author = event.getAuthor();
        String msg = message.getContentDisplay();

        Registration registration = MousePlayer.REGISTRATIONS.stream()
                .filter(it -> it.isCommandMatches(msg))
                .findFirst()
                .orElse(null);
        if (registration != null) {
            Player player = registration.getPlayer();

            MousePlayer mousePlayer = MousePlayer.of(author.getId());
            mousePlayer.setNickname(player.getDisplayName());
            mousePlayer.setUuid(player.getUniqueId().toString());

            player.sendMessage("Discord register complete!");

            MousePlayer.REGISTRATIONS.remove(registration);
            message.delete().queue();

            return;
        }

        TextChannel textChannel = event.getTextChannel();

        String name = member.getEffectiveName();
        String minecraftName = name;

        String format = "[Discord] <%s> %s";

        MousePlayer mousePlayer = MousePlayer.of(author.getId());
        if (mousePlayer.getUuid() != null) {
            if (mousePlayer.getChatFormat() != null) {
                format = "[Discord] " + mousePlayer.getChatFormat();
            }
            if (mousePlayer.getNickname() != null) {
                minecraftName = mousePlayer.getNickname();
            }
        }

        if (MouseTranslate.getInstance().getChannels().contains(textChannel.getId())) {
            Bukkit.getLogger()
                    .info(String.format("(%s)[%s]<%s>: %s", guild.getName(), textChannel.getName(), name, msg));
            broadcastTranslatedChat(format, minecraftName, msg);
            bot.sendDiscordMessage(
                    new MinecraftMessage("Discord", textChannel, name, msg, null, IconStorage.getIconFor(author),
                            mousePlayer.isConnected()
                    ));
            message.delete().queue();
        }
    }

    private boolean handleCommand(Message message) {
        StringTokenizer tokens = new StringTokenizer(message.getContentDisplay());

        String command;
        if(tokens.hasMoreTokens()) command = tokens.nextToken();
        else return false;

        String[] args = new String[tokens.countTokens()];
        for(int i = 0 ; i < args.length ; i++) args[i] = tokens.nextToken();

        command = command.toLowerCase();
        if(command.startsWith(COMMAND_PREFIX)) command = command.substring(1);
        else return false;

        switch(command) {
        case "list":
            List<Player> players = new ArrayList<>(MouseTranslate.getInstance().getServer().getOnlinePlayers());
            String reply = "List of online players:\n";
            reply += players.stream().map(Player::getPlayerListName).map(name -> "- " + name).collect(Collectors.joining("\n"));
            message.getChannel().sendMessage(reply).queue();
            return true;
        default:
            return false;
        }
    }

    private void broadcastTranslatedChat(String format, String username, String message) {
        Set<RatPlayer> recipients = Bukkit.getServer()
                .getOnlinePlayers()
                .stream()
                .map(RatPlayer::of)
                .collect(Collectors.toSet());

        LangStorage langStorage = RatTranslate.getInstance().getLangStorage();
        Translator translator = RatTranslate.getInstance().getTranslator();

        String originalMessage = String.format(format, username, message);

        Map<Locale, String> translateMap = recipients.stream()
                .map(RatPlayer::getLocale)
                .distinct()
                .collect(Collectors.toMap(locale -> locale,
                        locale -> String.format(format, username, translator.translateAuto(message, locale))
                ));
        for (RatPlayer recipient : recipients) {
            if (recipient.getTranslateMode()) {
                String translated = translateMap.get(recipient.getLocale());
                if (RatTranslate.getInstance().isJsonMessageAvailable()) {
                    String hover = recipient.format(langStorage, "chat.original",
                            Variable.ofAny("hover", "text", message), Variable.ofAny("hover", "lang", "auto")
                    );
                    recipient.sendHoverableMessage(translated, hover);
                } else {
                    recipient.sendMessage(translated);
                }
            } else {
                recipient.sendMessage(originalMessage);
            }
        }
    }

}
