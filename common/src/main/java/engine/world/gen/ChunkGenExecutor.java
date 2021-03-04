package engine.world.gen;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ChunkGenExecutor {
    private static ThreadPoolExecutor executor;

    public static void start() {
        int threadCount = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(threadCount, threadCount,
                0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>(), new ThreadFactory() {
            private final AtomicInteger poolNumber = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Chunk Generator " + poolNumber.getAndIncrement());
            }
        });
    }

    public static void stop() {
        executor.shutdownNow();
    }

//    public static void execute(Task task) {
//        executor.execute(task);
//    }
}
