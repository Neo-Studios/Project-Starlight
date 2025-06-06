package com.neostudios.starlight.neolight;

/**
 * Simple logger for engine events and debugging.
 */
public class EngineLogger {
    public static void log(String message) {
        System.out.println("[NeoLight] " + message);
    }
    public static void warn(String message) {
        System.out.println("[NeoLight][WARN] " + message);
    }
    public static void error(String message) {
        System.err.println("[NeoLight][ERROR] " + message);
    }
}
