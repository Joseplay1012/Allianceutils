package net.joseplay.allianceutils.api.playerProfile.data;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.playerProfile.entity.ServerProfile;
import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.ServerProfileAsyncPacket;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Manages the lifecycle, synchronization, and persistence of a single {@link ServerProfile}.
 *
 * <p>Core rules:</p>
 * <ul>
 *     <li>The internal profile must only be modified via {@link #modifyProfile(ProfileModifier)}</li>
 *     <li>{@link #getSnapshot()} returns a safe copy (read-only usage)</li>
 *     <li>Thread-safety is enforced using a {@link ReentrantReadWriteLock}</li>
 *     <li>Persistence is asynchronous</li>
 * </ul>
 *
 * <p>This manager is designed for a single shared server-wide profile.</p>
 */
public class ServerProfileManager {

    private static final String TABLE = "alcserverprofile";

    /**
     * Read/write lock protecting access to the profile.
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * The live server profile instance.
     */
    private ServerProfile serverProfile = new ServerProfile();

    /**
     * Indicates whether the profile has been loaded from the database.
     */
    public boolean isLoadded = false;

    /**
     * Creates the manager and starts async loading from database.
     */
    public ServerProfileManager() {
        loadFromDataBaseAsync();
    }

    /* =========================
       ======== LOAD ===========
       ========================= */

    /**
     * Loads the profile asynchronously from the database.
     */
    public void loadFromDataBaseAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(
                Allianceutils.getInstance(),
                this::loadFromDataBase
        );
    }

    /**
     * Performs the actual database load.
     */
    private void loadFromDataBase() {
        ServerProfile loaded = null;

        try (Connection conn = Allianceutils.getDataBaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT data FROM " + TABLE + " WHERE id = 1")) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String data = rs.getString("data");
                    if (data != null && !data.isBlank()) {
                        loaded = ServerProfile.fromJSON(data);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        lock.writeLock().lock();
        try {
            if (loaded != null) {
                serverProfile = loaded;
                isLoadded = true;
            } else {
                serverProfile = new ServerProfile();
                serverProfile.getFeatureManager().setDirty(true);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /* =========================
       ======== ACCESS =========
       ========================= */

    /**
     * Returns a snapshot (safe copy) of the current profile.
     *
     * <p>The returned object must NOT be modified outside the manager.</p>
     *
     * @return cloned server profile
     */
    @NotNull
    public ServerProfile getSnapshot() {
        lock.readLock().lock();
        try {
            return serverProfile.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return serverProfile; // fallback (unsafe)
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * The ONLY valid entry point to modify the server profile.
     *
     * @param modifier logic to apply changes
     */
    public void modifyProfile(ProfileModifier modifier) {
        lock.writeLock().lock();
        try {
            modifier.modify(serverProfile);
            serverProfile.getFeatureManager().setDirty(true);
        } finally {
            lock.writeLock().unlock();
        }

        saveIfDirtyAsync();
    }

    /* =========================
       ======== SAVE ===========
       ========================= */

    /**
     * Schedules an async save if the profile is marked as dirty.
     */
    public void saveIfDirtyAsync() {
        String json;

        lock.readLock().lock();
        try {
            if (!serverProfile.getFeatureManager().isDirty()) {
                return;
            }
            json = serverProfile.toJSON();
        } finally {
            lock.readLock().unlock();
        }

        Bukkit.getScheduler().runTaskAsynchronously(
                Allianceutils.getPlugin(),
                () -> saveAndDispatch(json)
        );
    }

    /**
     * Saves the profile and dispatches it to other servers.
     */
    private void saveAndDispatch(String json) {
        boolean saved = saveToDataBase(json);

        if (!saved) return;

        Bukkit.getScheduler().runTask(
                Allianceutils.getPlugin(),
                () -> Allianceutils
                        .getInstance()
                        .getDispatcher()
                        .getPacketDispatcher()
                        .send(new ServerProfileAsyncPacket(ServerProfile.fromJSON(json), true), false)
        );
    }

    /**
     * Persists the profile to the database.
     *
     * @param json serialized profile
     * @return true if saved successfully
     */
    private boolean saveToDataBase(String json) {
        try (Connection conn = Allianceutils.getDataBaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "REPLACE INTO " + TABLE + " (id, data) VALUES (1, ?)")) {

            stmt.setString(1, json);
            stmt.executeUpdate();

            lock.writeLock().lock();
            try {
                serverProfile.getFeatureManager().setDirty(false);
            } finally {
                lock.writeLock().unlock();
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Forces a synchronous save (used on shutdown).
     */
    public void shutdown() {
        saveToDataBase(getSnapshot().toJSON());
    }

    /**
     * Applies a remote profile update (e.g., from another server).
     *
     * @param remoteProfile incoming profile
     */
    public void applyRemoteProfile(ServerProfile remoteProfile) {
        lock.writeLock().lock();
        try {
            this.serverProfile = remoteProfile;
            this.serverProfile.getFeatureManager().setDirty(false);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /* =========================
       ======== API ============
       ========================= */

    /**
     * Functional interface used to modify the profile safely.
     */
    @FunctionalInterface
    public interface ProfileModifier {

        /**
         * Applies changes to the given profile.
         *
         * @param profile mutable profile instance
         */
        void modify(ServerProfile profile);
    }
}