package net.joseplay.allianceutils.api.extensions;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.extensions.interfaces.AllianceCommandExecutor;
import net.joseplay.allianceutils.api.extensions.interfaces.AllianceUtilsExtension;
import net.joseplay.allianceutils.api.internalListener.EventManager;
import net.joseplay.allianceutils.api.internalListener.events.ExtensionDisabledEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

/**
 * Responsible for dynamically loading, unloading, and managing
 * Alliance extensions from JAR files.
 *
 * <p>This class handles classloading, lifecycle invocation,
 * resource parsing (extension.yml), and cleanup operations.</p>
 */
public class ExtensionLoader {

    private final Allianceutils mainPlugin;
    private final ExtensionRegistry registry = new ExtensionRegistry();

    /**
     * Directory where extension JARs are stored.
     */
    public final File folder;

    private final String prefix = "[Extensions] ";

    public ExtensionLoader(Allianceutils mainPlugin) {
        this.mainPlugin = mainPlugin;
        this.folder = new File(mainPlugin.getDataFolder(), "extensions");
    }

    /**
     * Loads all extensions found in the extensions directory.
     */
    public void loadExtensions() {
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) return;

        for (File jar : files) {
            loadExtension(jar);
        }
    }

    /**
     * Parses an extension JAR and creates its container without enabling it.
     *
     * @param jar extension file
     * @return ExtensionContainer or null if invalid
     */
    public ExtensionContainer getExtensionContainer(File jar) {
        try {
            URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{jar.toURI().toURL()},
                    mainPlugin.getClass().getClassLoader()
            );

            mainPlugin.getLogger().info(prefix + "Loading extension file: " + jar.getName());

            JarFile jarFile = new JarFile(jar);

            JarEntry entry = jarFile.getJarEntry("extension.yml");
            if (entry == null) {
                mainPlugin.getLogger().warning(prefix + "extension.yml not found in: " + jar.getName());
                return null;
            }

            InputStream input = jarFile.getInputStream(entry);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(input));

            String mainClass = config.getString("main");
            String extensionName = config.getString("name");
            String extensionDescription = config.getString("description", "AllianceUtils extension");
            String extensionVersion = config.getString("version", "1.0");
            List<String> extensionAuthors = config.getStringList("authors");

            if (mainClass == null) {
                mainPlugin.getLogger().warning(prefix + "Missing 'main' in extension.yml: " + jar.getName());
                return null;
            }

            if (extensionName == null) {
                mainPlugin.getLogger().warning(prefix + "Missing 'name' in extension.yml: " + jar.getName());
                return null;
            }

            File extensionDataFolder = new File(folder, extensionName);

            Class<?> clazz = Class.forName(mainClass, true, classLoader);

            if (!AllianceUtilsExtension.class.isAssignableFrom(clazz)) {
                mainPlugin.getLogger().warning(prefix + "Main class does not implement AllianceUtilsExtension: " + mainClass);
                return null;
            }

            AllianceUtilsExtension extension = (AllianceUtilsExtension) clazz.getDeclaredConstructor().newInstance();

            if (extension instanceof AlliancePlugin plugin) {
                plugin.setExtensionName(extensionName);
                plugin.setExtensionDescription(extensionDescription);
                plugin.setExtensionVersion(extensionVersion);
                plugin.setExtensionAuthors(extensionAuthors);
                plugin.setDataFolder(extensionDataFolder);
                plugin.setStartTime(Instant.now());
            }

            return new ExtensionContainer(extension, classLoader, extensionName, jar.getName());

        } catch (Exception e) {
            mainPlugin.getLogger().severe(prefix + "Failed to load extension: " + jar.getName());
            e.printStackTrace();
            return null;
        }
    }

    public void loadExtension(File jar) {
        loadExtension(jar, e -> {});
    }

    /**
     * Loads and enables an extension from a JAR file.
     */
    public void loadExtension(File jar, Consumer<ExtensionContainer> action) {
        if (registry.containsKey(jar.getName())) {
            mainPlugin.getLogger().warning(prefix + "Duplicate extension detected: " + jar.getName());
            return;
        }

        ExtensionContainer container = null;

        try {
            container = getExtensionContainer(jar);
        } catch (NoClassDefFoundError e) {
            mainPlugin.getLogger().severe(prefix + "Failed to load extension: " + jar.getName());
            e.printStackTrace();
            return;
        }

        if (container == null) {
            mainPlugin.getLogger().warning(prefix + "Extension could not be loaded: " + jar.getName());
            return;
        }

        container.extension.onEnable(mainPlugin);

        registry.put(jar.getName(), container);

        mainPlugin.getLogger().info(prefix + "Extension successfully loaded: " + jar.getName());
        action.accept(container);
    }

    public void loadExtension(ExtensionContainer container, Consumer<ExtensionContainer> action) {
        if (container == null) return;

        if (registry.containsKey(container.extensionFileName)) {
            mainPlugin.getLogger().warning(prefix + "Duplicate extension detected: " + container.extensionFileName);
            return;
        }

        container.extension.onEnable(mainPlugin);
        registry.put(container.extensionFileName, container);

        mainPlugin.getLogger().info(prefix + "Extension successfully loaded: " + container.extensionFileName);
        action.accept(container);
    }

    /**
     * Disables and unloads all extensions.
     */
    public void disableExtensions() {
        for (ExtensionContainer container : registry.values()) {
            unloadExtension(container, e -> {}, true);
        }
        registry.clear();
    }

    public void reloadExtension(String name) {
        reloadExtension(name, e -> {});
    }

    /**
     * Reloads a specific extension by name.
     */
    public void reloadExtension(String name, Consumer<ExtensionContainer> action) {
        for (Map.Entry<String, ExtensionContainer> entry : registry.entrySet()) {
            ExtensionContainer container = entry.getValue();

            if (container.extensionName.equalsIgnoreCase(name)) {
                unloadExtension(container, e -> {
                    File file = new File(folder, e.extensionFileName);

                    if (file.exists()) {
                        loadExtension(file);
                    }
                    action.accept(e);
                });
            }
        }
    }

    public void unloadExtension(String name) {
        unloadExtension(name, e -> {});
    }

    public void unloadExtension(String name, Consumer<ExtensionContainer> action) {
        for (Map.Entry<String, ExtensionContainer> entry : registry.entrySet()) {
            if (entry.getValue().extensionName.equalsIgnoreCase(name)) {
                unloadExtension(entry.getValue(), action::accept);
                break;
            }
        }
    }

    public void unloadExtension(ExtensionContainer container, Consumer<ExtensionContainer> action) {
        unloadExtension(container, action, 0, false);
    }

    public void unloadExtension(ExtensionContainer container, Consumer<ExtensionContainer> action, boolean onDisabled) {
        unloadExtension(container, action, 0, onDisabled);
    }

    /**
     * Handles safe unloading of an extension with retry logic.
     */
    private void unloadExtension(ExtensionContainer container, Consumer<ExtensionContainer> action, int attempts, boolean isOnDisabled) {
        try {
            if (container.extension instanceof AlliancePlugin extension) {
                if (!isOnDisabled) unregisterCommands(extension);

                extension.onDisable();
                clearExtension(container);
            }

            container.classLoader.close();
            action.accept(registry.remove(container.extensionFileName));

        } catch (Exception e) {
            attempts++;

            if (attempts >= 3) {
                mainPlugin.getLogger().severe(prefix + "Persistent failure unloading extension: " + container.extensionFileName);

                try {
                    clearExtension(container);
                    container.classLoader.close();
                } catch (IOException ioException) {
                    mainPlugin.getLogger().severe(prefix + "Failed to close classloader: " + ioException.getMessage());
                }

                registry.remove(container.extensionFileName);
                action.accept(container);
            } else {
                mainPlugin.getLogger().warning(prefix + "Error unloading extension (attempt " + attempts + "), retrying...");
                unloadExtension(container, action, attempts, isOnDisabled);
            }
        }
    }

    /**
     * Unregisters all commands associated with an extension.
     */
    private void unregisterCommands(AlliancePlugin alliancePlugin) {
        if (!Alliance.getAllianceCommandManager().commands.containsKey(alliancePlugin)) return;

        List<AllianceCommandExecutor> executors =
                Alliance.getAllianceCommandManager().commands.get(alliancePlugin);

        for (AllianceCommandExecutor executor : executors) {
            Alliance.getAllianceCommandManager()
                    .fakeCommandRegister
                    .unregisterFakeCommand(executor, alliancePlugin);
        }
    }

    /**
     * Cleans up all runtime resources associated with an extension.
     */
    private void clearExtension(ExtensionContainer container) {
        if (container.extension instanceof AlliancePlugin extension) {

            for (BukkitTask task : new ArrayList<>(extension.activeTasks)) {
                if (task != null && !task.isCancelled()) {
                    task.cancel();
                }
            }

            extension.activeTasks.clear();

            Alliance.getAllianceListenerManager().unregisterListeners(extension);
            Alliance.getAllianceCommandManager().commands.remove(extension);

            EventManager.callEvent(new ExtensionDisabledEvent(extension));
            EventManager.unregisterAll(extension);
        }
    }

    public void loadExtensionByName(String name) {
        loadExtensionByName(name, e -> {});
    }

    public void loadExtensionByName(String name, Consumer<ExtensionContainer> action) {
        File file = new File(folder, name);

        if (file.exists()) {
            loadExtension(file, action::accept);
        }
    }

    /**
     * Returns the list of currently active extensions.
     */
    public List<String> getActiveExtensions() {
        List<String> list = new ArrayList<>();

        for (Map.Entry<String, ExtensionContainer> entry : registry.entrySet()) {
            if (entry.getValue().extension instanceof AlliancePlugin extension) {
                list.add(extension.getExtensionName());
            }
        }

        return list;
    }

    /**
     * Builds a formatted TextComponent listing active extensions
     * with hoverable metadata.
     */
    public TextComponent getActiveExtensionsAsSingleLine() {
        TextComponent full = new TextComponent(
                allianceFontReplace("§eExtensions (§a" + getActiveExtensions().size() + "§e)§f: ")
        );

        boolean first = true;

        for (Map.Entry<String, ExtensionContainer> entry : registry.entrySet()) {
            if (entry.getValue().extension instanceof AlliancePlugin extension) {

                if (!first) full.addExtra(new TextComponent(", "));
                first = false;

                String info = allianceFontReplace(
                        "§eName: §f" + extension.getExtensionName() + "\n" +
                                "§eAuthors: §f" + extension.getExtensionAuthors() + "\n" +
                                "§eVersion: §a" + extension.getExtensionVersion() + "\n" +
                                "§eDescription: §a" + extension.getExtensionDescription()
                ) + "\n\n§eCommands: §a" +
                        Alliance.getAllianceCommandManager().getAllCommandNames(extension);

                TextComponent name = new TextComponent("§6" + extension.getExtensionName());
                name.setHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new TextComponent[]{new TextComponent(info)}
                ));

                full.addExtra(name);
            }
        }

        return full;
    }

    public ExtensionRegistry getRegistry() {
        return registry;
    }
}