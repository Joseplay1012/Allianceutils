package net.joseplay.allianceutils.api.playerProfile.entity;

public final class RateLimiter {

    private long windowStart = System.nanoTime();
    private int count = 0;

    public synchronized boolean tryAcquire(int maxPerSecond) {
        long now = System.nanoTime();
        long elapsed = now - windowStart;

        if (elapsed >= 1_000_000_000L) {
            windowStart = now;
            count = 0;
        }

        if (count >= maxPerSecond) {
            return false;
        }

        count++;
        return true;
    }
}
