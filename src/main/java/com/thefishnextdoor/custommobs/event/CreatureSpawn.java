package com.thefishnextdoor.custommobs.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.thefishnextdoor.custommobs.Debug;
import com.thefishnextdoor.custommobs.SpawnOverride;

public class CreatureSpawn implements Listener {

    @EventHandler (ignoreCancelled = true)
    public void EntitySpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            return;
        }

        if (Debug.debug) {
            Debug.log("CreatureSpawnEvent: " + event.getEntity().getType() + " " + event.getSpawnReason() + " " + event.getLocation());
        }

        SpawnOverride override = SpawnOverride.get(event);
        if (override != null) {
            override.applyTo(event);
        }
    }
}
