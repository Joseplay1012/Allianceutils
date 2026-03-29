package net.joseplay.allianceutils.DeBug;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DebugExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final JavaPlugin plugin;

    public DebugExceptionHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        plugin.getLogger().severe("[DEBUG] Ocorreu um erro não tratado:");
        plugin.getLogger().severe(throwable.getMessage());
        for (StackTraceElement element : throwable.getStackTrace()) {
            plugin.getLogger().severe(element.toString());
        }
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage("[§cDEBUG§f] Ocorreu um erro não tratado no servidor. Verifique o console para mais detalhes.");
            }
        }
    }
}
