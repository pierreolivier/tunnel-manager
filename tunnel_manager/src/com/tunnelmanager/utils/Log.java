package com.tunnelmanager.utils;

import java.text.DateFormat;
import java.util.Date;

/**
 * Class Log
 * Log into terminal
 *
 * @author Pierre-Olivier on 07/03/14.
 */
public class Log {
    /**
     * Verbose log
     * @param text string to log
     */
    public static void v(String text) {
        Date date = new Date();

        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.DEFAULT);

        System.out.println(shortDateFormat.format(date) + " " + text);
    }

    /**
     * Error log
     * @param text string to log
     */
    public static void e(String text) {
        Date date = new Date();

        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.DEFAULT);

        System.err.println(shortDateFormat.format(date) + " " + text);
    }
}
