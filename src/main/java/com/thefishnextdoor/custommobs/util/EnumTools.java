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
}
