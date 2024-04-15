package com.thefishnextdoor.custommobs.configuration;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

import com.thefishnextdoor.custommobs.Config;
import com.thefishnextdoor.custommobs.FishsCustomMobs;
import com.thefishnextdoor.custommobs.util.EndOfTick;
import com.thefishnextdoor.custommobs.util.EnumTools;

public class MobConfiguration {

    private static HashMap<String, MobConfiguration> mobConfigurations = new HashMap<>();

    private final String id;

    private EntityType type;

    private String name;
    
    private Boolean glowing = null;
    private Boolean gravity = null;
    private Boolean invulnerable = null;
    private Boolean persistent = null;
    private Boolean silent = null;
    private Boolean visualFire = null;

    private Float pitch = null;
    private Float yaw = null;

    private ItemConfiguration hand;
    private ItemConfiguration offHand;
    private ItemConfiguration helmet;
    private ItemConfiguration chestplate;
    private ItemConfiguration leggings;
    private ItemConfiguration boots;

    public MobConfiguration(YamlConfiguration config, String id) {
        Logger logger = FishsCustomMobs.getInstance().getLogger();
        
        this.id = id;

        this.type = EnumTools.fromString(EntityType.class, config.getString(id + ".type"));
        if (type == null) {
            this.type = EntityType.PIG;
            logger.warning("Invalid type for mob " + id);
            logger.warning("Valid types are: " + EnumTools.allStrings(EntityType.class));
        }

        this.name = config.getString(id + ".name");

        if (config.contains(id + ".glowing")) {
            this.glowing = config.getBoolean(id + ".glowing");
        }
        if (config.contains(id + ".gravity")) {
            this.gravity = config.getBoolean(id + ".gravity");
        }
        if (config.contains(id + ".invulnerable")) {
            this.invulnerable = config.getBoolean(id + ".invulnerable");
        }
        if (config.contains(id + ".persistent")) {
            this.persistent = config.getBoolean(id + ".persistent");
        }
        if (config.contains(id + ".silent")) {
            this.silent = config.getBoolean(id + ".silent");
        }
        if (config.contains(id + ".visual-fire")) {
            this.visualFire = config.getBoolean(id + ".visual-fire");
        }

        if (config.contains(id + ".pitch")) {
            this.pitch = (float) config.getDouble(id + ".pitch");
        }
        if (config.contains(id + ".yaw")) {
            this.yaw = (float) config.getDouble(id + ".yaw");
        }

        this.hand = ItemConfiguration.get(config.getString(id + ".hand"));
        this.offHand = ItemConfiguration.get(config.getString(id + ".off-hand"));
        this.helmet = ItemConfiguration.get(config.getString(id + ".helmet"));
        this.chestplate = ItemConfiguration.get(config.getString(id + ".chestplate"));
        this.leggings = ItemConfiguration.get(config.getString(id + ".leggings"));
        this.boots = ItemConfiguration.get(config.getString(id + ".boots"));

        mobConfigurations.put(id, this);
    }

    public String getId() {
        return id;
    }

    public void spawn(Location location) {
        Entity entity = location.getWorld().spawnEntity(location, type);
        this.applyTo(entity);
    }

    public void applyTo(Entity entity) {
        if (entity.getType() != type) {
            this.spawn(entity.getLocation());
            EndOfTick.remove(entity);
            return;
        }

        if (name != null) {
            entity.setCustomName(name);
            entity.setCustomNameVisible(true);
        }

        if (glowing != null) {
            entity.setGlowing(glowing);
        }
        if (gravity != null) {
            entity.setGravity(gravity);
        }
        if (invulnerable != null) {
            entity.setInvulnerable(invulnerable);
        }
        if (persistent != null) {
            entity.setPersistent(persistent);
        }
        if (silent != null) {
            entity.setSilent(silent);
        }
        if (visualFire != null) {
            entity.setVisualFire(visualFire);
        }

        Location location = entity.getLocation();
        if (pitch != null) {
            location.setPitch(pitch);
        }
        if (yaw != null) {
            location.setYaw(yaw);
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            EntityEquipment equipment = livingEntity.getEquipment();

            if (hand != null) {
                equipment.setItemInMainHand(hand.create());
            }
            if (offHand != null) {
                equipment.setItemInOffHand(offHand.create());
            }
            if (helmet != null) {
                equipment.setHelmet(helmet.create());
            }
            if (chestplate != null) {
                equipment.setChestplate(chestplate.create());
            }
            if (leggings != null) {
                equipment.setLeggings(leggings.create());
            }
            if (boots != null) {
                equipment.setBoots(boots.create());
            }
        }
    }

    public static MobConfiguration get(String id) {
        return mobConfigurations.get(id);
    }

    public static void loadConfig() {
        mobConfigurations.clear();

        YamlConfiguration config = Config.get("mobs.yml");
        for (String id : config.getKeys(false)) {
            new MobConfiguration(config, id);
        }
    }
}
