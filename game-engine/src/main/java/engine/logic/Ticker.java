package engine.logic;

public final class Ticker implements Runnable {

    public static final int LOGIC_TICK = 20;
    public static final int CLIENT_TICK = 20; // 暂时用常量

    private final Tickable fixed;
    private final Tickable.Partial dynamic;
    private final int tickPerSecond;
    private final double interval;

    private Runnable onStopped;

    private boolean stopped = false;

    public Ticker(Tickable fixed, int tickPerSecond) {
        this.fixed = fixed;
        this.dynamic = partial -> {
            try {
                Thread.sleep((long) ((getInterval() - partial / tickPerSecond) * 1000));
            } catch (InterruptedException e) {
                stop();
            }
        };
        this.tickPerSecond = tickPerSecond;
        this.interval = 1D / tickPerSecond;
    }

    public Ticker(Tickable fixed, Tickable.Partial dynamic, int tickPerSecond) {
        this.fixed = fixed;
        this.dynamic = dynamic;
        this.tickPerSecond = tickPerSecond;
        this.interval = 1D / tickPerSecond;
    }

    public int getTickPerSecond() {
        return tickPerSecond;
    }

    public double getInterval() {
        return interval;
    }

    public Runnable getOnStopped() {
        return onStopped;
    }

    public void setOnStopped(Runnable onStopped) {
        this.onStopped = onStopped;
    }

    public void stop() {
        this.stopped = true;
    }

    public boolean isStopped() {
        return stopped;
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
        if (onStopped != null) onStopped.run();
    }

    /**
     * @return current time in second
     */
    private double getCurrentTime() {
        return System.nanoTime() / 1e9D;
    }
}
