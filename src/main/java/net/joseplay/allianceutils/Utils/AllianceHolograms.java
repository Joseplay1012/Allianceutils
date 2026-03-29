package net.joseplay.allianceutils.Utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class AllianceHolograms {
    private static Map<Location, ArmorStand> holograms;
    private final JavaPlugin plugin;

    public AllianceHolograms(JavaPlugin plugin) {
        this.plugin = plugin;
        this.holograms = new HashMap<>();
    }

    public static void createHologram(Location location, String[] lines) {
        double yOffset = 0.0;
        for (String line : lines) {
            ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, yOffset, 0), EntityType.ARMOR_STAND);
            hologram.setVisible(false);
            hologram.setCustomNameVisible(true);
            hologram.setCustomName(line);
            hologram.setGravity(false);
            hologram.setInvulnerable(true);
            hologram.setSmall(true);

            holograms.put(location.clone().add(0, yOffset, 0), hologram);

            yOffset -= 0.25;
        }
    }

}
