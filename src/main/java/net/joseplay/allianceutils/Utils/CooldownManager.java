package net.joseplay.allianceutils.Utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    // Mapa para armazenar cooldowns
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    /**
     * Verifica se o jogador está em cooldown.
     *
     * @param player O jogador a ser verificado.
     * @return true se o jogador ainda estiver em cooldown, false caso contrário.
     */
    public boolean isOnCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerId)) {
            long cooldownEnd = cooldowns.get(playerId);
            return currentTime < cooldownEnd; // Ainda em cooldown?
        }
        return false;
    }

    /**
     * Adiciona o jogador ao cooldown.
     *
     * @param player O jogador que será adicionado ao cooldown.
     * @param cooldownTime Tempo em segundos para o cooldown.
     */
    public void addCooldown(Player player, int cooldownTime) {
        UUID playerId = player.getUniqueId();
        long cooldownEnd = System.currentTimeMillis() + (cooldownTime * 1000L);
        cooldowns.put(playerId, cooldownEnd);
    }

    /**
     * Remove o jogador do cooldown.
     *
     * @param player O jogador que será removido do cooldown.
     */
    public void removeCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        cooldowns.remove(playerId);
    }

    /**
     * Obtém o tempo restante de cooldown.
     *
     * @param player O jogador a ser verificado.
     * @return Tempo restante em segundos ou 0 se não houver cooldown ativo.
     */
    public int getRemainingCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerId)) {
            long cooldownEnd = cooldowns.get(playerId);
            if (currentTime < cooldownEnd) {
                return (int) ((cooldownEnd - currentTime) / 1000); // Retorna em segundos
            }
        }
        return 0;
    }
}