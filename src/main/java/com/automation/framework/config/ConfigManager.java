package com.automation.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Thread-safe singleton that loads environment-specific properties.
 * Env is selected via {@code -Denv=staging} (defaults to {@code dev}).
 * System properties always override file values.
 */
public final class ConfigManager {

    private static final Logger LOG = LogManager.getLogger(ConfigManager.class);
    private static final String ENV_KEY = "env";
    private static final String DEFAULT_ENV = "dev";

    private static volatile ConfigManager instance;

    private final Properties properties = new Properties();
    private final String environment;

    private ConfigManager() {
        this.environment = System.getProperty(ENV_KEY, DEFAULT_ENV);
        load();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private void load() {
        String file = String.format("application-%s.properties", environment);
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new IllegalStateException("Properties file not found on classpath: " + file);
            }
            properties.load(in);
            LOG.info("Loaded {} properties for env='{}'", properties.size(), environment);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + file, e);
        }
    }

    public String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        String val = properties.getProperty(key);
        if (val == null) {
            throw new IllegalArgumentException("Missing config key: " + key);
        }
        return val;
    }

    public String get(String key, String fallback) {
        try {
            return get(key);
        } catch (IllegalArgumentException ex) {
            return fallback;
        }
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public String getEnvironment() {
        return environment;
    }
}
