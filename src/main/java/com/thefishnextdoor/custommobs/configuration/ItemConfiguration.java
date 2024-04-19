package com.thefishnextdoor.custommobs.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import com.thefishnextdoor.custommobs.Config;
import com.thefishnextdoor.custommobs.FishsCustomMobs;
import com.thefishnextdoor.custommobs.util.EnchantTools;
import com.thefishnextdoor.custommobs.util.EnumTools;
import com.thefishnextdoor.custommobs.util.RegistryTools;

public class ItemConfiguration {

    private static HashMap<String, ItemConfiguration> itemConfigurations = new HashMap<>();

    private static List<String> settings = List.of(
        "material",
        "name",
        "lore",
        "unbreakable",
        "trim-material",
        "trim-pattern",
        "enchantments"
    );

    private final String id;

    private Material material;

    private String name;

    private List<String> lore;

    private Boolean unbreakable = null;

    private HashMap<Enchantment, Integer> enchantments = new HashMap<>();

    private ArmorTrim armorTrim = null;

    public ItemConfiguration(YamlConfiguration config, String id) {
        Logger logger = FishsCustomMobs.getInstance().getLogger();

        this.id = id;

        for (String setting : config.getConfigurationSection(id).getKeys(false)) {
            if (!settings.contains(setting)) {
                logger.warning("Invalid setting for item " + id + ": " + setting);
                String possibleSettings = String.join(", ", settings);
                logger.warning("Valid settings are: " + possibleSettings);
            }
        }

        this.material = EnumTools.fromString(Material.class, config.getString(id + ".material"));
        if (material == null) {
            this.material = Material.STONE;
            logger.warning("Invalid material for item " + id);
            logger.warning("Valid materials are: " + EnumTools.allStrings(Material.class));
        }
        
        this.name = config.getString(id + ".name");
        this.lore = config.getStringList(id);

        if (config.contains(id + ".unbreakable")) {
            this.unbreakable = config.getBoolean(id + ".unbreakable");
        }

        if (config.contains(id + ".trim-material") && config.contains(id + ".trim-pattern")) {
            String trimMaterialName = config.getString(id + ".trim-material");
            TrimMaterial trimMaterial = RegistryTools.fromString(Registry.TRIM_MATERIAL, trimMaterialName);
            if (trimMaterial == null) {
                logger.warning("Invalid trim material for item " + id + ": " + trimMaterialName);
                logger.warning("Valid trim materials are: " + RegistryTools.allStrings(Registry.TRIM_MATERIAL));
            }

            String trimPatternName = config.getString(id + ".trim-pattern");
            TrimPattern trimPattern = RegistryTools.fromString(Registry.TRIM_PATTERN, trimPatternName);
            if (trimPattern == null) {
                logger.warning("Invalid trim pattern for item " + id + ": " + trimPatternName);
                logger.warning("Valid trim patterns are: " + RegistryTools.allStrings(Registry.TRIM_PATTERN));
            }

            if (trimMaterial != null && trimPattern != null) {
                this.armorTrim = new ArmorTrim(trimMaterial, trimPattern);
            }
        }

        if (config.contains(id + ".enchantments")) {
            for (String enchantmentName : config.getConfigurationSection(id + ".enchantments").getKeys(false)) {
                Enchantment enchantment = EnchantTools.fromString(enchantmentName);
                if (enchantment == null) {
                    logger.warning("Invalid enchantment for item " + id + ": " + enchantmentName);
                    continue;
                }
    
                int level = config.getInt(id + ".enchantments." + enchantmentName);
                if (level > 0) {
                    enchantments.put(enchantment, level);
                }
            }
        }

        itemConfigurations.put(id, this);
    }

    public String getId() {
        return id;
    }

    public ItemStack create(int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.setDisplayName(name);
        }

        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }

        if (unbreakable != null) {
            meta.setUnbreakable(unbreakable);
        }

        if (meta instanceof ArmorMeta) {
            ArmorMeta armorMeta = (ArmorMeta) meta;
            if (armorTrim != null) {
                armorMeta.setTrim(armorTrim);
            }
        }

        item.setItemMeta(meta);

        for (Enchantment enchantment : enchantments.keySet()) {
            item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment));
        }
        
        return item;
    }

    public static ItemConfiguration get(String id) {
        return itemConfigurations.get(id);
    }

    public static ArrayList<String> getIds() {
        ArrayList<String> names = new ArrayList<>();
        for (String id : itemConfigurations.keySet()) {
            names.add(id);
        }
        return names;
    }

    public static void loadConfig() {
        itemConfigurations.clear();

        YamlConfiguration config = Config.get("items.yml");
        for (String id : config.getKeys(false)) {
            new ItemConfiguration(config, id);
        }
    }
}
