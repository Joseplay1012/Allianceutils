package net.joseplay.allianceutils.api.menu;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.joseplay.allianceutils.DeBug.SendDebugMessage.sendDebugMessage;

public class MenuUpdater {
    private static final Map<Menu, BukkitRunnable> taskMap = new ConcurrentHashMap<>();

    public static void register(JavaPlugin plugin, Menu menu, long intervalTicks) {
        if (taskMap.containsKey(menu)) return;

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (menu.getInventory().getViewers().isEmpty()) {
                    //Bukkit.broadcast(sendDebugMessage("Parando task para um menu"), "alc.admin");
                    cancel();
                    taskMap.remove(menu);
                    return;
                }

                if (menu.usePlaceholders()) menu.setPlaceholders();
                //menu.onSetItems();
                menu.update();
            }
        };

        task.runTaskTimer(plugin, intervalTicks, intervalTicks);
        taskMap.put(menu, task);
    }

    public static void unregister(Menu menu) {
        if (taskMap.containsKey(menu)) {
            Bukkit.broadcast(sendDebugMessage("Parando task para um menu"), "alc.admin");
            BukkitRunnable task = taskMap.remove(menu);
            if (task != null) task.cancel();
        }
    }
}
