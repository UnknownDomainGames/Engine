package engine.world.gen;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ChunkGenExecutor {
    private static ExecutorService executor;
    private static boolean isAvailable;

    public static void start() {
        int threadCount = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
            private final AtomicInteger poolNumber = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Chunk Generator " + poolNumber.getAndIncrement());
            }
        });
        isAvailable = true;
    }

    public static <T> CompletableFuture<T> submitTask(Supplier<T> supplier) {
        if (!isAvailable) {
            return CompletableFuture.completedFuture(null);
        }
        try {
            return CompletableFuture.supplyAsync(supplier, executor);
        } catch (RejectedExecutionException e) {
            return CompletableFuture.completedFuture(null);
        }
    }

    public static void stop() {
        isAvailable = false;
        executor.shutdownNow();
    }

    /**
     * @return executor
     * @deprecated should submit task to ChunkGenExecutor instead of accessing inner executor
     */
    @Deprecated
    public static Executor getExecutor() {
        return executor;
    }

    //    public static void execute(Task task) {
//        executor.execute(task);
//    }
}
