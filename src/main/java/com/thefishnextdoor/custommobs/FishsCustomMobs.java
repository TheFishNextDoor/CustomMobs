package com.thefishnextdoor.custommobs;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.thefishnextdoor.custommobs.configuration.ItemConfiguration;
import com.thefishnextdoor.custommobs.configuration.MobConfiguration;
import com.thefishnextdoor.custommobs.event.CreatureSpawn;
import com.thefishnextdoor.custommobs.override.SpawnOverride;

public class FishsCustomMobs extends JavaPlugin {

    private static FishsCustomMobs instance;

    private static boolean debug = true;

    public void onEnable() {
        instance = this;

        ItemConfiguration.loadConfig();
        MobConfiguration.loadConfig();
        SpawnOverride.loadConfig();

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new CreatureSpawn(), this);

        getLogger().info("Plugin enabled");
    }

    public void onDisable() {
        getLogger().info("Plugin disabled");
    }

    public static FishsCustomMobs getInstance() {
        return instance;
    }

    public static void debug(String message) {
        if (debug) {
            getInstance().getLogger().info(message);
        }
    }
}
