package net.joseplay.allianceutils.api.chat;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Utility listener that allows capturing the next chat message from a player
 * and redirecting it to a custom handler.
 * <p>
 * Commonly used for chat-based input systems (e.g. menus, editors).
 * When a player is registered, their next message will:
 * <ul>
 *     <li>Be intercepted</li>
 *     <li>Not be sent to public chat</li>
 *     <li>Be delivered to the provided {@link Consumer}</li>
 * </ul>
 */
public class ChatEditor implements Listener {

    /**
     * Stores players waiting for chat input.
     * Key = player UUID
     * Value = handler to process the message
     */
    private static final Map<UUID, Consumer<String>> waiting = new ConcurrentHashMap<>();

    /**
     * Registers a player to wait for their next chat message.
     *
     * @param player   the player who will provide input
     * @param consumer the handler that will receive the message
     */
    public static void wait(Player player, Consumer<String> consumer) {
        waiting.put(player.getUniqueId(), consumer);
    }

    /**
     * Intercepts chat messages and redirects them if the player is registered.
     *
     * @param event the chat event
     */
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        UUID uuid = event.getPlayer().getUniqueId();

        if (!waiting.containsKey(uuid))
            return;

        event.setCancelled(true);

        Consumer<String> consumer = waiting.remove(uuid);

        // Ensure execution on the main thread (Bukkit API safety)
        Bukkit.getScheduler().runTask(
                Allianceutils.getPlugin(),
                () -> consumer.accept(event.getMessage())
        );
    }
}