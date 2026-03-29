package net.joseplay.allianceutils.api.playerProfile.data;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.playerProfile.entity.PlayerProfile;
import net.joseplay.allianceutils.api.playerProfile.entity.RateLimiter;
import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.PlayerProfileAsyncPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * Manages {@link PlayerProfile} lifecycle, including caching, synchronization,
 * persistence, and cross-server updates.
 *
 * <p><b>Core design principles:</b></p>
 * <ul>
 *     <li>Profiles are never exposed directly (only immutable snapshots)</li>
 *     <li>All mutations must go through {@link #modify(UUID, Consumer)}</li>
 *     <li>Thread-safety is enforced via per-profile locks</li>
 *     <li>Persistence is asynchronous and batched</li>
 * </ul>
 *
 * <p><b>Caching:</b></p>
 * <ul>
 *     <li>Backed by Caffeine {@link LoadingCache}</li>
 *     <li>Entries expire after inactivity</li>
 *     <li>Maximum size is bounded</li>
 * </ul>
 *
 * <p><b>Concurrency model:</b></p>
 * <ul>
 *     <li>Each profile has its own {@link ReentrantReadWriteLock}</li>
 *     <li>Reads use snapshots (no shared mutable state)</li>
 *     <li>Writes are exclusive and rate-limited</li>
 * </ul>
 *
 * <p><b>Data integrity:</b></p>
 * <ul>
 *     <li>Changes are detected via JSON diff</li>
 *     <li>Only modified profiles are persisted</li>
 *     <li>Dirty flag ensures retry on failure</li>
 * </ul>
 *
 * <p><b>Warning:</b></p>
 * <ul>
 *     <li>Snapshots are deep-copied via JSON (performance cost)</li>
 *     <li>No strong consistency across servers (eventual consistency)</li>
 * </ul>
 */
public class PlayerProfileManager {

    private static final String TABLE = "alcprofiles";

    private static final int GLOBAL_MODIFY_LIMIT = 120; // por segundo
    private static final Duration CACHE_EXPIRE = Duration.ofMinutes(45);
    private static final int CACHE_MAX_SIZE = 8000;

    private final LoadingCache<UUID, ProfileEntry> cache;
    private final RateLimiter globalRateLimiter = new RateLimiter();

    private final ExecutorService saveExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "Profile-Save-Worker");
        t.setDaemon(true);
        return t;
    });

    public PlayerProfileManager() {
        this.cache = Caffeine.newBuilder()
                .expireAfterAccess(CACHE_EXPIRE)
                .maximumSize(CACHE_MAX_SIZE)
                .build(this::loadFromDatabase);
    }

    // ─────────────────────────────────────────────────────────────
    // API pública
    // ─────────────────────────────────────────────────────────────

    /**
     * @deprecated Retorna apenas SNAPSHOT (somente leitura)
     */
    @Deprecated
    @NotNull
    public PlayerProfile getPlayer(@NotNull UUID uuid) {
        return getSnapshot(uuid);
    }

    /**
     * Returns an immutable snapshot of the player's profile.
     *
     * <p>The returned object is a deep copy and must not be used for mutations.</p>
     *
     * @param uuid player UUID
     * @return immutable snapshot
     */
    @NotNull
    public PlayerProfile getSnapshot(@NotNull UUID uuid) {
        return cache.get(uuid).createSnapshot();
    }


    /**
     * Applies a mutation to the player's profile in a thread-safe manner.
     *
     * <p><b>Execution flow:</b></p>
     * <ol>
     *     <li>Global rate limit check</li>
     *     <li>Acquire write lock</li>
     *     <li>Apply mutation</li>
     *     <li>Detect changes via JSON comparison</li>
     *     <li>Mark as dirty if changed</li>
     *     <li>Dispatch async update to other servers</li>
     *     <li>Schedule async persistence</li>
     * </ol>
     *
     * <p><b>Failure cases:</b></p>
     * <ul>
     *     <li>Rate limit exceeded → modification is ignored</li>
     *     <li>Lock not acquired → modification is skipped</li>
     * </ul>
     *
     * @param uuid player UUID
     * @param modifier mutation logic
     */
    public void modify(@NotNull UUID uuid, @NotNull Consumer<PlayerProfile> modifier) {

        if (!globalRateLimiter.tryAcquire(GLOBAL_MODIFY_LIMIT)) {
            warnRateLimit(uuid, "GLOBAL");
            return;
        }

        ProfileEntry entry = cache.get(uuid);

        if (!entry.tryWriteLock()) {
            warnRateLimit(uuid, "LOCK");
            return;
        }

        try {
            String beforeJson = entry.profile.toJSON();

            modifier.accept(entry.profile);

            String afterJson = entry.profile.toJSON();

            if (beforeJson.equals(afterJson)) {
                return; // nenhuma mudança real
            }

            entry.markDirty();

            // Atualiza snapshot remoto (outros servidores)
            Allianceutils.getInstance()
                    .getDispatcher()
                    .getPacketDispatcher()
                    .send(new PlayerProfileAsyncPacket(uuid, afterJson), false);

            scheduleSave(uuid, entry);

        } finally {
            entry.unlockWrite();
        }
    }

    /**
     * Flushes all dirty profiles and shuts down the persistence executor.
     *
     * <p><b>Warning:</b> Failure here may result in data loss.</p>
     */
    public void shutdown() {
        cache.asMap().forEach((uuid, entry) -> {
            if (entry.isDirty()) {
                try {
                    saveToDatabase(uuid, entry.getJson());
                } catch (Exception e) {
                    Allianceutils.getInstance().getLogger().severe(
                            "[Profile] PERDA POSSÍVEL de dados no shutdown: " + uuid);
                    e.printStackTrace();
                }
            }
        });

        saveExecutor.shutdown();
    }

    /**
     * Applies a remote update received from another server.
     *
     * <p>This overwrites the local profile state.</p>
     *
     * @param uuid player UUID
     * @param json serialized profile
     */
    public void applyRemoteUpdate(@NotNull UUID uuid, @NotNull String json) {
        ProfileEntry entry = cache.get(uuid);

        entry.withWriteLock(() -> {
            entry.profile = PlayerProfile.fromJSON(json);
            entry.clearDirty();
        });

        Allianceutils.getInstance().getLogger()
                .info("[PlayerProfileManager] Atualizando perfil remoto " + uuid);
    }

    // ─────────────────────────────────────────────────────────────
    // Internos
    // ─────────────────────────────────────────────────────────────

    private ProfileEntry loadFromDatabase(UUID uuid) {
        PlayerProfile profile = null;

        try (Connection conn = Allianceutils.getDataBaseManager().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT data FROM " + TABLE + " WHERE uuid = ?")) {

            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    profile = PlayerProfile.fromJSON(rs.getString("data"));
                }
            }

        } catch (SQLException e) {
            Allianceutils.getInstance().getLogger().severe(
                    "[Profile] Erro ao carregar perfil: " + uuid);
            e.printStackTrace();
        }

        if (profile == null) {
            profile = new PlayerProfile(uuid);
            profile.getFeatureManager().setDirty(true);
        }

        profile.ensureFeatures();
        return new ProfileEntry(profile);
    }

    private void scheduleSave(UUID uuid, ProfileEntry entry) {
        saveExecutor.execute(() -> {
            if (!entry.isDirty()) return;

            try {
                saveToDatabase(uuid, entry.getJson());
                entry.clearDirty();
            } catch (Exception e) {
                entry.markDirty();
                Allianceutils.getInstance().getLogger().warning(
                        "[Profile] Falha ao salvar perfil " + uuid + " (retry automático)");
                e.printStackTrace();
            }
        });
    }

    private void saveToDatabase(UUID uuid, String json) throws SQLException {
        try (Connection conn = Allianceutils.getDataBaseManager().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "REPLACE INTO " + TABLE + " (uuid, data) VALUES (?, ?)")) {

            ps.setString(1, uuid.toString());
            ps.setString(2, json);
            ps.executeUpdate();
        }
    }

    /**
     * Forces immediate persistence of a profile if present in cache.
     *
     * <p>This operation is synchronous and blocks the current thread.</p>
     *
     * @param uuid player UUID
     */
    public void saveNow(@NotNull UUID uuid) {
        ProfileEntry entry = cache.getIfPresent(uuid);
        if (entry == null) return;

        entry.withWriteLock(() -> {
            if (!entry.isDirty()) return;

            try {
                saveToDatabase(uuid, entry.getJson());
                entry.clearDirty();
            } catch (SQLException e) {
                Allianceutils.getInstance().getLogger().severe(
                        "[Profile] Falha ao salvar perfil no saveNow: " + uuid
                );
                e.printStackTrace();
            }
        });
    }

    private void warnRateLimit(UUID uuid, String type) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()) {
            p.sendMessage("§cAguarde um pouco… muitas alterações seguidas.");
        }

        Allianceutils.getInstance().getLogger().warning(
                "[Profile] Rate-limit (" + type + ") → " + uuid);
    }

    /**
     * Preloads a profile into cache (typically on player join).
     *
     * @param uuid player UUID
     */
    public void loadOnJoin(UUID uuid){
        cache.get(uuid);
    }


    /**
     * Saves and removes a profile from active usage (typically on player quit).
     *
     * @param uuid player UUID
     */
    public void unloadOnQuit(UUID uuid){
        saveNow(uuid);
    }


    /**
     * Internal wrapper for a {@link PlayerProfile} with concurrency control.
     *
     * <p><b>Responsibilities:</b></p>
     * <ul>
     *     <li>Holds the mutable profile instance</li>
     *     <li>Manages read/write locking</li>
     *     <li>Tracks dirty state for persistence</li>
     * </ul>
     *
     * <p><b>Thread-safety:</b></p>
     * <ul>
     *     <li>Read operations use read lock</li>
     *     <li>Write operations require write lock</li>
     * </ul>
     */
    private static final class ProfileEntry {

        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private final AtomicBoolean dirty = new AtomicBoolean(false);

        private PlayerProfile profile;

        ProfileEntry(PlayerProfile profile) {
            this.profile = profile;
        }

        PlayerProfile createSnapshot() {
            lock.readLock().lock();
            try {
                return PlayerProfile.fromJSON(profile.toJSON());
            } finally {
                lock.readLock().unlock();
            }
        }

        String getJson() {
            lock.readLock().lock();
            try {
                return profile.toJSON();
            } finally {
                lock.readLock().unlock();
            }
        }

        boolean tryWriteLock() {
            return lock.writeLock().tryLock();
        }

        void unlockWrite() {
            lock.writeLock().unlock();
        }

        void withWriteLock(Runnable action) {
            lock.writeLock().lock();
            try {
                action.run();
            } finally {
                lock.writeLock().unlock();
            }
        }

        void markDirty() {
            dirty.set(true);
        }

        void clearDirty() {
            dirty.set(false);
        }

        boolean isDirty() {
            return dirty.get();
        }
    }
}