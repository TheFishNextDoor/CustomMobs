package com.thefishnextdoor.custommobs.util;

import java.util.Iterator;

import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

public class EnchantTools {

    public static Enchantment fromString(String name) {
        if (name == null) {
            return null;
        }

        name = name.trim().replace(" ", "_").replace("-", "_");

        Iterator<Enchantment> enchantmentIter = Registry.ENCHANTMENT.iterator();
        while (enchantmentIter.hasNext()) {
            Enchantment enchantment = enchantmentIter.next();
            if (enchantment.getKey().getKey().equalsIgnoreCase(name)) {
                return enchantment;
            }
        }
        return null;
    }
}
