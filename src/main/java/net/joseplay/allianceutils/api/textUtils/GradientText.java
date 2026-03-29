package net.joseplay.allianceutils.api.textUtils;

import java.awt.Color;
import java.util.*;

public class GradientText {

    // Cores do arco-íris (hex real)
    private static final Color[] COLORS = {
            new Color(255, 0, 0),     // Vermelho
            new Color(255, 165, 0),   // Laranja
            new Color(255, 255, 0),   // Amarelo
            new Color(0, 255, 0),     // Verde
            new Color(0, 255, 255),   // Ciano
            new Color(0, 0, 255),     // Azul
            new Color(128, 0, 128)    // Roxo
    };

    private static int offset = 0;
    private static final Map<String, String> cache = new HashMap<>();

    public static void nextFrame() {
        offset = (offset + 1) % 1000; // permite ciclos longos
        cache.clear();
    }

    public static String toGradient(String text) {
        if (text == null || text.isEmpty()) return text;

        String key = text + "#" + offset;
        return cache.computeIfAbsent(key, k -> generateGradient(text, offset));
    }

    private static String generateGradient(String text, int shift) {
        int totalSteps = text.length();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            float progress = ((float)(i + shift) / totalSteps) % 1.0f;

            Color color = getInterpolatedColor(progress);
            String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());

            sb.append("§x");
            for (char c : hex.substring(1).toCharArray()) {
                sb.append('§').append(c);
            }
            sb.append(text.charAt(i));
        }

        return sb.toString();
    }

    private static Color getInterpolatedColor(float progress) {
        float scaled = progress * COLORS.length;
        int index = (int) Math.floor(scaled) % COLORS.length;
        int nextIndex = (index + 1) % COLORS.length;

        float blend = scaled - index;

        Color c1 = COLORS[index];
        Color c2 = COLORS[nextIndex];

        int red   = (int) (c1.getRed()   + (c2.getRed()   - c1.getRed())   * blend);
        int green = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * blend);
        int blue  = (int) (c1.getBlue()  + (c2.getBlue()  - c1.getBlue())  * blend);

        return new Color(red, green, blue);
    }
}
