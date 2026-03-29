package net.joseplay.allianceutils.api.discord.webhook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Handles sending messages to a Discord webhook asynchronously.
 */
public class DiscordWebhookSender {

    private final Plugin plugin;
    private final String webhookUrl;

    /**
     * @param plugin     Plugin instance (used for async scheduling)
     * @param webhookUrl Discord webhook URL
     */
    public DiscordWebhookSender(Plugin plugin, String webhookUrl) {
        this.plugin = plugin;
        this.webhookUrl = webhookUrl;
    }

    /**
     * Sends a webhook message asynchronously to avoid blocking the main thread.
     *
     * @param message WebHookMessage object containing JSON payload
     */
    public void send(WebHookMessage message) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String json = message.toJson();

                // Write JSON payload
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = connection.getResponseCode();

                // Discord usually returns 204 No Content on success
                if (responseCode != 204) {
                    Bukkit.getLogger().warning("[AllianceUtils] Webhook returned non-success response: " + responseCode);
                }

                connection.getInputStream().close();

            } catch (Exception e) {
                Bukkit.getLogger().warning("[AllianceUtils] Failed to send webhook: " + e.getMessage());
            }
        });
    }
}