package net.joseplay.allianceutils.api.extensions;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionScheduler {
    private final Map<Integer, BukkitTask> taskMap = new ConcurrentHashMap<>();
    private final Set<BukkitTask> tasksIds = ConcurrentHashMap.newKeySet();

    public BukkitTask getTaskById(int id){
        return taskMap.get(id);
    }


    public void addTask(int id, BukkitTask run){
        taskMap.put(id, run);
        tasksIds.add(run);
    }

    public BukkitTask removeTaskById(int id){
        BukkitTask task = taskMap.remove(id);

        if (task == null) return null;
        tasksIds.remove(task);

        return task;
    }

    private boolean deleteTask(BukkitTask task){
        return taskMap.values().removeIf(b -> b.equals(task));
    }

    public boolean removeTask(BukkitTask task){
        Objects.requireNonNull(task);

        boolean removed = deleteTask(task);

        if (removed){
            tasksIds.remove(task);

            if (!task.isCancelled()) task.cancel();
            return true;
        }

        return false;
    }

    public List<BukkitTask> getAllTasks(){
        return List.copyOf(taskMap.values());
    }

    public void stopAll(){
        for (Map.Entry<Integer, BukkitTask> entry : taskMap.entrySet()){
            if (entry.getValue().isCancelled()) continue;

            entry.getValue().cancel();
            tasksIds.remove(entry.getValue());
            taskMap.remove(entry.getKey());

            if (!entry.getValue().isCancelled()) Bukkit.getScheduler().cancelTask(entry.getKey());
        }
    }

    public boolean clear(){
        if (taskMap.isEmpty()) return false;

        stopAll();
        taskMap.clear();
        tasksIds.clear();
        return true;
    }

}
