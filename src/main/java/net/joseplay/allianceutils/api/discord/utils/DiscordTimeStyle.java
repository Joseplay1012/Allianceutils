package net.joseplay.allianceutils.api.discord.utils;

public enum DiscordTimeStyle {

    SHORT_TIME("t"),
    LONG_TIME("T"),
    SHORT_DATE("d"),
    LONG_DATE("D"),
    SHORT_DATE_TIME("f"),
    LONG_DATE_TIME("F"),
    RELATIVE("R");

    private final String code;

    DiscordTimeStyle(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}