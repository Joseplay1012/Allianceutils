package net.joseplay.allianceutils.api.discord.webhook;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Discord webhook embed object.
 * Uses a fluent builder-style API for easier construction.
 */
public class WebHookEmbed {

    private Author author;
    private String title;
    private String description;
    private int color;
    private Footer footer;
    private Thumbnail thumbnail;
    private Image image;
    private String timestamp;

    /**
     * List of embed fields.
     */
    private final List<Field> fields = new ArrayList<>();

    /* ---------- FLUENT SETTERS ---------- */

    /**
     * Sets the embed author.
     *
     * @param name     Author name
     * @param iconUrl  Author icon URL
     * @param url      Author link URL
     */
    public WebHookEmbed setAuthor(String name, String iconUrl, String url) {
        this.author = new Author(name, iconUrl, url);
        return this;
    }

    /**
     * Sets the embed title.
     */
    public WebHookEmbed setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the embed description.
     */
    public WebHookEmbed setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the embed color (decimal format, e.g., 0xFF0000).
     */
    public WebHookEmbed setColor(int color) {
        this.color = color;
        return this;
    }

    /**
     * Sets the embed footer.
     */
    public WebHookEmbed setFooter(String text, String iconUrl) {
        this.footer = new Footer(text, iconUrl);
        return this;
    }

    /**
     * Sets the embed thumbnail image.
     */
    public WebHookEmbed setThumbnail(String url) {
        this.thumbnail = new Thumbnail(url);
        return this;
    }

    /**
     * Sets the embed main image.
     */
    public WebHookEmbed setImage(String url) {
        this.image = new Image(url);
        return this;
    }

    /**
     * Sets the embed timestamp using ISO-8601 format.
     */
    public WebHookEmbed setTimestamp(Instant instant) {
        this.timestamp = instant.toString();
        return this;
    }

    /**
     * Adds a field to the embed.
     *
     * @param name   Field title
     * @param value  Field content
     * @param inline Whether the field is inline
     */
    public WebHookEmbed addField(String name, String value, boolean inline) {
        this.fields.add(new Field(name, value, inline));
        return this;
    }

    /* ---------- INTERNAL DATA STRUCTURES ---------- */

    /**
     * Represents embed author object.
     */
    private static class Author {
        private final String name;
        private final String icon_url;
        private final String url;

        private Author(String name, String iconUrl, String url) {
            this.name = name;
            this.icon_url = iconUrl;
            this.url = url;
        }
    }

    /**
     * Represents embed footer object.
     */
    private static class Footer {
        private final String text;
        private final String icon_url;

        private Footer(String text, String iconUrl) {
            this.text = text;
            this.icon_url = iconUrl;
        }
    }

    /**
     * Represents embed thumbnail object.
     */
    private static class Thumbnail {
        private final String url;

        private Thumbnail(String url) {
            this.url = url;
        }
    }

    /**
     * Represents embed image object.
     */
    private static class Image {
        private final String url;

        private Image(String url) {
            this.url = url;
        }
    }

    /**
     * Represents embed field object.
     */
    private static class Field {
        private final String name;
        private final String value;
        private final boolean inline;

        private Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }
    }
}