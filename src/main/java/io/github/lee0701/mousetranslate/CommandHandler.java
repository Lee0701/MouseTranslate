package io.github.lee0701.mousetranslate;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args[0].equals("reload")) {
            if(sender.isOp()) {
                sender.sendMessage("Reloading config...");
                MouseTranslate.getInstance().reload();
            }
        }
        return false;
    }
}
