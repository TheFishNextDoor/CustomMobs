package com.thefishnextdoor.custommobs.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import com.thefishnextdoor.custommobs.FishsCustomMobs;

public class EndOfTick {

    public static void remove(final Entity entity) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(FishsCustomMobs.getInstance(), new Runnable() {
            public void run() {
                entity.remove();
            }
        }, 0);
    }
    
}
