package com.thefishnextdoor.custommobs;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

    public static YamlConfiguration get(String name) {
        File config = new File(getFolder(), name);
        if (!config.exists()) {
            try {
                FishsCustomMobs.getInstance().saveResource(name, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(config);
    }

    private static File getFolder() {
        File folder = FishsCustomMobs.getInstance().getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }   
}
