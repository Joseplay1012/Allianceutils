package net.joseplay.allianceutils.api.extensions;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.extensions.interfaces.AllianceUtilsExtension;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Base abstraction for Alliance extensions.
 *
 * <p>This class provides common infrastructure such as configuration
 * management, resource access, task scheduling, and lifecycle metadata.</p>
 */
public abstract class AlliancePlugin implements AllianceUtilsExtension {

    /**
     * Internal SLF4J logger for low-level logging.
     */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AlliancePlugin.class);

    /**
     * Directory where extension-specific data is stored.
     */
    private File dataFolder;

    /**
     * Cached configuration instance loaded from config.yml.
     */
    private YamlConfiguration config;

    /**
     * Extension metadata fields.
     */
    private String extensionName;
    private String extensionVersion;
    private String extensionDescription;
    private List<String> extensionAuthors;

    /**
     * Timestamp indicating when the extension was started.
     */
    private Instant startTime;

    /**
     * Tracks active scheduled tasks for proper lifecycle management.
     */
    public final Set<BukkitTask> activeTasks = ConcurrentHashMap.newKeySet();

    public File getDataFolder() {
        return dataFolder;
    }

    public void setDataFolder(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }

    public void setExtensionVersion(String extensionVersion) {
        this.extensionVersion = extensionVersion;
    }

    public void setExtensionDescription(String extensionDescription) {
        this.extensionDescription = extensionDescription;
    }

    public void setExtensionAuthors(List<String> extensionAuthors) {
        this.extensionAuthors = extensionAuthors;
    }

    /**
     * Ensures that a default config.yml exists in the extension's data folder.
     *
     * <p>If the file does not exist, it is extracted from the extension's JAR.</p>
     */
    public void createDefaultConfig() {
        if (dataFolder == null) return;

        File configFile = new File(dataFolder, "config.yml");

        if (configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(configFile);
            return;
        }

        try {
            CodeSource source = getClass().getProtectionDomain().getCodeSource();
            if (source == null) return;

            URL jarUrl = source.getLocation();

            try (JarFile jarFile = new JarFile(new File(jarUrl.toURI()))) {
                JarEntry entry = jarFile.getJarEntry("config.yml");

                if (entry == null) {
                    log.error("[AllianceUtils][{}] config.yml not found inside extension JAR.", extensionName);
                    return;
                }

                InputStream in = jarFile.getInputStream(entry);
                dataFolder.mkdirs();

                Files.copy(in, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                config = YamlConfiguration.loadConfiguration(configFile);

                log.info("[AllianceUtils][{}] config.yml successfully extracted from JAR.", extensionName);
            }
        } catch (Exception e) {
            log.error("[AllianceUtils][{}] Failed to create default config.", extensionName, e);
        }
    }

    /**
     * Retrieves a resource file from inside the extension JAR.
     *
     * <p>Returns a safe copy of the InputStream to avoid issues
     * with closed JAR streams.</p>
     *
     * @param path resource path inside the JAR
     * @return InputStream or null if not found
     */
    public InputStream getResource(String path) {
        try {
            CodeSource source = getClass().getProtectionDomain().getCodeSource();
            if (source == null) return null;

            File jarFile = new File(source.getLocation().toURI());

            try (JarFile jar = new JarFile(jarFile)) {
                JarEntry entry = jar.getJarEntry(path);
                if (entry == null) return null;

                try (InputStream in = jar.getInputStream(entry)) {
                    byte[] bytes = in.readAllBytes();
                    return new ByteArrayInputStream(bytes);
                }
            }
        } catch (Exception e) {
            log.error("[AllianceUtils][{}] Failed to load resource: {}", extensionName, path, e);
            return null;
        }
    }

    /**
     * Provides a namespaced logger for the extension.
     *
     * @return custom logger instance
     */
    public Logger getLogger() {
        return new Logger("[AllianceUtils] " + extensionName);
    }

    public String getExtensionName() {
        return extensionName;
    }

    /**
     * Retrieves the configuration, ensuring it is initialized.
     *
     * @return configuration instance
     */
    public YamlConfiguration getConfig() {
        createDefaultConfig();
        return config;
    }

    public String getExtensionVersion() {
        return extensionVersion;
    }

    public String getExtensionDescription() {
        return extensionDescription;
    }

    public List<String> getExtensionAuthors() {
        return extensionAuthors;
    }

    /**
     * Schedules a synchronous task.
     */
    public BukkitTask runTask(Runnable runnable) {
        return Bukkit.getScheduler().runTask(Allianceutils.getPlugin(), wrap(runnable));
    }

    /**
     * Schedules a delayed synchronous task.
     */
    public BukkitTask runTaskLater(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(Allianceutils.getPlugin(), wrap(runnable), delay);
    }

    /**
     * Schedules a repeating synchronous task and tracks it.
     */
    public BukkitTask runTaskTimer(Runnable runnable, long delay, long period) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                Allianceutils.getPlugin(),
                wrap(runnable),
                delay,
                period
        );
        activeTasks.add(task);
        return task;
    }

    /**
     * Schedules an asynchronous task.
     */
    public BukkitTask runTaskAsync(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(
                Allianceutils.getPlugin(),
                wrap(runnable)
        );
    }

    /**
     * Schedules a repeating asynchronous task and tracks it.
     */
    public BukkitTask runTaskAsyncTimer(Runnable runnable, long delay, long period) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                Allianceutils.getPlugin(),
                wrap(runnable),
                delay,
                period
        );
        activeTasks.add(task);
        return task;
    }

    /**
     * Wraps a runnable with centralized error handling.
     *
     * <p>Prevents scheduler threads from silently failing.</p>
     */
    private Runnable wrap(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                reportError("task execution", t);
            }
        };
    }

    /**
     * Reports execution errors in a standardized format.
     *
     * @param context execution context
     * @param t       thrown exception
     */
    private void reportError(String context, Throwable t) {
        log.error("[AllianceUtils][{}] Error during {}.", extensionName, context, t);
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    /**
     * Hook for conditional disabling logic.
     *
     * @return true if the extension should be disabled
     */
    private boolean shouldDisable() {
        return false;
    }
}