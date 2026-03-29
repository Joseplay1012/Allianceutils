package net.joseplay.allianceutils.api.extensions;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.extensions.gui.ExtensionGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExtensionCommandUtils implements CommandExecutor, TabCompleter {
    private final ExtensionLoader extensionLoader = Allianceutils.getInstance().extensionLoader;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender.hasPermission("alc.admin")){

            if (strings.length > 0){
                if (strings[0].equalsIgnoreCase("reload")){
                    String extensionName = strings[1];

                    extensionLoader.reloadExtension(extensionName, e -> {
                        commandSender.sendMessage("§aExtensão §e" + e.extensionName + " §arecarregada.");
                    });
                    return true;
                } else if (strings[0].equalsIgnoreCase("load")){
                    String extensionName = strings[1];

                    extensionLoader.loadExtensionByName(extensionName, e -> {
                        commandSender.sendMessage("§aExtensão §e" + e.extensionName + " §acarregada.");
                    });
                    return true;
                } else if (strings[0].equalsIgnoreCase("unload")){
                    String extensionName = strings[1];

                    extensionLoader.unloadExtension(extensionName, e -> {
                        commandSender.sendMessage("§aExtensão §e" + e.extensionName + " §adescarregada.");
                    });
                    return true;
                }
            }

            if (!(commandSender instanceof Player)){
                List<String> extensionList = extensionLoader.getActiveExtensions();

                StringBuilder builder = new StringBuilder();

                for (String s1 : extensionList){
                    builder.append(s1).append(" ");
                }

                commandSender.sendMessage(builder.toString());
            } else if (commandSender instanceof  Player player){
                new ExtensionGUI().open(player);
//                player.spigot().sendMessage(extensionLoader.getActiveExtensionsAsSingleLine());
            }
        }


        return false;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1){
            return List.of("reload", "load", "unload");
        } else if (strings.length == 2){
            if (strings[0].equalsIgnoreCase("load")){
                List<String> files = new ArrayList<>();

                for (File file : extensionLoader.folder.listFiles((file, name) -> name.endsWith(".jar"))){
                    if (extensionLoader.getRegistry().containsKey(file.getName())) continue;
                    files.add(file.getName());
                }

                return files;
            }

            return extensionLoader.getActiveExtensions().stream()
                    .filter(s1 -> s1.toLowerCase().startsWith(strings[1].toLowerCase()))
                    .toList();
        }

        return List.of();
    }
}
