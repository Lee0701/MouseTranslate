package io.github.lee0701.mousetranslate;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandHandler implements TabExecutor {

    private final List<String> completes = new ArrayList<>(Arrays.asList("register", "reload"));

    private static String getRandomString(int length) {
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(26) + 0x41;
            if (random.nextBoolean()) { num += 0x20; }
            result.append((char) num);
        }
        return result.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            if (sender.isOp()) {
                sender.sendMessage("Usage:");
                sender.sendMessage("/" + label + " register");
                sender.sendMessage("/" + label + " reload");
            } else {
                sender.sendMessage("Usage: /" + label + " register");
            }
            return true;
        }
        if (args[0].equals("reload")) {
            if (sender.isOp()) {
                sender.sendMessage(ChatColor.GRAY + "Reload config...");
                MouseTranslate.getInstance().reload();
                sender.sendMessage(ChatColor.GREEN + "Reload complete!");
            }
            return true;
        }
        if (args[0].equals("register")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Registration registration = new Registration(getRandomString(5), player);
                MousePlayer.REGISTRATIONS.add(registration);

                String registerCommand = "!register " + registration.getKey();
                String url = "http:register/" + registration.getKey();
                TextComponent register = new TextComponent(registerCommand);
                register.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[] {new TextComponent("or click to copy an alternative command")}
                ));
                register.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
                register.setColor(ChatColor.AQUA.asBungee());
                TextComponent root = new TextComponent(
                        new TextComponent("Type \""), register, new TextComponent("\" in Discord chat to complete."));
                sender.spigot().sendMessage(root);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> copied;
        if (sender.isOp()) {
            copied = new ArrayList<>(completes);
        } else {
            copied = new ArrayList<>(Collections.singletonList("register"));
        }
        if (args.length == 1) {
            copied.removeIf(it -> !it.startsWith(args[0]));
            return copied;
        } else {
            return Collections.emptyList();
        }
    }
}
