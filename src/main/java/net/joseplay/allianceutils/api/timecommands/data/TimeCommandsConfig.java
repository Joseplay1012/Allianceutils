package net.joseplay.allianceutils.api.timecommands.data;

import net.joseplay.allianceutils.api.configuration.AbstractConfig;
import net.joseplay.allianceutils.api.timecommands.entity.TimeCommandTask;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeCommandsConfig extends AbstractConfig {

    private final Map<String, TimeCommandTask> tasks = new HashMap<>();

    public TimeCommandsConfig(File file) {
        super(file, "tasks", DummyKey.class);
        loadTasks();
    }

    private void loadTasks() {
        tasks.clear();

        ConfigurationSection section = config.getConfigurationSection("tasks");
        if (section == null) return;

        for (String id : section.getKeys(false)) {
            ConfigurationSection taskSec = section.getConfigurationSection(id);
            if (taskSec == null) continue;

            boolean useDay = taskSec.getBoolean("useday", false);
            String dayStr = taskSec.getString("day");
            String hor = taskSec.getString("hor");
            List<String> commands = taskSec.getStringList("commands");

            if (hor == null || commands.isEmpty()) continue;

            LocalTime time;
            try {
                time = LocalTime.parse(hor);
            } catch (Exception e) {
                continue;
            }

            DayOfWeek day = null;
            if (useDay && dayStr != null) {
                try {
                    day = DayOfWeek.valueOf(dayStr.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    continue;
                }
            }

            tasks.put(id, new TimeCommandTask(
                    id,
                    useDay,
                    day,
                    time,
                    commands
            ));
        }
    }

    public Collection<TimeCommandTask> getTasks() {
        return tasks.values();
    }

    /** dummy só pra satisfazer o AbstractConfig */
    private enum DummyKey implements ConfigKey {
        DUMMY;

        @Override public String getPath() { return ""; }
        @Override public ValueType getType() { return ValueType.STRING; }
        @Override public Object getDefaultValue() { return ""; }
    }

    @Override
    protected Object getDefaultValue(Enum<?> key) {
        return "";
    }

    @Override
    protected ConfigKey getConfigKey(Enum<?> key) {
        return (AbstractConfig.ConfigKey) key;
    }
}
