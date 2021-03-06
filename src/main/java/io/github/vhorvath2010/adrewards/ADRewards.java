package io.github.vhorvath2010.adrewards;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ADRewards extends JavaPlugin {

    public static ADRewards instance;
    private ArrayList<String> claimed;
    private ArrayList<String> claimedIPs;

    public ArrayList<String> getClaimed() {
        return claimed;
    }

    public ArrayList<String> getClaimedIPs() {
        return claimedIPs;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            loadDataFile();
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("Could not load data");
            e.printStackTrace();
        }
        getCommand("likecheck").setExecutor(new Likecheck());
        getCommand("ResetLikes").setExecutor(new ResetLikes());
        instance = this;
    }

    @Override
    public void onDisable() {
        try {
            saveData();
        } catch (IOException e) {
            System.out.println("Failed to save data");
        }
    }

    public void saveData() throws IOException {
        File file = new File(getDataFolder() + "/data.yml");
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("data", claimed);
        configuration.set("ip-data", claimedIPs);
        configuration.save(file);
    }

    private void loadDataFile() throws IOException, InvalidConfigurationException {
        claimed = new ArrayList<>();
        claimedIPs = new ArrayList<>();
        File file = new File(getDataFolder() + "/data.yml");
        if (file.exists()) {
            System.out.println("Loaded File");
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.load(file);
            if (configuration.get("data") != null) {
                claimed = (ArrayList<String>) configuration.get("data");
                System.out.println("Loaded Data " + claimed.size());
            }
            if (configuration.get("ip-data") != null) {
                claimedIPs = (ArrayList<String>) configuration.get("ip-data");
                System.out.println("Loaded IP " + claimedIPs.size());
            }
        }

    }

}
