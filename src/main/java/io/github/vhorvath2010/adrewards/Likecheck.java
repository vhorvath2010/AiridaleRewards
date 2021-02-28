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
                ArrayList<String> claimedIPs = ADRewards.instance.getClaimedIPs();
                if (claimed.contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You've already claimed your reward!");
                    return false;
                }
                if (claimedIPs.contains(player.getAddress().toString())) {
                    player.sendMessage(ChatColor.RED + "A like from your IP address has already been claimed!");
                    return false;
                }
                FileConfiguration config = ADRewards.instance.getConfig();
                // Check if player voted
                try {
                    URL url = new URL(config.getString("api-url") + player.getUniqueId().toString());
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
                        for (String section : config.getConfigurationSection("rewards").getKeys(false)) {
                            double chance = config.getDouble("rewards." + section + ".chance");
                            if (new Random().nextDouble() * 100 < chance) {
                                String cmd = config.getString("rewards." + section + ".cmd");
                                cmd = cmd.replace("%player%", player.getName());
                                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
                            }
                        }
                        claimed.add(player.getUniqueId());
                        claimedIPs.add(player.getAddress().toString());
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
