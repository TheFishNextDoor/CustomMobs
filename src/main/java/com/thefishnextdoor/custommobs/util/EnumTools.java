package com.thefishnextdoor.custommobs.util;

public class EnumTools {

    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String name) {
        if (name == null) {
            return null;
        }

        name = name.trim();

        for (E constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(name)) {
                return constant;
            }
        }

        return null;
    }

    public static String allStrings(Class<? extends Enum<?>> enumClass) {
        StringBuilder builder = new StringBuilder();
        for (Enum<?> constant : enumClass.getEnumConstants()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(constant.name());
        }
        return builder.toString();
    }
}
