package net.joseplay.allianceutils.api.internalListener;

public interface CancellableEvent {
    boolean isCancelled();
    void setCancelled(boolean cancelled);
}
