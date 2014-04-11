package com.tunnelmanager.utils;

/**
 * Class SystemConfiguration
 *
 * @author Pierre-Olivier on 11/04/2014.
 */
public class SystemConfiguration {
    /**
     * Detection of Windows os
     * @return true if on Windows
     */
    public static boolean onWindows() {
        String OS = System.getProperty("os.name").toLowerCase();

        return OS.contains("win");
    }

    /**
     * Detection of a Linux os
     * @return true if on Linux
     */
    public static boolean onLinux() {
        String OS = System.getProperty("os.name").toLowerCase();

        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }
}
