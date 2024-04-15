package com.thefishnextdoor.custommobs;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.thefishnextdoor.custommobs.util.EnchantTools;
import com.thefishnextdoor.custommobs.util.EnumTools;

public class ItemConfiguration {

    private static HashMap<String, ItemConfiguration> itemConfigurations = new HashMap<>();

    private final String id;

    private Material material;
    private String name;
    private HashMap<Enchantment, Integer> enchantments = new HashMap<>();

    public ItemConfiguration(YamlConfiguration config, String id) {
        this.id = id;

        this.material = EnumTools.fromString(Material.class, config.getString(id + ".material"));
        if (material == null) {
            this.material = Material.STONE;
            FishsCustomMobs.getInstance().getLogger().warning("Invalid material for item " + id);
        }
        
        this.name = config.getString(id + ".name");

        for (String enchantmentName : config.getConfigurationSection(id + ".enchantments").getKeys(false)) {
            Enchantment enchantment = EnchantTools.fromString(enchantmentName);
            if (enchantment == null) {
                FishsCustomMobs.getInstance().getLogger().warning("Invalid enchantment for item " + id + ": " + enchantmentName);
                continue;
            }

            int level = config.getInt(id + ".enchantments." + enchantmentName);
            if (level > 0) {
                enchantments.put(enchantment, level);
            }
        }

        itemConfigurations.put(id, this);
    }

    public String getId() {
        return id;
    }

    public ItemStack create() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.setDisplayName(name);
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

    public static void loadConfig() {
        itemConfigurations.clear();

        YamlConfiguration config = Config.get("items.yml");
        for (String id : config.getKeys(false)) {
            new ItemConfiguration(config, id);
        }
    }
}
