package net.joseplay.allianceutils.api.textUtils;

import org.bukkit.ChatColor;

import java.util.Random;

public class RainbowText {

    private static final ChatColor[][] RAINBOW_VARIATIONS = {
            {ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.AQUA, ChatColor.BLUE, ChatColor.LIGHT_PURPLE},
            {ChatColor.LIGHT_PURPLE, ChatColor.BLUE, ChatColor.AQUA, ChatColor.GREEN, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED},
            {ChatColor.YELLOW, ChatColor.RED, ChatColor.GREEN, ChatColor.BLUE, ChatColor.GOLD, ChatColor.AQUA, ChatColor.LIGHT_PURPLE}
    };

    private static final Random random = new Random();

    public static String toRainbow(String input) {
        ChatColor[] gradient = RAINBOW_VARIATIONS[random.nextInt(RAINBOW_VARIATIONS.length)];
        StringBuilder sb = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len; i++) {
            ChatColor color = gradient[i % gradient.length];
            sb.append(color).append(input.charAt(i));
        }
        return sb.toString();
    }
}
