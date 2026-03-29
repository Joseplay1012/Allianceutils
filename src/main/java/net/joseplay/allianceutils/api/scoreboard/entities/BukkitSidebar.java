package net.joseplay.allianceutils.api.scoreboard.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;

public class BukkitSidebar extends Sidebar {
    private String title;


    private final Scoreboard scoreboard;
    private Objective objective;
    private final Map<Integer, Team> teams = new HashMap<>();

    public BukkitSidebar(Player player, String title) {
        super(player);
        this.title = title;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        objective = scoreboard.registerNewObjective("sidebar", "dummy", title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(scoreboard);
    }

    @Override
    public void setTitle(String title) {
        objective.setDisplayName(title);
    }

    @Override
    public void setLine(int line, String text) {

        Team team = teams.get(line);

        if (team == null) {

            String entry = "§" + Integer.toHexString(line);

            team = scoreboard.registerNewTeam("line_" + line);
            team.addEntry(entry);

            objective.getScore(entry).setScore(line);

            teams.put(line, team);
        }

        if (text.length() <= 16) {
            team.setPrefix(text);
            team.setSuffix("");
        } else {
            team.setPrefix(text.substring(0, 16));
            team.setSuffix(text.substring(16, Math.min(text.length(), 32)));
        }
    }

    @Override
    public void remove() {

        for (Team team : teams.values()) {
            team.unregister();
        }

        objective.unregister();

        if (player == null) return;
        setDefaultSB();
    }

    private String getEntry(int line) {
        return "§" + Integer.toHexString(line);
    }

    @Override
    public void clear() {

        for (Team team : teams.values()) {
            team.unregister();
        }

        teams.clear();

        objective.unregister();

        objective = scoreboard.registerNewObjective("sidebar", "dummy", title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    public void removeLine(int line) {

    }
}
