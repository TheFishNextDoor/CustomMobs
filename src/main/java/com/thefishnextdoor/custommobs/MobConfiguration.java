package com.thefishnextdoor.custommobs;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

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

    private ItemConfiguration hand;
    private ItemConfiguration offHand;
    private ItemConfiguration helmet;
    private ItemConfiguration chestplate;
    private ItemConfiguration leggings;
    private ItemConfiguration boots;

    public MobConfiguration(YamlConfiguration config, String id) {
        this.id = id;

        this.type = EnumTools.fromString(EntityType.class, config.getString(id + ".type"));
        if (type == null) {
            this.type = EntityType.PIG;
            FishsCustomMobs.getInstance().getLogger().warning("Invalid type for mob " + id);
        }

        if (config.contains(id + ".glowing")) {
            this.glowing = config.getBoolean(id + ".glowing");
        }

        if (config.contains(id + ".gravity")) {
            this.gravity = config.getBoolean(id + ".gravity");
        }

        if (config.contains(id + ".invulnerable")) {
            this.invulnerable = config.getBoolean(id + ".invulnerable");
        }

        this.hand = ItemConfiguration.get(config.getString(id + ".hand"));
        this.offHand = ItemConfiguration.get(config.getString(id + ".off-hand"));
        this.helmet = ItemConfiguration.get(config.getString(id + ".helmet"));
        this.chestplate = ItemConfiguration.get(config.getString(id + ".chestplate"));
        this.leggings = ItemConfiguration.get(config.getString(id + ".leggings"));
        this.boots = ItemConfiguration.get(config.getString(id + ".boots"));
        
        this.name = config.getString(id + ".name");

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
