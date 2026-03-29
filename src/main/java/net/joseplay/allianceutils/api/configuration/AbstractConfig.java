package net.joseplay.allianceutils.api.configuration;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.Utils.GradientMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base abstraction for configuration systems.
 * Handles loading, validation, caching and default value fallback.
 */
public abstract class AbstractConfig {

    private static final Logger log = LoggerFactory.getLogger(AbstractConfig.class);

    protected final File file;
    protected final String configPath;
    protected FileConfiguration config;

    /**
     * Cached config values mapped by enum keys.
     */
    protected final Map<Enum<?>, Object> values = new HashMap<>();

    /**
     * Initializes configuration file and loads all values.
     *
     * @param file       Configuration file (e.g., config.yml)
     * @param configPath Root path inside config (can be empty)
     * @param keys       Enum class implementing ConfigKey
     */
    public <E extends Enum<E> & ConfigKey> AbstractConfig(File file, String configPath, Class<E> keys) {
        if (!file.exists()) file = getFileInPlugin(file);

        this.file = file;
        this.configPath = configPath;
        this.config = YamlConfiguration.loadConfiguration(file);

        loadConfig();
        initConfig(keys);
    }

    /**
     * Copies default config from plugin resources if file does not exist.
     */
    public File getFileInPlugin(File file){
        if (file.exists()) return file;

        try (InputStream in = Allianceutils.getInstance()
                .getResource(file.toPath().getFileName().toString())) {

            if (in != null) Files.copy(in, file.toPath());

        } catch (Exception e) {
            log.error("Failed to copy default config file: {}", file.getName(), e);
        }

        return file;
    }

