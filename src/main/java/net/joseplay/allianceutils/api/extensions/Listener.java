package net.joseplay.allianceutils.api.extensions;

import net.joseplay.allianceutils.api.extensions.interfaces.AllianceCommandExecutor;
import net.joseplay.allianceutils.api.internalListener.AuListener;
import net.joseplay.allianceutils.api.internalListener.annotations.AuEventHandler;
import net.joseplay.allianceutils.api.internalListener.events.ExtensionDisabledEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Listener implements org.bukkit.event.Listener, AuListener {

    //@EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        if (!message.startsWith("/")) return;

        String[] parts = message.substring(1).split(" ");

        String commandName = parts[0].contains(":") ? parts[0].split(":")[0] : parts[0];

        //String commandName = command;
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Optional<AllianceCommandExecutor> opt = Alliance.getAllianceCommandManager().getCommand(commandName);
        if (opt.isPresent()) {
            event.setCancelled(true);
            opt.get().execute(event.getPlayer(), args);
        }
    }

    //@EventHandler
    public void onConsoleCommand(ServerCommandEvent event){
        String message = event.getCommand();
        if (message.trim().isEmpty()) return;

        String[] parts = message.split(" ");

        String commandName = parts[0].contains(":") ? parts[0].split(":")[0] : parts[0];

        //String commandName = command;
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Optional<AllianceCommandExecutor> opt = Alliance.getAllianceCommandManager().getCommand(commandName);
        if (opt.isPresent()) {
            event.setCancelled(true);
            opt.get().execute(event.getSender(), args);
        }
    }

    @AuEventHandler
    public void onExtensionDisabled(ExtensionDisabledEvent event){
    }

    public void onTabSend(PlayerCommandSendEvent event) {
        List<String> customCommands = Alliance.getAllianceCommandManager().getAllCommandNames();
        event.getCommands().addAll(customCommands);
    }

}
