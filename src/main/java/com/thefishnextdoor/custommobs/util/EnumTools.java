package com.thefishnextdoor.custommobs.util;

public class EnumTools {

    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String name) {
        if (name == null) {
            return null;
        }

        for (E constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(name)) {
                return constant;
            }
        }

        return null;
    }

    public static String allStrings(Class<? extends Enum<?>> enumClass) {
        // Make a string that contains all the enum values separated by a comma
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
