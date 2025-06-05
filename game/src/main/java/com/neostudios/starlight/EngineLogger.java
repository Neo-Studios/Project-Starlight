package com.neostudios.starlight.neolight;

/**
 * Simple engine-wide logger with log levels.
 */
public class EngineLogger {
    public enum Level { INFO, WARN, ERROR }
    private static Level minLevel = Level.INFO;

    public static void setLevel(Level level) {
        minLevel = level;
    }

    public static void log(Level level, String msg) {
        if (level.ordinal() >= minLevel.ordinal()) {
            System.out.println("[" + level + "] " + msg);
        }
    }

    public static void info(String msg) { log(Level.INFO, msg); }
    public static void warn(String msg) { log(Level.WARN, msg); }
    public static void error(String msg) { log(Level.ERROR, msg); }
}

// This file is a duplicate. The real EngineLogger is in the neolight package.
