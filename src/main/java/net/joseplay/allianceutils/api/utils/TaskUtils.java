package net.joseplay.allianceutils.api.utils;

import net.joseplay.allianceutils.Allianceutils;

public class TaskUtils {
    public static void runSync(Runnable action) {
        if (org.bukkit.Bukkit.isPrimaryThread()) {
            action.run();
        } else {
            org.bukkit.Bukkit.getScheduler().runTask(
                    Allianceutils.getPlugin(),
                    action
            );
        }
    }

}
