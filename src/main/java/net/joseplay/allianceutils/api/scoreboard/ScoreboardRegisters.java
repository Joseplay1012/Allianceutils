package net.joseplay.allianceutils.api.scoreboard;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.internalListener.EventManager;
import net.joseplay.allianceutils.api.scoreboard.listeners.ScoreboardListeners;

public class ScoreboardRegisters {

    public static void registerEvents(){
        ScoreboardListeners scoreboardListeners = new ScoreboardListeners();

        Allianceutils.getInstance().getServer().getPluginManager().registerEvents(scoreboardListeners, Allianceutils.getInstance());
        EventManager.registerListener(scoreboardListeners, Allianceutils.getInstance());
    }

}
