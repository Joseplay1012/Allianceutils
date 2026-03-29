package net.joseplay.allianceutils.api.utils;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

public class ToastUtils {
    public static void sendToast(Player player, String id, String title, String description,
                                 Material icon, String frame) {

        // frame: "task", "challenge", "goal"

        String json = """
                {
                  "display": {
                    "icon": {
                      "id": "minecraft:%ICON%",
                      "count": 1
                    },
                    "title": {"text": "%TITLE%"},
                    "description": {"text": "%DESC%"},
                    "background": "minecraft:textures/gui/advancements/backgrounds/adventure.png",
                    "frame": "%FRAME%",
                    "announce_to_chat": false,
                    "show_toast": true,
                    "hidden": true
                  },
                  "criteria": {
                    "impossible": {
                      "trigger": "minecraft:impossible"
                    }
                  }
                }
                """;


        json = json.replace("%ICON%", icon.name().toLowerCase())
                .replace("%TITLE%", title)
                .replace("%DESC%", description)
                .replace("%FRAME%", frame.toLowerCase());

        NamespacedKey key = new NamespacedKey(Allianceutils.getPlugin(), "custom_toast_" + id);
        Advancement advancement = Bukkit.getAdvancement(key) != null ? Bukkit.getAdvancement(key) : Bukkit.getUnsafe().loadAdvancement(key, json);

        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        for (String criteria : progress.getRemainingCriteria()) {
            progress.awardCriteria(criteria);
        }

        Bukkit.getScheduler().runTaskLater(Allianceutils.getPlugin(), () -> {
            AdvancementProgress p = player.getAdvancementProgress(advancement);

            for (String crit : p.getAwardedCriteria()) {
                p.revokeCriteria(crit);
            }
            Bukkit.getUnsafe().removeAdvancement(key);
        }, 2);
    }

}
