package nullengine.logic;

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
}
