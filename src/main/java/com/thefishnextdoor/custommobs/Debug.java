package com.thefishnextdoor.custommobs;

public class Debug {

    public static boolean debug = false;

    public static void log(String message) {
        FishsCustomMobs.getInstance().getLogger().info(message);
    }
}
