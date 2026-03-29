package net.joseplay.allianceutils.api.worldGuard.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class NoExitRegion implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("alc.admin")) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null || from.getBlock().equals(to.getBlock())) return;

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld()));
        if (regionManager == null) return;

        BlockVector3 fromVec = BlockVector3.at(from.getX(), from.getY(), from.getZ());
        BlockVector3 toVec = BlockVector3.at(to.getX(), to.getY(), to.getZ());

        ApplicableRegionSet fromSet = regionManager.getApplicableRegions(fromVec);
        ApplicableRegionSet toSet = regionManager.getApplicableRegions(toVec);

        // Se ele está tentando sair de uma região com 'exit: deny'
        for (ProtectedRegion region : fromSet) {
            if (!toSet.getRegions().contains(region)) {
                // Verifica a flag 'exit' da região
                StateFlag.State exitFlag = region.getFlag(Flags.EXIT);
                StateFlag.State enterFlag = region.getFlag(Flags.ENTRY);
                if (exitFlag == StateFlag.State.DENY || enterFlag == StateFlag.State.DENY) {
                    event.setCancelled(true);

                    //drawnExpandCircle(player.getLocation(), player, 15, Particle.CLOUD, 90);

                    Bukkit.getScheduler().runTaskLater(Allianceutils.getPlugin(), () -> {
                        //drawnExpandCircle(player.getLocation(), player, 15, Particle.DUST_PLUME, 90);
                        showExpansiveBorders(player.getLocation(), region);


                        Vector knockback = from.toVector().subtract(to.toVector()).normalize().multiply(1.5);
                        knockback.setY(1);
                        player.setVelocity(knockback);


                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                        player.sendMessage("§cyou no exit region!");
                    }, 1L);
                    return;
                }
            }
        }
    }


    public void showExpansiveBorders(Location location, ProtectedRegion region) {
        World world = location.getWorld();

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        // roda async só o cálculo pesado (sem mexer com Bukkit API)
        Bukkit.getScheduler().runTaskAsynchronously(Allianceutils.getPlugin(), () -> {
            List<BlockVector3> bordasBrutas = new ArrayList<>();

            for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                    for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                        boolean bordaX = (x == min.getBlockX() || x == max.getBlockX());
                        boolean bordaY = (y == min.getBlockY() || y == max.getBlockY());
                        boolean bordaZ = (z == min.getBlockZ() || z == max.getBlockZ());

                        if (bordaX || bordaY || bordaZ) {
                            double dx = x - location.getX();
                            double dy = y - location.getY();
                            double dz = z - location.getZ();
                            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

                            if (dist < 20) {
                                bordasBrutas.add(BlockVector3.at(x, y, z));
                            }

                        }
                    }
                }
            }

            // de volta pro main thread
            Bukkit.getScheduler().runTask(Allianceutils.getPlugin(), () -> {
                List<Location> bordas = bordasBrutas.stream()
                        .map(v -> new Location(world, v.getX(), v.getY(), v.getZ()))
                        .toList();

                // Agora roda a animação expansiva
                new BukkitRunnable() {
                    int r = 1;

                    @Override
                    public void run() {
                        if (r > 20) {
                            cancel();
                            return;
                        }

                        Location center = location.clone();

                        for (Location loc : bordas) {
                            double dist = center.distance(loc);

                            if (dist <= r && dist > r - 1) {
                                world.spawnParticle(Particle.DUST_PLUME, loc.getBlock().getLocation().clone().add(0.5, 0.5, 0.5),
                                        3, 0.02, 0.02, 0.02, 0.01);
                            }
                        }

                        r++;
                    }
                }.runTaskTimer(Allianceutils.getPlugin(), 0L, 1L);
            });
        });
    }

}
