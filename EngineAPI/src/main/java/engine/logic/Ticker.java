package engine.logic;

public class Ticker implements Runnable {

    public static final int LOGIC_TICK = 20;
    public static final int CLIENT_TICK = 20; // 暂时用常量

    protected final Tickable fixed;
    protected final Tickable.Partial dynamic;
    protected final int tickPerSecond;
    protected final double interval;

    protected boolean stopped = false;

    public Ticker(Tickable task, int tickPerSecond) {
        this.fixed = task;
        this.dynamic = partial -> {
            try {
                Thread.sleep((long) ((getInterval() - partial / tickPerSecond) * 1000));
            } catch (InterruptedException ignored) {
            }
        };
        this.tickPerSecond = tickPerSecond;
        this.interval = 1D / tickPerSecond;
    }

    public Ticker(Tickable task, Tickable.Partial dynamic, int tickPerSecond) {
        this.fixed = task;
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

    public double getInterval() {
        return interval;
    }

    /**
     * @return current time in second
     */
    protected double getCurrentTime() {
        return System.nanoTime() / 1e9D;
    }

    public void run() {
        double previous = getCurrentTime();
        double lag = 0D;
        while (!stopped) {
            double current = getCurrentTime();

            double elapsed = current - previous;
            previous = current;
            lag += elapsed;

            while (lag >= interval && !stopped) {
                fixed.tick();
                lag -= interval;
            }

            if (!stopped) {
                dynamic.tick((float) (lag * tickPerSecond));
            }
        }
    }
}
