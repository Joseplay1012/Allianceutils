package net.joseplay.allianceutils.api.discord.utils;

import java.awt.Color;

public final class DiscordColorUtil {

    private DiscordColorUtil() {}

    public static int from(Color color) {
        return color.getRGB() & 0xFFFFFF;
    }

    public static int from(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    public static int fromHex(String hex) {
        return Integer.parseInt(
                hex.replace("#", ""),
                16
        );
    }
}
