package com.neostudios.starlight;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Handles loading and accessing game configuration properties.
 */
public class ConfigManager {
    private final Properties properties = new Properties();

    public ConfigManager(String configPath) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(configPath)) {
            if (in == null) {
                throw new IOException("Config file not found: " + configPath);
            }
            properties.load(in);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }
}
