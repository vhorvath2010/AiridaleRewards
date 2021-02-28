package io.github.vhorvath2010.adrewards;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetLikes implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("ResetLikes")) {
            ADRewards.instance.getClaimed().clear();
            commandSender.sendMessage(ChatColor.RED + "Cleared all likes");
        }
        return false;
    }
}
