package net.joseplay.allianceutils.api.combat.handlers;

import org.bukkit.entity.Player;

public interface CombatHandler {
    String getName();

    boolean isValidAPI();
    boolean isCombat(Player player);
    boolean addCombat(Player damaged, Player damager);
    boolean removeCombat(Player player);
}
