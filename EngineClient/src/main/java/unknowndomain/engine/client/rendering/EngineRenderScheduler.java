package unknowndomain.engine.client.rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EngineRenderScheduler implements RenderScheduler {

    private final BlockingQueue<Runnable> nextTasks = new LinkedBlockingQueue<>();
    private final BlockingQueue<Runnable> everyTasks = new LinkedBlockingQueue<>();

    private final List<Runnable> currentTasks = new ArrayList<>();

    @Override
    public void runTaskNextFrame(Runnable runnable) {
        nextTasks.add(runnable);
    }

    @Override
    public void runTaskEveryFrame(Runnable runnable) {
        everyTasks.add(runnable);
    }

    @Override
    public void cancelTask(Runnable runnable) {
        if (everyTasks.remove(runnable))
            return;

        nextTasks.remove(runnable);
    }

    public void run() {
        everyTasks.forEach(Runnable::run);
        nextTasks.drainTo(currentTasks);
        currentTasks.forEach(Runnable::run);
        currentTasks.clear();
    }
}
