package engine.world.gen;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ChunkGenExecutor {
    private static ExecutorService executor;

    public static void start() {
        int threadCount = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
            private final AtomicInteger poolNumber = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Chunk Generator " + poolNumber.getAndIncrement());
            }
        });
    }

    public static void stop() {
        executor.shutdown();
    }

    public static Executor getExecutor() {
        return executor;
    }

    //    public static void execute(Task task) {
//        executor.execute(task);
//    }
}
