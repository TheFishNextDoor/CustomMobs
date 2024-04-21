package com.thefishnextdoor.custommobs.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.thefishnextdoor.custommobs.Config;
import com.thefishnextdoor.custommobs.FishsCustomMobs;
import com.thefishnextdoor.custommobs.util.EndOfTick;
import com.thefishnextdoor.custommobs.util.EnumTools;
import com.thefishnextdoor.custommobs.util.RegistryTools;

public class MobConfiguration {

    private static HashMap<String, MobConfiguration> mobConfigurations = new HashMap<>();

    private static List<String> settings = List.of(
        "type",
        "name",
        "glowing",
        "gravity",
        "invulnerable",
        "persistent",
        "silent",
        "visual-fire",
        "powered",
        "radius",
        "fuse",
        "pitch",
        "yaw",
        "effecs",
        "hand",
        "off-hand",
        "helmet",
        "chestplate",
        "leggings",
        "boots",
        "health",
        "size"
    );

    private final String id;

    private EntityType type;

    private String name;
    
    private Boolean glowing = null;
    private Boolean gravity = null;
    private Boolean invulnerable = null;
    private Boolean persistent = null;
    private Boolean silent = null;
    private Boolean visualFire = null;
    private Boolean powered = null;

    private Integer radius = null;
    private Integer fuse = null;

    private Float pitch = null;
    private Float yaw = null;

    private ArrayList<PotionEffect> potionEffects = new ArrayList<>();

    private ItemConfiguration hand;
    private ItemConfiguration offHand;
    private ItemConfiguration helmet;
    private ItemConfiguration chestplate;
    private ItemConfiguration leggings;
    private ItemConfiguration boots;

    private Integer health = null;

    private Integer size = null;

