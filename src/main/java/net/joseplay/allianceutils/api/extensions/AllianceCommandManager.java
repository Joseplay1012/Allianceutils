package net.joseplay.allianceutils.api.extensions;

import net.joseplay.allianceutils.api.extensions.interfaces.AllianceCommandExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the lifecycle, storage, and lookup of custom commands
 * registered through the Alliance API.
 *
 * <p>This manager maintains a mapping between {@link AlliancePlugin}
 * instances and their associated {@link AllianceCommandExecutor}s,
 * allowing isolation per extension and centralized command resolution.</p>
 */
public class AllianceCommandManager {

    /**
     * Stores commands grouped by their owning plugin.
     *
     * <p>Uses {@link ConcurrentHashMap} to allow thread-safe access
     * in environments where commands may be registered dynamically
     * at runtime.</p>
     */
    public Map<AlliancePlugin, List<AllianceCommandExecutor>> commands = new ConcurrentHashMap<>();

    /**
     * Responsible for injecting commands into Bukkit's command map
     * without requiring plugin.yml declarations.
     *
     * <p>This enables dynamic command registration at runtime.</p>
     */
    public FakeCommandRegister fakeCommandRegister = new FakeCommandRegister();

    /**
     * Registers a command under a specific plugin.
     *
     * <p>The command is stored internally and also registered into
     * Bukkit's command system via {@link FakeCommandRegister}.</p>
     *
     * @param plugin  the owning plugin
     * @param command the command executor instance
     */
    public void registerCommand(AlliancePlugin plugin, AllianceCommandExecutor command) {
        commands.computeIfAbsent(plugin, k -> new ArrayList<>()).add(command);
        fakeCommandRegister.registerFakeCommand(command, plugin);
    }

    /**
     * Retrieves a command by its primary name or any of its aliases.
     *
     * <p>The lookup is case-insensitive and searches across all
     * registered plugins.</p>
     *
     * @param name the command name or alias
     * @return an {@link Optional} containing the command if found
     */
    public Optional<AllianceCommandExecutor> getCommand(String name) {
        return commands.values().stream()
                .flatMap(List::stream)
                .filter(cmd -> cmd.getName().equalsIgnoreCase(name) || cmd.alliances().contains(name))
                .findFirst();
    }

    /**
     * Retrieves an immutable list of all registered command executors.
     *
     * <p>This method aggregates commands from all plugins into a single
     * collection.</p>
     *
     * @return unmodifiable list of all commands
     */
    public List<AllianceCommandExecutor> getAllCommands() {
        List<AllianceCommandExecutor> executors = new ArrayList<>();

        for (List<AllianceCommandExecutor> list : commands.values()) {
            executors.addAll(list);
        }

        return Collections.unmodifiableList(executors);
    }

    /**
     * Retrieves all registered command names, including aliases,
     * formatted with a leading '/'.
     *
     * <p>Useful for command parsing or intercepting raw command input.</p>
     *
     * @return list of all command names with prefix
     */
    public List<String> getAllCommandNames() {
        List<String> all = new ArrayList<>();
        commands.values().forEach(list -> list.forEach(cmd -> {
            all.add("/" + cmd.getName());
            cmd.alliances().forEach(a -> all.add("/" + a));
        }));
        return all;
    }

    /**
     * Retrieves all command names (including aliases) for a specific plugin.
     *
     * <p>Unlike {@link #getAllCommandNames()}, this method does not include
     * the '/' prefix.</p>
     *
     * @param plugin the plugin whose commands should be retrieved
     * @return list of command names, or empty list if none are registered
     */
    public List<String> getAllCommandNames(AlliancePlugin plugin) {
        List<String> all = new ArrayList<>();

        if (commands.get(plugin) == null) {
            return List.of();
        }

        commands.get(plugin).forEach(cmd -> {
            all.add(cmd.getName());
            cmd.alliances().forEach(a -> all.add(a));
        });

        return all;
    }

    /**
     * Unregisters all commands associated with a specific plugin.
     *
     * <p>This removes the commands from the internal registry,
     * but does not explicitly remove them from Bukkit's command map
     * unless handled separately by {@link FakeCommandRegister}.</p>
     *
     * @param extension the plugin whose commands should be removed
     */
    public void unregisterCommands(AlliancePlugin extension) {
        if (commands.containsKey(extension)) {
            commands.remove(extension);
        }
    }
}