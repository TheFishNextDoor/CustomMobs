package com.thefishnextdoor.custommobs.util;

import com.thefishnextdoor.custommobs.Debug;

public class EnumTools {

    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String name) {
        if (name == null) {
            return null;
        }

        name = name.trim().replace(" ", "_").replace("-", "_");

        E[] constants = enumClass.getEnumConstants();
        if (constants == null) {
            Debug.error(enumClass.getName() + " is not a valid enum class");
            return null;
        }

        for (E constant : constants) {
            if (constant.name().equalsIgnoreCase(name)) {
                return constant;
            }
        }

        return null;
    }

    public static String allStrings(Class<? extends Enum<?>> enumClass) {
        StringBuilder builder = new StringBuilder();

        Enum<?>[] constants = enumClass.getEnumConstants();
        if (constants == null) {
            Debug.error(enumClass.getName() + " is not a valid enum class");
            return "None";
        }

        for (Enum<?> constant : constants) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(constant.name());
        }
        return builder.toString();
    }
}