    public MobConfiguration(YamlConfiguration config, String id) {
        Logger logger = FishsCustomMobs.getInstance().getLogger();
        
        this.id = id;

        for (String setting : config.getConfigurationSection(id).getKeys(false)) {
            if (!settings.contains(setting)) {
                logger.warning("Invalid setting for mob " + id + ": " + setting);
                String possibleSettings = String.join(", ", settings);
                logger.warning("Valid settings are: " + possibleSettings);
            }
        }

        this.type = EnumTools.fromString(EntityType.class, config.getString(id + ".type"));
        if (type == null) {
            this.type = EntityType.PIG;
            logger.warning("Invalid type for mob " + id);
            logger.warning("Valid types are: " + EnumTools.allStrings(EntityType.class));
        }

        this.name = config.getString(id + ".name");
        if (name != null) {
            this.name = ChatColor.translateAlternateColorCodes('&', this.name);
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
        if (config.contains(id + ".persistent")) {
            this.persistent = config.getBoolean(id + ".persistent");
        }
        if (config.contains(id + ".silent")) {
            this.silent = config.getBoolean(id + ".silent");
        }
        if (config.contains(id + ".visual-fire")) {
            this.visualFire = config.getBoolean(id + ".visual-fire");
        }
        if (config.contains(id + ".powered")) {
            this.powered = config.getBoolean(id + ".powered");
        }


        if (config.contains(id + ".radius")) {
            this.radius = config.getInt(id + ".radius");
        }
        if (config.contains(id + ".fuse")) {
            this.fuse = config.getInt(id + ".fuse");
        }

        if (config.contains(id + ".pitch")) {
            this.pitch = (float) config.getDouble(id + ".pitch");
        }
        if (config.contains(id + ".yaw")) {
            this.yaw = (float) config.getDouble(id + ".yaw");
        }

        if (config.contains(id + ".effects")) {
            for (String effectString : config.getStringList(id + ".effects")) {
                String[] parts = effectString.split(",");
                if (parts.length != 3) {
                    logger.warning("Invalid potion effect for mob " + id + ": " + effectString);
                    logger.warning("Potion effect must be in the format: <effect>, <amplifier>, <ticks>");
                    continue;
                }

                String effectName = parts[0];
                PotionEffectType effectType = RegistryTools.fromString(Registry.EFFECT, effectName);
                if (effectType == null) {
                    logger.warning("Invalid potion effect for mob " + id + ": " + effectName);
                    logger.warning("Valid potion effects are: " + RegistryTools.allStrings(Registry.EFFECT));
                    continue;
                }

                int amplifier;
                int ticks;
                try {
                    amplifier = Integer.parseInt(parts[1].trim());
                    ticks = Integer.parseInt(parts[2].trim());
                } catch (NumberFormatException e) {
                    logger.warning("Invalid amplifier or duration for potion effect for mob " + id + ": " + effectString);
                    logger.warning("Potion effect must be in the format: <effect>, <amplifier>, <ticks>");
                    continue;
                }

                potionEffects.add(new PotionEffect(effectType, ticks, amplifier));
            }
        }

        this.hand = ItemConfiguration.get(config.getString(id + ".hand"));
        this.offHand = ItemConfiguration.get(config.getString(id + ".off-hand"));
        this.helmet = ItemConfiguration.get(config.getString(id + ".helmet"));
        this.chestplate = ItemConfiguration.get(config.getString(id + ".chestplate"));
        this.leggings = ItemConfiguration.get(config.getString(id + ".leggings"));
        this.boots = ItemConfiguration.get(config.getString(id + ".boots"));

        if (config.contains(id + ".health")) {
            this.health = config.getInt(id + ".health");
        }

        if (config.contains(id + ".size")) {
            this.size = config.getInt(id + ".size");
        }

        mobConfigurations.put(id, this);
    }

    public String getId() {
        return id;
    }

    public void spawn(Location location) {
        Entity entity = location.getWorld().spawnEntity(location, type);
        this.applyTo(entity, false);
    }

    public void applyTo(Entity entity, boolean removeAtEndOfTick) {
        if (entity.getType() != type) {
            this.spawn(entity.getLocation());
            if (removeAtEndOfTick) {
                EndOfTick.remove(entity);
            }
            else {
                entity.remove();
            }
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

            for (PotionEffect effect : potionEffects) {
                livingEntity.addPotionEffect(effect);
            }

            EntityEquipment equipment = livingEntity.getEquipment();
            if (hand != null) {
                equipment.setItemInMainHand(hand.create(1));
            }
            if (offHand != null) {
                equipment.setItemInOffHand(offHand.create(1));
            }
            if (helmet != null) {
                equipment.setHelmet(helmet.create(1));
            }
            if (chestplate != null) {
                equipment.setChestplate(chestplate.create(1));
            }
            if (leggings != null) {
                equipment.setLeggings(leggings.create(1));
            }
            if (boots != null) {
                equipment.setBoots(boots.create(1));
            }

            if (health != null) {
                AttributeInstance maxHealthAttribute = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (maxHealthAttribute != null) {
                    maxHealthAttribute.setBaseValue(health);
                    livingEntity.setHealth(health);
                }
            }
        }

        if (entity instanceof Slime) {
            Slime slime = (Slime) entity;
            if (size != null) {
                slime.setSize(size);
            }
        }

        if (entity instanceof Phantom) {
            Phantom phantom = (Phantom) entity;
            if (size != null) {
                phantom.setSize(size);
            }
        }

        if (entity instanceof Creeper) {
            Creeper creeper = (Creeper) entity;
            if (powered != null) {
                creeper.setPowered(powered);
            }
            if (radius != null) {
                creeper.setExplosionRadius(radius);
            }
            if (fuse != null) {
                creeper.setMaxFuseTicks(fuse);
            }
        }
    }

    public static MobConfiguration get(String id) {
        return mobConfigurations.get(id);
    }

    public static ArrayList<String> getIds() {
        ArrayList<String> names = new ArrayList<>();
        for (String id : mobConfigurations.keySet()) {
            names.add(id);
        }
        return names;
    }

    public static void loadConfig() {
        mobConfigurations.clear();

        YamlConfiguration config = Config.get("mobs.yml");
        for (String id : config.getKeys(false)) {
            new MobConfiguration(config, id);
        }
    }
}
