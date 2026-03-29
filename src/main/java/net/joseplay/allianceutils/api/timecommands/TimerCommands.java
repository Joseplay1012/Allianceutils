package net.joseplay.allianceutils.api.timecommands;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.timecommands.data.TimeCommandsConfig;
import net.joseplay.allianceutils.api.timecommands.entity.TimeCommandTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class TimerCommands {

    private final JavaPlugin plugin = Allianceutils.getPlugin();
    private final TimeCommandsConfig config;
    private final Set<String> executedThisMinute = new HashSet<>();
    private String lastMinute = "";
    private BukkitTask task;

    public TimerCommands() {
        this.config = new TimeCommandsConfig(
                new File(plugin.getDataFolder(), "timecommand.yml")
        );
        start();
    }

    private void start() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
            String minuteKey = now.getHour() + ":" + now.getMinute();

            if (!minuteKey.equals(lastMinute)) {
                executedThisMinute.clear();
                lastMinute = minuteKey;
            }

            for (TimeCommandTask task : config.getTasks()) {
                if (executedThisMinute.contains(task.getId())) continue;
                if (!task.matches(now)) continue;

                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (String cmd : task.getCommands()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    }
                });

                executedThisMinute.add(task.getId());
            }
        }, 0L, 20L);
    }


    public void stop(){
        if (task != null && !task.isCancelled()) task.cancel();
    }
}
