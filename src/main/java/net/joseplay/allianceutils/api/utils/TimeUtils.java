package net.joseplay.allianceutils.api.utils;

public class TimeUtils {
    public static String getRemainingTime(long totalSeconds) {
        if (totalSeconds <= 0) {
            return "0s";
        }

        long days = totalSeconds / 86400L;
        long hours = totalSeconds % 86400L / 3600L;
        long minutes = totalSeconds % 3600L / 60L;
        long seconds = totalSeconds % 60L;

        if (days > 0) {
            return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
        } else if (hours > 0) {
            return hours + "h " + minutes + "m " + seconds + "s";
        } else if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return seconds + "s";
        }
    }
}
