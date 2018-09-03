package io.github.lee0701.mousetranslate;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class CommandHandler implements CommandExecutor {

    private static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(26) + 0x41;
            if (random.nextBoolean()) { num += 0x20; }
            result.append((char) ((int) num));
        }
        return result.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /mousetranslate register");
            return true;
        }
        if (args[0].equals("reload")) {
            if (sender.isOp()) {
                sender.sendMessage("Reloading config...");
                MouseTranslate.getInstance().reload();
            }
            return true;
        }
        if (args[0].equals("register")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String registerCommand = "!register " + getRandomString(5);
                MousePlayer.REGISTER_MAP.put(registerCommand, player);
                sender.sendMessage("Type \"" + registerCommand + "\" in Discord chat to complete.");
            }
            return true;
        }
        return false;
    }

}
