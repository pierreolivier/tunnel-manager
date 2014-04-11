package com.tunnelmanager.utils;

/**
 * Created by pbouteau on 11/04/14.
 */
public class SystemConfiguration {
    public static boolean onWindows() {
        String OS = System.getProperty("os.name").toLowerCase();

        return OS.contains("win");
    }

    public static boolean onLinux() {
        String OS = System.getProperty("os.name").toLowerCase();

        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }
}
