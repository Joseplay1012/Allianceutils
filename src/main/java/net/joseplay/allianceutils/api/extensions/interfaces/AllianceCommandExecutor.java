package net.joseplay.allianceutils.api.extensions.interfaces;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Represents a command handler abstraction.
 */
public interface AllianceCommandExecutor {

    /**
     * Command name (without "/")
     */
    String getName();

    /**
     * Aliases of the command
     */
    List<String> alliances();

    /**
     * Executes the command
     *
     * @param sender Command sender (player or console)
     * @param args Command arguments
     */
    void execute(CommandSender sender, String[] args);

    /**
     * Tab completion handler
     *
     * @param sender Command sender
     * @param args Arguments typed so far
     * @return List of suggestions
     */
    List<String> tabComplete(CommandSender sender, String[] args);
}