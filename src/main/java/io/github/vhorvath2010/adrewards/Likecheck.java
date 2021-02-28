package io.github.vhorvath2010.adrewards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Likecheck implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("likecheck")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ArrayList<UUID> claimed = ADRewards.instance.getClaimed();
                if (claimed.contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You've already claimed your reward!");
                    return false;
                }
                // Check if player voted
                try {
                    URL url = new URL("https://api.namemc.com/server/play.airidale.net/likes?profile=" + player.getUniqueId().toString());
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    InputStream input = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String response = reader.readLine();
                    if (response.equalsIgnoreCase("false")) {
                        player.sendMessage(ChatColor.RED + "You have not yet liked the server!");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "Thank you for liking the server!");
                        // Give rewards
                        FileConfiguration config = ADRewards.instance.getConfig();
                        for (String section : config.getConfigurationSection("rewards").getKeys(false)) {
                            double chance = config.getDouble("rewards." + section + ".chance");
                            if (new Random().nextDouble() * 100 < chance) {
                                String cmd = config.getString("rewards." + section + ".cmd");
                                cmd = cmd.replace("%player%", player.getName());
                                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
                            }
                        }
                        claimed.add(player.getUniqueId());
                        ADRewards.instance.saveData();
                    }
                    return true;
                } catch (IOException e) {
                    player.sendMessage(ChatColor.RED + "Error fetching data");
                }
            }
        }
        return false;
    }
}
