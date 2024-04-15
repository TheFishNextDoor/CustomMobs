package com.thefishnextdoor.custommobs.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.thefishnextdoor.custommobs.override.SpawnOverride;

public class CreatureSpawn implements Listener {

    @EventHandler
    public void EntitySpawn(CreatureSpawnEvent event) {
        SpawnOverride override = SpawnOverride.get(event);
        if (override != null) {
            override.applyTo(event);
        }
    }
}
