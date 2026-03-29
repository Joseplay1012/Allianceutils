package net.joseplay.allianceutils.api.discord.webhook;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Discord webhook message payload.
 * Supports content, embeds, username override, and message flags.
 */
public class WebHookMessage {

    private String content;
    private String username;
    private String avatar_url;
    private Integer flags;

    /**
     * List of embeds attached to the message.
     */
    private final List<WebHookEmbed> embeds = new ArrayList<>();

    /* ---------- FLUENT SETTERS ---------- */

    /**
     * Sets the plain text content of the message.
     *
     * @param content Message content
     */
    public WebHookMessage setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * Overrides the webhook username.
     *
     * @param username Custom username
     */
    public WebHookMessage setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Overrides the webhook avatar.
     *
     * @param avatarUrl Avatar image URL
     */
    public WebHookMessage setAvatarUrl(String avatarUrl) {
        this.avatar_url = avatarUrl;
        return this;
    }

    /**
     * Adds an embed to the message.
     *
     * @param embed Embed instance
     */
    public WebHookMessage addEmbed(WebHookEmbed embed) {
        this.embeds.add(embed);
        return this;
    }

    /**
     * Suppresses notifications for this message.
     * Uses Discord flag 4096.
     */
    public WebHookMessage suppressNotifications() {
        this.flags = 4096;
        return this;
    }

    /* ---------- SERIALIZATION ---------- */

    /**
     * Converts this message into JSON format for webhook sending.
     *
     * @return JSON payload string
     */
    public String toJson() {
        return new Gson().toJson(this);
    }
}