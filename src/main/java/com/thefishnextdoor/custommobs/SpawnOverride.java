package com.thefishnextdoor.custommobs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.thefishnextdoor.custommobs.configuration.MobConfiguration;
import com.thefishnextdoor.custommobs.util.EnumTools;

public class SpawnOverride {

    private static class LinkedMobConfiguration {

        private final MobConfiguration mobConfiguration;
    
        private final int weight;
    
        private LinkedMobConfiguration(MobConfiguration mobConfiguration, int weight) {
            this.mobConfiguration = mobConfiguration;
            this.weight = weight;
        }
    
        public MobConfiguration unWrap() {
            return mobConfiguration;
        }
    
        public int getWeight() {
            return weight;
        }
    }    

    private static ArrayList<SpawnOverride> spawnOverrides = new ArrayList<>();

    private static List<String> settings = List.of(
        "priority",
        "mobs",
        "worlds",
        "environments",
        "spawn-reasons",
        "spawn-categories",
        "entity-types",
        "biomes",
        "min-x",
        "min-y",
        "min-z",
        "max-x",
        "max-y",
        "max-z"
    );

    private int priority;

    private ArrayList<LinkedMobConfiguration> linkedMobConfigurations = new ArrayList<>();

    private HashSet<String> worlds = new HashSet<>();
    private HashSet<Environment> environments = new HashSet<>();
    private HashSet<SpawnReason> spawnReasons = new HashSet<>();
    private HashSet<SpawnCategory> spawnCategories = new HashSet<>();
    private HashSet<EntityType> entityTypes = new HashSet<>();
    private HashSet<Biome> biomes = new HashSet<>();

    private Integer minX = null;
    private Integer minY = null;
    private Integer minZ = null;

    private Integer maxX = null;
    private Integer maxY = null;
    private Integer maxZ = null;

    public SpawnOverride(YamlConfiguration config, String id) {
        Logger logger = FishsCustomMobs.getInstance().getLogger();

        for (String setting : config.getConfigurationSection(id).getKeys(false)) {
            if (!settings.contains(setting)) {
                logger.warning("Invalid setting for override " + id + ": " + setting);
                String possibleSettings = String.join(", ", settings);
                logger.warning("Valid settings are: " + possibleSettings);
            }
        }

        this.priority = config.getInt(id + ".priority");

        for (String mobId : config.getConfigurationSection(id + ".mobs").getKeys(false)) {
            int weight = config.getInt(id + ".mobs." + mobId);
            MobConfiguration mobConfiguration = MobConfiguration.get(mobId);
            if (mobConfiguration != null) {
                linkedMobConfigurations.add(new LinkedMobConfiguration(mobConfiguration, weight));
            }
            else {
                logger.warning("Invalid mob for override " + id + ": " + mobId);
            }
        }

        for (String world : config.getStringList(id + ".worlds")) {
            worlds.add(world);
        }

        for (String environmentName : config.getStringList(id + ".environments")) {
            Environment environment = EnumTools.fromString(Environment.class, environmentName);
            if (environment == null) {
                logger.warning("Invalid environment for override " + id + ": " + environmentName);
                logger.warning("Valid environments are: " + EnumTools.allStrings(Environment.class));
                continue;
            }

            environments.add(environment);
        }

        for (String reasonName : config.getStringList(id + ".spawn-reasons")) {
            SpawnReason reason = EnumTools.fromString(SpawnReason.class, reasonName);
            if (reason == null) {
                logger.warning("Invalid spawn reason for override " + id + ": " + reasonName);
                logger.warning("Valid spawn reasons are: " + EnumTools.allStrings(SpawnReason.class));
                continue;
            }
            spawnReasons.add(reason);
        }
        if (spawnReasons.isEmpty()) {
            logger.warning("No spawn reasons specified for override " + id + ", this override will have no effect");
            logger.warning("Valid spawn reasons are: " + EnumTools.allStrings(SpawnReason.class));
        }

        for (String categoryName : config.getStringList(id + ".spawn-categories")) {
            SpawnCategory category = EnumTools.fromString(SpawnCategory.class, categoryName);
            if (category == null) {
                logger.warning("Invalid spawn category for override " + id + ": " + categoryName);
                logger.warning("Valid spawn categories are: " + EnumTools.allStrings(SpawnCategory.class));
                continue;
            }
            spawnCategories.add(category);
        }

        for (String typeName : config.getStringList(id + ".entity-types")) {
            EntityType entityType = EnumTools.fromString(EntityType.class, typeName);
            if (entityType == null) {
                logger.warning("Invalid entity type for override " + id + ": " + typeName);
                logger.warning("Valid entity types are: " + EnumTools.allStrings(EntityType.class));
                continue;
            }
            entityTypes.add(entityType);
        }

        for (String biomeName : config.getStringList(id + ".biomes")) {
            Biome biome = EnumTools.fromString(Biome.class, biomeName);
            if (biome == null) {
                logger.warning("Invalid biome for override " + id + ": " + biomeName);
                logger.warning("Valid biomes are: " + EnumTools.allStrings(Biome.class));
                continue;
            }
            biomes.add(biome);
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

        if (environments.size() > 0 && !environments.contains(location.getWorld().getEnvironment())) {
            return false;
        }

        if (!spawnReasons.contains(event.getSpawnReason())) {
            return false;
        }

        if (spawnCategories.size() > 0 && !spawnCategories.contains(event.getEntity().getSpawnCategory())) {
            return false;
        }

        if (entityTypes.size() > 0 && !entityTypes.contains(event.getEntityType())) {
            return false;
        }

        if (biomes.size() > 0 && !biomes.contains(location.getBlock().getBiome())) {
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
        for (LinkedMobConfiguration linkedEntityConfiguration : linkedMobConfigurations) {
            totalWeight += linkedEntityConfiguration.getWeight();
        }

        SpawnReason reason = event.getSpawnReason();
        LivingEntity entity = event.getEntity();
        if (reason == SpawnReason.SLIME_SPLIT) {
            if (entity.getNearbyEntities(8.0, 8.0, 8.0).size() > 8) {
                event.setCancelled(true);
                return;
            }
        }

        boolean delayRemoval;
        switch (reason) {
            case SPAWNER_EGG:
            case BUILD_IRONGOLEM:
            case BUILD_SNOWMAN:
            case BUILD_WITHER:
                delayRemoval = true;
                break;
            default:
                delayRemoval = false;
                break;
        }

        int random = (int) (Math.random() * totalWeight);
        for (LinkedMobConfiguration linkedEntityConfiguration : linkedMobConfigurations) {
            random -= linkedEntityConfiguration.getWeight();
            if (random <= 0) {
                linkedEntityConfiguration.unWrap().applyTo(entity, delayRemoval);
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
