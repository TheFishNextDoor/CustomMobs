package com.thefishnextdoor.custommobs.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Cat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.material.Colorable;
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
        "baby",
        "pickup-items",
        "despawn",
        "chested",
        "radius",
        "fuse",
        "health",
        "size",
        "pitch",
        "yaw",
        "effects",
        "color",
        "villager",
        "profession",
        "cat",
        "fox",
        "parrot",
        "rabbit",
        "horse-color",
        "horse-style",
        "hand",
        "off-hand",
        "helmet",
        "chestplate",
        "leggings",
        "boots"
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
    private Boolean baby = null;
    private Boolean pickupItems = null;
    private Boolean despawn = null;
    private Boolean chested = null;

    private Integer health = null;
    private Integer size = null;
    private Integer radius = null;
    private Integer fuse = null;

    private Float pitch = null;
    private Float yaw = null;

    private ArrayList<PotionEffect> potionEffects = new ArrayList<>();

    private DyeColor color = null;

    private Villager.Type villager = null;
    private Villager.Profession profession = null;

    private Cat.Type cat = null;

    private Fox.Type fox = null;

    private Parrot.Variant parrot = null;

    private Rabbit.Type rabbit = null;

    private Horse.Color horseColor = null;
    private Horse.Style horseStyle = null;

    private ItemConfiguration hand;
    private ItemConfiguration offHand;
    private ItemConfiguration helmet;
    private ItemConfiguration chestplate;
    private ItemConfiguration leggings;
    private ItemConfiguration boots;

    public MobConfiguration(YamlConfiguration config, String id) {
        Logger logger = FishsCustomMobs.getInstance().getLogger();
        
        this.id = id;

        if (!config.contains(id)) {
            logger.severe("Could not find configuration for mob: " + id);
            return;
        }
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
        if (config.contains(id + ".baby")) {
            this.baby = config.getBoolean(id + ".baby");
        }
        if (config.contains(id + ".pickup-items")) {
            this.pickupItems = config.getBoolean(id + ".pickup-items");
        }
        if (config.contains(id + ".despawn")) {
            this.despawn = config.getBoolean(id + ".despawn");
        }
        if (config.contains(id + ".chested")) {
            this.chested = config.getBoolean(id + ".chested");
        }

        if (config.contains(id + ".health")) {
            this.health = config.getInt(id + ".health");
        }
        if (config.contains(id + ".size")) {
            this.size = config.getInt(id + ".size");
        }
        if (config.contains(id + ".radius")) {
            this.radius = config.getInt(id + ".radius");
            if (this.radius != null) {
                this.radius = Math.max(0, this.radius);
                this.radius = Math.min(96, this.radius);
            }
        }
        if (config.contains(id + ".fuse")) {
            this.fuse = config.getInt(id + ".fuse");
            if (this.fuse != null) {
                this.fuse = Math.max(0, this.fuse);
            }
        }

        if (config.contains(id + ".pitch")) {
            this.pitch = (float) config.getDouble(id + ".pitch");
        }
        if (config.contains(id + ".yaw")) {
            this.yaw = (float) config.getDouble(id + ".yaw");
        }

        if (config.contains(id + ".color")) {
            this.color = EnumTools.fromString(DyeColor.class, config.getString(id + ".color"));
            if (color == null) {
                logger.warning("Invalid color for mob " + id);
                logger.warning("Valid colors are: " + EnumTools.allStrings(DyeColor.class));
            }
        }

        if (config.contains(id + ".villager")) {
            this.villager = EnumTools.fromString(Villager.Type.class, config.getString(id + ".villager"));
            if (villager == null) {
                logger.warning("Invalid villager type for mob " + id);
                logger.warning("Valid villager types are: " + EnumTools.allStrings(Villager.Type.class));
            }
        }

        if (config.contains(id + ".profession")) {
            this.profession = EnumTools.fromString(Villager.Profession.class, config.getString(id + ".profession"));
            if (profession == null) {
                logger.warning("Invalid villager profession for mob " + id);
                logger.warning("Valid villager professions are: " + EnumTools.allStrings(Villager.Profession.class));
            }
        }

        if (config.contains(id + ".cat")) {
            this.cat = EnumTools.fromString(Cat.Type.class, config.getString(id + ".cat"));
            if (cat == null) {
                logger.warning("Invalid cat type for mob " + id);
                logger.warning("Valid cat types are: " + EnumTools.allStrings(Cat.Type.class));
            }
        }

        if (config.contains(id + ".fox")) {
            this.fox = EnumTools.fromString(Fox.Type.class, config.getString(id + ".fox"));
            if (fox == null) {
                logger.warning("Invalid fox type for mob " + id);
                logger.warning("Valid fox types are: " + EnumTools.allStrings(Fox.Type.class));
            }
        }

        if (config.contains(id + ".parrot")) {
            this.parrot = EnumTools.fromString(Parrot.Variant.class, config.getString(id + ".parrot"));
            if (parrot == null) {
                logger.warning("Invalid parrot type for mob " + id);
                logger.warning("Valid parrot types are: " + EnumTools.allStrings(Parrot.Variant.class));
            }
        }

        if (config.contains(id + ".rabbit")) {
            this.rabbit = EnumTools.fromString(Rabbit.Type.class, config.getString(id + ".rabbit"));
            if (rabbit == null) {
                logger.warning("Invalid rabbit type for mob " + id);
                logger.warning("Valid rabbit types are: " + EnumTools.allStrings(Rabbit.Type.class));
            }
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

        if (config.contains("id" + ".horse-color")) {
            this.horseColor = EnumTools.fromString(Horse.Color.class, config.getString(id + ".horse-color"));
            if (horseColor == null) {
                logger.warning("Invalid horse color for mob " + id);
                logger.warning("Valid horse colors are: " + EnumTools.allStrings(Horse.Color.class));
            }
        }

        if (config.contains("id" + ".horse-style")) {
            this.horseStyle = EnumTools.fromString(Horse.Style.class, config.getString(id + ".horse-style"));
            if (horseStyle == null) {
                logger.warning("Invalid horse style for mob " + id);
                logger.warning("Valid horse styles are: " + EnumTools.allStrings(Horse.Style.class));
            }
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
        this.applyTo(entity, false);
    }

    public void applyTo(Entity entity, boolean removeAtEndOfTick) {
        if (entity.getType() != this.type) {
            this.spawn(entity.getLocation());
            if (removeAtEndOfTick) {
                EndOfTick.remove(entity);
            }
            else {
                entity.remove();
            }
            return;
        }

        if (this.name != null) {
            entity.setCustomName(this.name);
            entity.setCustomNameVisible(true);
        }

        if (this.glowing != null) {
            entity.setGlowing(this.glowing);
        }
        if (this.gravity != null) {
            entity.setGravity(this.gravity);
        }
        if (this.invulnerable != null) {
            entity.setInvulnerable(this.invulnerable);
        }
        if (this.persistent != null) {
            entity.setPersistent(this.persistent);
        }
        if (this.silent != null) {
            entity.setSilent(this.silent);
        }
        if (this.visualFire != null) {
            entity.setVisualFire(this.visualFire);
        }

        Location location = entity.getLocation();
        if (this.pitch != null) {
            location.setPitch(this.pitch);
        }
        if (this.yaw != null) {
            location.setYaw(this.yaw);
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            if (this.pickupItems != null) {
                livingEntity.setCanPickupItems(this.pickupItems);
            }
            if (this.despawn != null) {
                livingEntity.setRemoveWhenFarAway(this.despawn);
            }

            for (PotionEffect effect : potionEffects) {
                livingEntity.addPotionEffect(effect);
            }

            EntityEquipment equipment = livingEntity.getEquipment();
            if (this.hand != null) {
                equipment.setItemInMainHand(this.hand.create(1));
            }
            if (this.offHand != null) {
                equipment.setItemInOffHand(this.offHand.create(1));
            }
            if (this.helmet != null) {
                equipment.setHelmet(this.helmet.create(1));
            }
            if (this.chestplate != null) {
                equipment.setChestplate(this.chestplate.create(1));
            }
            if (this.leggings != null) {
                equipment.setLeggings(this.leggings.create(1));
            }
            if (this.boots != null) {
                equipment.setBoots(this.boots.create(1));
            }

            if (this.health != null) {
                AttributeInstance maxHealthAttribute = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (maxHealthAttribute != null) {
                    maxHealthAttribute.setBaseValue(this.health);
                    livingEntity.setHealth(this.health);
                }
            }
        }

        if (entity instanceof Slime) {
            Slime slime = (Slime) entity;
            if (this.size != null) {
                slime.setSize(this.size);
            }
        }

        if (entity instanceof Phantom) {
            Phantom phantom = (Phantom) entity;
            if (this.size != null) {
                phantom.setSize(this.size);
            }
        }

        if (entity instanceof Creeper) {
            Creeper creeper = (Creeper) entity;
            if (this.powered != null) {
                creeper.setPowered(this.powered);
            }
            if (this.radius != null) {
                creeper.setExplosionRadius(this.radius);
            }
            if (this.fuse != null) {
                creeper.setMaxFuseTicks(this.fuse);
            }
        }

        if (entity instanceof Ageable) {
            Ageable ageable = (Ageable) entity;
            if (this.baby != null) {
                if (this.baby) {
                    ageable.setBaby();
                }
                else {
                    ageable.setAdult();
                }
            }
        }

        if (entity instanceof Villager) {
            Villager villager = (Villager) entity;
            if (this.villager != null) {
                villager.setVillagerType(this.villager);
            }
            if (this.profession != null) {
                villager.setProfession(this.profession);
            }
        }

        if (entity instanceof Colorable) {
            Colorable colorable = (Colorable) entity;
            if (this.color != null) {
                colorable.setColor(this.color);
            }
        }

        if (entity instanceof Cat) {
            Cat cat = (Cat) entity;
            if (this.cat != null) {
                cat.setCatType(this.cat);
            }
            if (this.color != null) {
                cat.setCollarColor(this.color);
            }
        }

        if (entity instanceof Wolf) {
            Wolf wolf = (Wolf) entity;
            if (this.color != null) {
                wolf.setCollarColor(this.color);
            }
        }

        if (entity instanceof Fox) {
            Fox fox = (Fox) entity;
            if (this.fox != null) {
                fox.setFoxType(this.fox);
            }
        }

        if (entity instanceof Parrot) {
            Parrot parrot = (Parrot) entity;
            if (this.parrot != null) {
                parrot.setVariant(this.parrot);
            }
        }

        if (entity instanceof Rabbit) {
            Rabbit rabbit = (Rabbit) entity;
            if (this.rabbit != null) {
                rabbit.setRabbitType(this.rabbit);
            }
        }

        if (entity instanceof ChestedHorse) {
            ChestedHorse horse = (ChestedHorse) entity;
            if (this.chested != null) {
                horse.setCarryingChest(this.chested);
            }
        }

        if (entity instanceof Horse) {
            Horse horse = (Horse) entity;
            if (this.horseColor != null) {
                horse.setColor(this.horseColor);
            }
            if (this.horseStyle != null) {
                horse.setStyle(this.horseStyle);
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
