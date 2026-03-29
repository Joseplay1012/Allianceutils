package net.joseplay.allianceutils.api.preferences.command;

import net.joseplay.allianceutils.api.preferences.gui.PreferencesCategoriesGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PreferencesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player player){
            new PreferencesCategoriesGUI().open(player);
        }

        return false;
    }
}