    /**
     * Ensures file structure exists.
     */
    protected void loadConfig() {
        if (!file.exists() && file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
    }

    /**
     * Initializes config values and ensures missing entries are created.
     */
    protected <E extends Enum<E> & ConfigKey> void initConfig(Class<E> keys) {
        loadValues(keys);
        createInexistentConfig(keys);
    }

    /**
     * Creates missing or null config paths with default values.
     */
    protected <E extends Enum<E> & ConfigKey> void createInexistentConfig(Class<E> keys){
        if (config == null) {
            log.error("Configuration is null");
            return;
        }

        for (E key : keys.getEnumConstants()){
            if (config.contains(key.getPath()) && config.get(key.getPath()) != null) continue;

            config.set(key.getPath(), key.getDefaultValue());
        }

        try{
            saveConfig();
        } catch (IOException e) {
            log.error("Failed to save config file: {}", file.getName(), e);
        }
    }

    /**
     * Applies gradient formatting to messages.
     */
    protected String processMessage(String rawMsg) {
        if (rawMsg == null || rawMsg.trim().isEmpty()) {
            return "";
        }
        return GradientMessage.createGradientMessageAsString(rawMsg);
    }

    /**
     * Saves configuration to disk.
     */
    public void saveConfig() throws IOException {
        config.save(file);
    }

    /**
     * Resolves a config path considering optional root section.
     */
    private String resolvePath(String keyPath) {
        String sectionPath = configPath + "." + keyPath;

        if (config.contains(sectionPath)) return sectionPath;
        if (config.contains(keyPath)) return keyPath;

        return null;
    }

    /**
     * Loads values from config into cache.
     * Applies validation and fallback to default values.
     */
    protected <E extends Enum<E> & ConfigKey> void loadValues(Class<E> enumClass) {
        values.clear();

        for (E key : enumClass.getEnumConstants()) {
            String path = resolvePath(key.getPath());

            Object loaded = null;
            boolean valid = false;

            if (path == null) {
                loaded = key.getDefaultValue();
                valid = true;
            }

            if (path != null) {
                switch (key.getType()) {

                    case STRING -> {
                        if (config.isString(path)) {
                            String rawMsg = config.getString(path);
                            log.info("Loading {} with value {}", path, rawMsg);
                            loaded = processMessage(rawMsg);
                            valid = true;
                        }
                    }

                    case STRING_LIST -> {
                        if (config.isList(path)) {
                            List<String> rawList = config.getStringList(path);
                            log.info("Loading {} with value {}", path, rawList);

                            loaded = Collections.unmodifiableList(
                                    rawList.stream()
                                            .map(this::processMessage)
                                            .toList()
                            );
                            valid = true;
                        }
                    }

                    case INT -> {
                        if (config.isInt(path)) {
                            loaded = config.getInt(path);
                            log.info("Loading {} with value {}", path, loaded);
                            valid = true;
                        }
                    }

                    case INT_LIST -> {
                        if (config.isList(path)) {
                            List<?> rawList = config.getList(path);

                            List<Integer> intList = rawList.stream()
                                    .filter(obj -> obj instanceof Number)
                                    .map(obj -> ((Number) obj).intValue())
                                    .toList();

                            log.info("Loading {} with value {}", path, intList);

                            loaded = Collections.unmodifiableList(intList);
                            valid = !intList.isEmpty();
                        }
                    }

                    case BOOLEAN -> {
                        if (config.isBoolean(path)) {
                            loaded = config.getBoolean(path);
                            log.info("Loading {} with value {}", path, loaded);
                            valid = true;
                        }
                    }

                    case DOUBLE -> {
                        if (config.isInt(path) || config.isLong(path) || config.isDouble(path)) {
                            loaded = ((Number) config.get(path)).doubleValue();
                            log.info("Loading {} with value {}", path, loaded);
                            valid = true;
                        }
                    }

                    case LONG -> {
                        if (config.isInt(path) || config.isLong(path)) {
                            loaded = config.getLong(path);
                            log.info("Loading {} with value {}", path, loaded);
                            valid = true;
                        }
                    }

                    default -> log.warn("Unknown config type for path {}", path);
                }
            }

            if (!valid) {
                loaded = key.getDefaultValue();
                log.warn("Invalid or missing value for {}. Using default: {}", path, loaded);
            }

            values.put(key, loaded);
        }
    }

    /**
     * Retrieves a cached value or fallback default.
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(Enum<?> key) {
        return (T) values.getOrDefault(key, getDefaultValue(key));
    }

    /**
     * Retrieves a typed value with validation.
     */
    @SuppressWarnings("unchecked")
    public <P extends Enum & ConfigKey, C> C getValue(P key, ValueType type) {
        if (key.getType() != type) {
            throw new IllegalArgumentException(
                    "Key " + key + " is of type " + key.getType() + ", expected " + type
            );
        }

        return (C) values.getOrDefault(key, key.getDefaultValue());
    }

    // === Typed getters ===

    public <P extends Enum & ConfigKey> String getString(P key){
        return getValue(key, ValueType.STRING);
    }

    public <P extends Enum & ConfigKey> List<String> getStringList(P key){
        return getValue(key, ValueType.STRING_LIST);
    }

    public <P extends Enum & ConfigKey> int getInt(P key){
        return getValue(key, ValueType.INT);
    }

    public <P extends Enum & ConfigKey> long getLong(P key){
        return getValue(key, ValueType.LONG);
    }

    public <P extends Enum & ConfigKey> List<Integer> getIntList(P key){
        return getValue(key, ValueType.INT_LIST);
    }

    public <P extends Enum & ConfigKey> boolean getBoolean(P key){
        return getValue(key, ValueType.BOOLEAN);
    }

    public <P extends Enum & ConfigKey> double getDouble(P key){
        return getValue(key, ValueType.DOUBLE);
    }

    /**
     * Updates config value in memory and persists to file.
     */
    public boolean setValue(Enum<?> key, Object value) {
        ConfigKey configKey = getConfigKey(key);

        config.set(configKey.getPath(), value);
        values.put(key, value);

        try {
            saveConfig();
            return true;
        } catch (IOException e) {
            log.error("Failed to save {}: {}", file.getName(), e.getMessage());
            return false;
        }
    }

    /**
     * Provides default value for a key.
     */
    protected abstract Object getDefaultValue(Enum<?> key);

    /**
     * Maps enum to ConfigKey.
     */
    protected abstract ConfigKey getConfigKey(Enum<?> key);

    /**
     * Contract for configuration keys.
     */
    public interface ConfigKey {
        String getPath();
        ValueType getType();
        Object getDefaultValue();
    }

    /**
     * Supported config value types.
     */
    public enum ValueType {
        STRING(String.class),
        STRING_LIST(List.class),
        INT(Integer.class),
        INT_LIST(List.class),
        BOOLEAN(Boolean.class),
        DOUBLE(Double.class),
        LONG(Long.class);

        private final Class<?> aClass;

        ValueType(Class<?> aClass){
            this.aClass = aClass;
        }

        public Class<?> getaClass() {
            return aClass;
        }
    }
}