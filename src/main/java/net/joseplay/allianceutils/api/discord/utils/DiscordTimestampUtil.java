package net.joseplay.allianceutils.api.discord.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for generating Discord-compatible timestamps.
 *
 * Supports both:
 * - ISO-8601 format (used in embeds)
 * - Discord formatted timestamps (<t:epoch:style>)
 */
public final class DiscordTimestampUtil {

    /**
     * Returns the current timestamp in ISO-8601 format.
     * Used for Discord embed timestamps.
     *
     * @return Current timestamp as ISO-8601 string
     */
    public static String now() {
        return Instant.now().toString();
    }

    /**
     * Converts an Instant into ISO-8601 format.
     * Useful for embed timestamp fields.
     *
     * @param instant Target instant
     * @return ISO-8601 formatted timestamp
     */
    public static String fromInstant(Instant instant) {
        return instant.toString();
    }

    /**
     * Generates an ISO-8601 timestamp with time offset.
     *
     * Example:
     * +5 minutes, -2 hours, etc.
     *
     * @param amount Amount to offset
     * @param unit   Time unit (e.g., MINUTES, HOURS)
     * @return Offset timestamp in ISO-8601 format
     */
    public static String withOffset(long amount, ChronoUnit unit) {
        return Instant.now().plus(amount, unit).toString();
    }

    /**
     * Formats a timestamp using Discord's special syntax.
     *
     * Format: <t:epoch:style>
     *
     * Example:
     * <t:1699999999:R> → "2 minutes ago"
     *
     * @param instant Target instant
     * @param style   Discord display style
     * @return Formatted Discord timestamp string
     */
    public static String text(Instant instant, DiscordTimeStyle style) {
        return "<t:" + instant.getEpochSecond() + ":" + style.getCode() + ">";
    }

    /**
     * Returns a relative timestamp for the current time.
     *
     * Example output:
     * "a few seconds ago"
     *
     * @return Discord relative timestamp string
     */
    public static String relativeNow() {
        return text(Instant.now(), DiscordTimeStyle.RELATIVE);
    }
}