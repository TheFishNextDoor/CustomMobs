package com.thefishnextdoor.custommobs;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.thefishnextdoor.custommobs.util.EnumTools;

public class SpawnOverride {

    private static ArrayList<SpawnOverride> spawnOverrides = new ArrayList<>();

    private int priority;

    private ArrayList<LinkedMobConfiguration> linkedEntityConfigurations = new ArrayList<>();

    private HashSet<String> worlds = new HashSet<>();
    private HashSet<SpawnReason> spawnReasons = new HashSet<>();
    private HashSet<EntityType> entityTypes = new HashSet<>();

    private Integer minX = null;
    private Integer minY = null;
    private Integer minZ = null;

    private Integer maxX = null;
    private Integer maxY = null;
    private Integer maxZ = null;

    public SpawnOverride(YamlConfiguration config, String id) {
        this.priority = config.getInt(id + ".priority");

        for (String mobId : config.getConfigurationSection(id + ".mobs").getKeys(false)) {
            int weight = config.getInt(id + ".mobs." + mobId);
            MobConfiguration mobConfiguration = MobConfiguration.get(mobId);
            if (mobConfiguration != null) {
                linkedEntityConfigurations.add(new LinkedMobConfiguration(mobConfiguration, weight));
            }
            else {
                FishsCustomMobs.getInstance().getLogger().warning("Invalid mob for override " + id + ": " + mobId);
            }
        }

        for (String world : config.getStringList(id + ".worlds")) {
            worlds.add(world);
        }

        for (String reasonName : config.getStringList(id + ".spawn-reasons")) {
            SpawnReason reason = EnumTools.fromString(SpawnReason.class, reasonName);
            if (reason == null) {
                FishsCustomMobs.getInstance().getLogger().warning("Invalid spawn reason for override " + id + ": " + reasonName);
                continue;
            }

            spawnReasons.add(reason);
        }

        for (String typeName : config.getStringList(id + ".entity-types")) {
            EntityType entityType = EnumTools.fromString(EntityType.class, typeName);
            if (entityType == null) {
                FishsCustomMobs.getInstance().getLogger().warning("Invalid entity type for override " + id + ": " + typeName);
                continue;
            }

            entityTypes.add(entityType);
        }

        if (config.contains(id + ".min-x")) {
            minX = config.getInt(id + ".min-x");
        }
        if (config.contains(id + ".min-y")) {
            minY = config.getInt(id + ".min-y");
        }
        if (config.contains(id + ".min-z")) {
            minZ = config.getInt(id + ".min-z");
        }

        if (config.contains(id + ".max-x")) {
            maxX = config.getInt(id + ".max-x");
        }
        if (config.contains(id + ".max-y")) {
            maxY = config.getInt(id + ".max-y");
        }
        if (config.contains(id + ".max-z")) {
            maxZ = config.getInt(id + ".max-z");
        }

        spawnOverrides.add(this);
    }

    public int getPriority() {
        return priority;
    }

    public boolean isValidFor(CreatureSpawnEvent event) {
        Location location = event.getLocation();

        if (worlds.size() > 0 && !worlds.contains(location.getWorld().getName())) {
            return false;
        }

        if (spawnReasons.size() > 0 && !spawnReasons.contains(event.getSpawnReason())) {
            return false;
        }

        if (entityTypes.size() > 0 && !entityTypes.contains(event.getEntityType())) {
            return false;
        }

        int x = location.getBlockX();
        if (minX != null && x < minX) {
            return false;
        }
        if (maxX != null && x > maxX) {
            return false;
        }

        int y = location.getBlockY();
        if (minY != null && y < minY) {
            return false;
        }
        if (maxY != null && y > maxY) {
            return false;
        }

        int z = location.getBlockZ();
        if (minZ != null && z < minZ) {
            return false;
        }
        if (maxZ != null && z > maxZ) {
            return false;
        }

        return true;
    }

    public void applyTo(CreatureSpawnEvent event) {
        int totalWeight = 0;
        for (LinkedMobConfiguration linkedEntityConfiguration : linkedEntityConfigurations) {
            totalWeight += linkedEntityConfiguration.getWeight();
        }

        int random = (int) (Math.random() * totalWeight);
        for (LinkedMobConfiguration linkedEntityConfiguration : linkedEntityConfigurations) {
            random -= linkedEntityConfiguration.getWeight();
            if (random <= 0) {
                linkedEntityConfiguration.unWrap().applyTo(event.getEntity());
                return;
            }
        }
    }

    public static SpawnOverride get(CreatureSpawnEvent event) {
        SpawnOverride best = null;
        for (SpawnOverride override : spawnOverrides) {
            if (override.isValidFor(event)) {
                if (best == null || override.getPriority() > best.getPriority()) {
                    best = override;
                }
            }
        }
        return best;
    }

    public static void loadConfig() {
        spawnOverrides.clear();

        YamlConfiguration config = Config.get("overrides.yml");
        for (String id : config.getKeys(false)) {
            new SpawnOverride(config, id);
        }
    }
}
