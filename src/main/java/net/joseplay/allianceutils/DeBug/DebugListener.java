package net.joseplay.allianceutils.DeBug;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DebugListener implements Listener {

    private final JavaPlugin plugin;

    public DebugListener(JavaPlugin plugin) {
        this.plugin = plugin;
        if(!plugin.getConfig().getBoolean("dbug-mode")){
            Bukkit.getConsoleSender().sendMessage("§aDeBug Ativado.");
        } else {
            Bukkit.getConsoleSender().sendMessage("§cDeBug Desativado.");
            return;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " entrou no servidor.");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " saiu do servidor.");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " interagiu com " + event.getClickedBlock().getType());
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " teleportou-se para " + event.getTo().getBlockX() + ", " + event.getTo().getBlockY() + ", " + event.getTo().getBlockZ());
            }
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " usou o comando: " + event.getMessage());
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " abriu o inventário: " + event.getInventory().getType());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " fechou o inventário: " + event.getInventory().getType());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " clicou no inventário: " + event.getInventory().getType());
                if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                    player.sendMessage("[§CDEBUG§F] Item clicado: " + event.getCurrentItem().getType());
                }
            }
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " quebrou um bloco de " + event.getBlock().getType());
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " colocou um bloco de " + event.getBlock().getType() + " em " + event.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " dropou um item de " + event.getItemDrop().getItemStack().getType());
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " pegou um item de " + event.getItem().getItemStack().getType());
            }
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " " + (event.isFlying() ? "ativou" : "desativou") + " o voo.");
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " interagiu com a entidade: " + event.getRightClicked().getType());
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " interagiu diretamente com a entidade: " + event.getRightClicked().getType());
            }
        }
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " entrou na cama.");
            }
        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " saiu da cama.");
            }
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " consumiu um item de " + event.getItem().getType());
            }
        }
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " danificou um item de " + event.getItem().getType());
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " trocou para o slot " + event.getNewSlot());
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " foi expulso do servidor.");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("dbug-mode")) {
            if (player.isOp()) {
                player.sendMessage("[§CDEBUG§F] O jogador " + player.getName() + " renasceu em " + event.getRespawnLocation());
            }
        }
    }
}
