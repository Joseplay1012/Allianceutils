package net.joseplay.allianceutils.api.extensions;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.extensions.interfaces.AllianceCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class FakeCommandRegister {

    private static final String PREFIX = "[Extensions] [Command] ";

    /**
     * Retrieves Bukkit's internal CommandMap via reflection.
     *
     * <p>This is required for dynamic command registration.</p>
     *
     * <b>Use when:</b>
     * - Registering or unregistering commands dynamically
     *
     * <b>Avoid when:</b>
     * - You can use plugin.yml (preferred for static commands)
     *
     * <b>Side effects:</b>
     * - Relies on internal Bukkit implementation (may break on updates)
     *
     * @return CommandMap instance
     */
    private CommandMap getCommandMap() {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (CommandMap) field.get(Bukkit.getServer());
        } catch (Exception e) {
            throw new RuntimeException("Failed to access CommandMap", e);
        }
    }

    /**
     * Registers a dynamic command and binds it to an AllianceCommandExecutor.
     *
     * <p>The command is injected into Bukkit at runtime.</p>
     *
     * <b>Use when:</b>
     * - Loading extensions dynamically
     *
     * <b>Avoid when:</b>
     * - Registering large batches without batching sync (performance issue)
     *
     * <b>Side effects:</b>
     * - Triggers command tree resync (expensive)
     *
     * @param executor command executor
     * @param extension owning extension
     */
    public void registerFakeCommand(AllianceCommandExecutor executor, AlliancePlugin extension) {
        CommandMap map = getCommandMap();

        unregisterFakeCommand(executor, extension);

        String name = executor.getName();
        List<String> aliases = List.copyOf(executor.alliances());

        Bukkit.getLogger().info(PREFIX + "Registering command '" + name +
                "' (Extension: " + extension.getExtensionName() + ")");

        Command command = new BukkitCommand(name) {

            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {

                Optional<AllianceCommandExecutor> opt =
                        Alliance.getAllianceCommandManager().getCommand(name);

                if (opt.isPresent()) {
                    opt.get().execute(sender, args);
                    return true;
                }

                sender.sendMessage(allianceFontReplace(
                        "§c❌ Command data not found for §e" + name
                ));

                if (sender.hasPermission("alc.admin")) {
                    sender.sendMessage("");
                    sender.sendMessage(allianceFontReplace("§d🔺 Staff only"));
                    sender.sendMessage(allianceFontReplace(
                            "§cExtension: §e" + extension.getExtensionName()
                    ));
                }

                return false;
            }

            @Override
            public @NotNull List<String> tabComplete(
                    @NotNull CommandSender sender,
                    @NotNull String alias,
                    @NotNull String[] args
            ) {
                if (!(sender instanceof Player player)) {
                    return Collections.emptyList();
                }

                return Alliance.getAllianceCommandManager()
                        .getCommand(name)
                        .map(cmd -> cmd.tabComplete(player, args))
                        .orElse(Collections.emptyList());
            }
        };

        command.setAliases(aliases);
        map.register(extension.getExtensionName(), command);

        syncCommands();
    }

    /**
     * Forces a full command tree synchronization with the client.
     *
     * <p>This is required after dynamic command changes.</p>
     *
     * <b>Use when:</b>
     * - After registering/unregistering commands
     *
     * <b>Avoid when:</b>
     * - Inside loops or bulk operations (VERY expensive)
     *
     * <b>Side effects:</b>
     * - Triggers full command rebuild on server
     */
    private void syncCommands() {
        try {
            Object server = Bukkit.getServer();
            var method = server.getClass().getMethod("syncCommands");

            Bukkit.getLogger().info(PREFIX + "Syncing command tree...");

            Bukkit.getScheduler().runTask(Allianceutils.getPlugin(), () -> {
                try {
                    method.invoke(server);
                    Bukkit.getLogger().info(PREFIX + "Command sync completed.");
                } catch (Exception e) {
                    Bukkit.getLogger().severe(PREFIX + "Command sync failed.");
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            Bukkit.getLogger().severe(PREFIX + "Failed to access syncCommands.");
            e.printStackTrace();
        }
    }

    /**
     * Unregisters a command and all its aliases from Bukkit.
     *
     * <p>Directly manipulates knownCommands map.</p>
     *
     * <b>Use when:</b>
     * - Unloading extensions
     *
     * <b>Avoid when:</b>
     * - You need compatibility across unknown server forks
     *
     * <b>Side effects:</b>
     * - Modifies internal Bukkit structures
     * - Requires syncCommands()
     *
     * @param executor executor
     * @param extension owning extension
     */
    public void unregisterFakeCommand(AllianceCommandExecutor executor, AlliancePlugin extension) {
        CommandMap map = getCommandMap();
        Map<String, Command> known = map.getKnownCommands();

        String name = executor.getName().toLowerCase();
        String prefix = extension.getExtensionName().toLowerCase();

        Bukkit.getLogger().warning(PREFIX + "Unregistering command '" + name + "'");

        known.remove(name);
        known.remove(prefix + ":" + name);

        for (String alias : executor.alliances()) {
            String a = alias.toLowerCase();
            known.remove(a);
            known.remove(prefix + ":" + a);
        }

        syncCommands();
    }
}