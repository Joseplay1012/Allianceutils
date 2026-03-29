package net.joseplay.allianceutils.api.internalListener.listeners;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.internalListener.EventManager;
import net.joseplay.allianceutils.api.internalListener.events.PlayerItemAddEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerItemAddListener implements Listener {
    private final Map<UUID, ItemStack[]> lastInv = new ConcurrentHashMap<>();

    // Salva snapshot ao entrar
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        lastInv.put(e.getPlayer().getUniqueId(),
                cloneContents(e.getPlayer().getInventory().getContents()));
    }

    // Limpa ao sair
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        lastInv.remove(e.getPlayer().getUniqueId());
    }

    // Detecta mudanças após interações
    @EventHandler
    public void onInventoryChange(InventoryCloseEvent e) {
        check((Player) e.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Bukkit.getScheduler().runTask(Allianceutils.getPlugin(),
                () -> check((Player) e.getWhoClicked()));
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        Bukkit.getScheduler().runTask(Allianceutils.getPlugin(),
                () -> check((Player) e.getWhoClicked()));
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        Bukkit.getScheduler().runTask(Allianceutils.getPlugin(),
                () -> check((Player) e.getWhoClicked()));
    }

    // ----- Lógica de detecção -----

    private void check(Player p) {
        UUID id = p.getUniqueId();

        ItemStack[] oldInv = lastInv.get(id);
        ItemStack[] newInv = p.getInventory().getContents();

        if (oldInv == null) {
            lastInv.put(id, cloneContents(newInv));
            return;
        }

        for (int i = 0; i < newInv.length; i++) {
            ItemStack before = oldInv[i];
            ItemStack after = newInv[i];

            if (after != null) {
                if (before == null) {
                    // Novo item entrou
                    fireAddEvent(p, after.clone());
                } else if (after.getAmount() > before.getAmount()
                        && after.isSimilar(before)) {

                    // Item aumentou de quantidade
                    ItemStack difference = after.clone();
                    difference.setAmount(after.getAmount() - before.getAmount());

                    fireAddEvent(p, difference);
                }
            }
        }

        lastInv.put(id, cloneContents(newInv));
    }

    private void fireAddEvent(Player p, ItemStack item) {
        PlayerItemAddEvent event = new PlayerItemAddEvent(p, item);
        EventManager.callEvent(event);
    }

    private ItemStack[] cloneContents(ItemStack[] items) {
        ItemStack[] clone = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            clone[i] = (items[i] == null ? null : items[i].clone());
        }
        return clone;
    }
}
