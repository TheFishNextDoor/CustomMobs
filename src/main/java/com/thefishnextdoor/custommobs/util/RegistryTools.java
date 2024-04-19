package com.thefishnextdoor.custommobs.util;

import java.util.Iterator;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
public class RegistryTools {

    public static <T extends Keyed> T fromString(Registry<T> registry, String name) {
        try {
            return registry.get(NamespacedKey.minecraft(name.toLowerCase()));
        } catch (Exception e) {
            return null;
        }
    }

    public static String allStrings(Registry<? extends Keyed> registry) {
        StringBuilder builder = new StringBuilder();
        Iterator<? extends Keyed> registryIterator = registry.iterator();
        while (registryIterator.hasNext()) {
            Keyed constant = registryIterator.next();
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(constant.getKey().getKey());
        }
        return builder.toString();
    }
}
