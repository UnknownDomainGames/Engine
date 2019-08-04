package nullengine.client.asset;

import nullengine.client.asset.reloading.AssetReloadScheduler;

import javax.annotation.Nonnull;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AssetReloadSchedulerImpl implements AssetReloadScheduler {

    private final ThreadPoolExecutor executor;

    public AssetReloadSchedulerImpl() {
        var threadAmount = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(threadAmount, threadAmount, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new ThreadFactory() {
            private final AtomicInteger nextThreadId = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "AssetLoadThread-" + nextThreadId.getAndIncrement());
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdownNow();
            try {
                while (true) {
                    if (executor.awaitTermination(1L, TimeUnit.MILLISECONDS)) break;
                }
            } catch (InterruptedException ignored) {
            }
        }));
    }

    @Override
    public void execute(@Nonnull Runnable command) {
        executor.submit(command);
    }

    @Override
    public void awaitCompletion() throws InterruptedException {
        while (executor.getActiveCount() != 0) {
            Thread.sleep(1L);
        }
    }
}
