package net.joseplay.allianceutils.api.extensions;

import net.joseplay.allianceutils.api.extensions.interfaces.AllianceUtilsExtension;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
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
    public final org.slf4j.Logger log = LoggerFactory.getLogger(AlliancePlugin.class);

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
    @Deprecated(forRemoval = true)
    public final Set<BukkitTask> activeTasks = ConcurrentHashMap.newKeySet();

    /**
     * Tracks active scheduled tasks for proper lifecycle management.
     */
    public final AllianceTaskManager taskManager = new AllianceTaskManager(this);

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

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public AllianceTaskManager getTaskManager() {
        return taskManager;
    }

    /**
     * Schedules a synchronous task.
     */
    @Deprecated(forRemoval = true, since = "use #getTaskManager \n remove in 1.7.2")
    public BukkitTask runTask(Runnable runnable) {
        return taskManager.runTask(runnable);
    }

    @Deprecated(forRemoval = true, since = "use #getTaskManager \n remove in 1.7.2")
    public BukkitTask runTask(BukkitRunnable runnable) {
        return taskManager.runTask(runnable);
    }


    /**
     * Schedules a delayed synchronous task.
     */
    @Deprecated(forRemoval = true, since = "use #getTaskManager \n remove in 1.7.2")
    public BukkitTask runTaskLater(Runnable runnable, long delay) {
        return taskManager.runTaskLater(runnable, delay);
    }

    @Deprecated(forRemoval = true, since = "use #getTaskManager \n remove in 1.7.2")
    public BukkitTask runTaskLater(BukkitRunnable runnable, long delay) {
        return taskManager.runTaskLater(runnable, delay);
    }

    /**
     * Schedules a repeating synchronous task and tracks it.
     */
    @Deprecated(forRemoval = true, since = "use #getTaskManager \n remove in 1.7.2")
    public BukkitTask runTaskTimer(Runnable runnable, long delay, long period) {
        return taskManager.runTaskTimer(runnable, delay, period);
    }

    @Deprecated(forRemoval = true, since = "use #getTaskManager \n remove in 1.7.2")
    public BukkitTask runTaskTimer(BukkitRunnable runnable, long delay, long period) {
        return taskManager.runTaskTimer(runnable, delay, period);
    }

    /**
     * Schedules an asynchronous task.
     */
    @Deprecated(forRemoval = true, since = "use #getTaskManager \n remove in 1.7.2")
    public BukkitTask runTaskAsync(Runnable runnable) {
        return taskManager.runTaskAsync(runnable);
    }

    @Deprecated(forRemoval = true, since = "use #getTaskManager \n remove in 1.7.2")
    public BukkitTask runTaskAsync(BukkitRunnable runnable) {
        return taskManager.runTaskAsync(runnable);
    }

    /**
     * Schedules a repeating asynchronous task and tracks it.
     */
    @Deprecated(forRemoval = true, since = "use #getTaskManager \n remove in 1.7.2")
    public BukkitTask runTaskAsyncTimer(Runnable runnable, long delay, long period) {
        return taskManager.runTaskAsyncTimer(runnable, delay, period);
    }

    @Deprecated(forRemoval = true, since = "use #getTaskManager \n remove in 1.7.2")
    public BukkitTask runTaskAsyncTimer(BukkitRunnable runnable, long delay, long period) {
        return taskManager.runTaskAsyncTimer(runnable, delay, period);
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