package nullengine.util.io;

import nullengine.util.function.SupplierWithException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

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

    public static CompletableFuture<byte[]> readAllBytes(InputStream input) {
        return CompletableFuture.supplyAsync(() -> {
            try (input) {
                return IOUtils.toByteArray(input);
            } catch (IOException e) {
                throw new AsyncIOException(e.getMessage(), e);
            }
        }, EXECUTOR);
    }

    public static <T> CompletableFuture<T> supply(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, EXECUTOR);
    }

    public static <T> CompletableFuture<T> supply(SupplierWithException<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new AsyncIOException(e.getMessage(), e);
            }
        }, EXECUTOR);
    }
}
