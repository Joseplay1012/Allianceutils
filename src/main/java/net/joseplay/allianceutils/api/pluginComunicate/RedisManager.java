package net.joseplay.allianceutils.api.pluginComunicate;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.json.JSONObject;

public class RedisManager {

    private final RedisAsyncCommands<String, String> async;
    private final RedisCommands<String, String> sync;

    public RedisManager(
            RedisAsyncCommands<String, String> async,
            RedisCommands<String, String> sync
    ) {
        this.async = async;
        this.sync = sync;
    }

    /* ===== SYNC (uso pontual) ===== */

    public void set(String key, String value) {
        sync.set(key, value);
    }

    public String get(String key) {
        return sync.get(key);
    }

    public void sendMessage(String channel, String message) {
        sync.publish(channel, message);
    }

    public void sendMessage(String channel, JSONObject message) {
        sync.publish(channel, message.toString());
    }

    /* ===== ASYNC (recomendado) ===== */

    public void setAsync(String key, String value) {
        async.set(key, value);
    }

    public void sendMessageAsync(String channel, String message) {
        async.publish(channel, message);
    }
}