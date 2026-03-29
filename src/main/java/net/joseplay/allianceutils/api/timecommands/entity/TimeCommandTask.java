package net.joseplay.allianceutils.api.timecommands.entity;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public final class TimeCommandTask {

    private final String id;
    private final boolean useDay;
    private final DayOfWeek day; // null se useDay = false
    private final LocalTime time;
    private final List<String> commands;

    public TimeCommandTask(
            String id,
            boolean useDay,
            DayOfWeek day,
            LocalTime time,
            List<String> commands
    ) {
        this.id = id;
        this.useDay = useDay;
        this.day = day;
        this.time = time;
        this.commands = List.copyOf(commands);
    }

    public String getId() {
        return id;
    }

    public boolean matches(LocalDateTime now) {
        if (!now.toLocalTime().withSecond(0).withNano(0).equals(time)) return false;
        if (useDay && now.getDayOfWeek() != day) return false;
        return true;
    }

    public List<String> getCommands() {
        return commands;
    }
}
