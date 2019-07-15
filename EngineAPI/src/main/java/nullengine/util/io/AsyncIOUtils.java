package nullengine.util.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncIOUtils {

    private static final ExecutorService EXECUTOR;

    static {
        EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            private final AtomicInteger nextThreadId = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "AsyncIO-" + nextThreadId.getAndIncrement());
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            EXECUTOR.shutdownNow();
            while (true) {
                try {
                    if (EXECUTOR.awaitTermination(1, TimeUnit.SECONDS))
                        break;
                } catch (InterruptedException ignored) {
                }
            }
        }));
    }

    public static Executor getAsyncIOExecutor() {
        return EXECUTOR;
    }

    public static CompletableFuture<byte[]> readAllBytes(Path path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new AsyncIOException(e.getMessage(), e);
            }
        }, EXECUTOR);
    }
}
