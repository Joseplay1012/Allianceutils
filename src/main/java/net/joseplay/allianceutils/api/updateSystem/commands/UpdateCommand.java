package net.joseplay.allianceutils.api.updateSystem.commands;

import net.joseplay.allianceutils.api.updateSystem.UpdateManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UpdateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player player){
            if (!player.hasPermission("alc.admin")) return false;
        }

        UpdateManager.checkUpdateAsync();
        commandSender.sendMessage("§aIniciando verificação de update...");
        commandSender.sendMessage("§aUpdatede verificado, verifique o console para mais informações.");
        return false;
    }
}
