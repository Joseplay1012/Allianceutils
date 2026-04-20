package net.joseplay.allianceutils.api.extensions;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class AllianceTaskManager {

    private final ExtensionScheduler scheduler = new ExtensionScheduler();
    private final AlliancePlugin extension;

    public AllianceTaskManager(AlliancePlugin extension) {
        this.extension = extension;
    }

    public ExtensionScheduler getScheduler() {
        return scheduler;
    }

    /**
     * Schedules a synchronous task.
     */
    public BukkitTask runTask(Runnable runnable) {
        return Bukkit.getScheduler().runTask(Allianceutils.getPlugin(), wrap(runnable));
    }

    public BukkitTask runTask(BukkitRunnable runnable) {
        return runnable.runTask(Allianceutils.getPlugin());
    }

    /**
     * Schedules a delayed synchronous task.
     */
    public BukkitTask runTaskLater(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(Allianceutils.getPlugin(), wrap(runnable), delay);
    }

    public BukkitTask runTaskLater(BukkitRunnable runnable, long delay) {
        return runnable.runTaskLater(Allianceutils.getPlugin(), delay);
    }

    /**
     * Schedules a repeating synchronous task and tracks it.
     */
    public BukkitTask runTaskTimer(Runnable runnable, long delay, long period) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                Allianceutils.getPlugin(), wrap(runnable), delay, period
        );
        scheduler.addTask(task.getTaskId(), task);
        return task;
    }

    public BukkitTask runTaskTimer(BukkitRunnable runnable, long delay, long period) {
        BukkitTask task = runnable.runTaskTimer(Allianceutils.getPlugin(), delay, period);
        scheduler.addTask(task.getTaskId(), task);
        return task;
    }

    /**
     * Schedules an asynchronous task.
     */
    public BukkitTask runTaskAsync(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(
                Allianceutils.getPlugin(), wrap(runnable)
        );
    }

    public BukkitTask runTaskAsync(BukkitRunnable runnable) {
        return runnable.runTaskAsynchronously(Allianceutils.getPlugin());
    }

    /**
     * Schedules a repeating asynchronous task and tracks it.
     */
    public BukkitTask runTaskAsyncTimer(Runnable runnable, long delay, long period) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                Allianceutils.getPlugin(), wrap(runnable), delay, period
        );
        scheduler.addTask(task.getTaskId(), task);
        return task;
    }

    public BukkitTask runTaskAsyncTimer(BukkitRunnable runnable, long delay, long period) {
        BukkitTask task = runnable.runTaskTimerAsynchronously(Allianceutils.getPlugin(), delay, period);
        scheduler.addTask(task.getTaskId(), task);
        return task;
    }

    /**
     * Wraps a runnable with centralized error handling.
     *
     * <p>Prevents scheduler threads from silently failing.</p>
     */
    private Runnable wrap(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                reportError("task execution", t);
            }
        };
    }

    /**
     * Reports execution errors in a standardized format.
     *
     * @param context execution context
     * @param t       thrown exception
     */
    private void reportError(String context, Throwable t) {
        extension.log.error("[AllianceUtils][{}] Error during {}.", extension.getExtensionName(), context, t);
    }
}