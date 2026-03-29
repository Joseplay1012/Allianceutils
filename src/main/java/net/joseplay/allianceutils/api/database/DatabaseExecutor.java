package net.joseplay.allianceutils.api.database;

import java.util.concurrent.*;

public final class DatabaseExecutor {

    private static final ExecutorService EXECUTOR =
            new ThreadPoolExecutor(
                    2, 2,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(100), // LIMITE
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );

    public static void submit(Runnable task) {
        EXECUTOR.submit(task);
    }

    public static void shutdownAndAwait() {
        EXECUTOR.shutdown();
        try {
            if (!EXECUTOR.awaitTermination(10, TimeUnit.SECONDS)) {
                EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

