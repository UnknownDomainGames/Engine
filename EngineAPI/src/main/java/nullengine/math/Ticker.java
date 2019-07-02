package nullengine.math;

import nullengine.logic.Tickable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Ticker implements Runnable {

    public static final int CLIENT_TICK = 60; // 暂时用常量

    protected final Tickable fix;
    protected final Tickable.Partial dynamic;
    protected final int tickPerSecond;
    protected final double interval;

    protected boolean stopped = false;

    public Ticker(Tickable task, Tickable.Partial dynamic, int tickPerSecond) {
        fix = task;
        this.dynamic = dynamic;
        this.tickPerSecond = tickPerSecond;
        this.interval = 1D / tickPerSecond;
    }

    public void stop() {
        this.stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }

    /**
     * @return current time in second
     */
    protected double getCurrentTime() {
        return System.nanoTime() / 1e9D;
    }

    public void run() {
        double previous = getCurrentTime();
        double lag = 0.0;
        while (!stopped) {
            double current = getCurrentTime();

            double elapsed = current - previous;
            previous = current;
            lag += elapsed;

            while (lag >= interval && !stopped) {
                fix.tick();
                lag -= interval;
            }

            if (!stopped) {
                dynamic.tick((float) (lag / tickPerSecond));
            }
        }
    }

    //Considering Tickable interface would probably be removed so I used a threadpool to do this
    public static class DefaultThreadTicker{
        public static int TICK_LENGTH=50;
        private static int cpus= Runtime.getRuntime().availableProcessors();
        private static Executor executor= Executors.newScheduledThreadPool(cpus, Executors.defaultThreadFactory());
        public static void executeTick(Runnable runnable){
            ((ScheduledThreadPoolExecutor)executor).scheduleAtFixedRate(runnable, 0, TICK_LENGTH, TimeUnit.MILLISECONDS);
        }
    }
}
