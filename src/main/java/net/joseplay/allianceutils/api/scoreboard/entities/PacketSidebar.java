package net.joseplay.allianceutils.api.scoreboard.entities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PacketSidebar extends Sidebar {
    private String title;
    private final String objectiveName = "sb_" + System.nanoTime();
    private final Map<Integer, String> lines = new HashMap<>();

    public PacketSidebar(Player player, String title) {
        super(player);
        this.title = title;
        createObjective(title);
    }

    private void createObjective(String title) {
        WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective(objectiveName, WrapperPlayServerScoreboardObjective.ObjectiveMode.CREATE, Component.text(title), WrapperPlayServerScoreboardObjective.RenderType.INTEGER);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        WrapperPlayServerDisplayScoreboard display = new WrapperPlayServerDisplayScoreboard(1, objectiveName);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, display);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;

        WrapperPlayServerScoreboardObjective packet =
                new WrapperPlayServerScoreboardObjective(
                        objectiveName,
                        WrapperPlayServerScoreboardObjective.ObjectiveMode.UPDATE,
                        Component.text(title),
                        WrapperPlayServerScoreboardObjective.RenderType.INTEGER
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    @Override
    public void setLine(int line, String text) {
        lines.put(line, text);
        WrapperPlayServerUpdateScore score = new WrapperPlayServerUpdateScore(text, WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM, objectiveName, Optional.of(line));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, score);
    }

    @Override
    public void remove() {
        if (player == null) return;

        WrapperPlayServerScoreboardObjective remove = new WrapperPlayServerScoreboardObjective(objectiveName, WrapperPlayServerScoreboardObjective.ObjectiveMode.REMOVE, null, null);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, remove);
        setDefaultSB();

    }

    @Override
    public void clear() {

        WrapperPlayServerScoreboardObjective remove =
                new WrapperPlayServerScoreboardObjective(
                        objectiveName,
                        WrapperPlayServerScoreboardObjective.ObjectiveMode.REMOVE,
                        null,
                        null
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, remove);

        createObjective(title);

        lines.clear();
    }

    @Override
    public void removeLine(int line) {
    }
}