package unknowndomain.engine.math;

import unknowndomain.engine.Tickable;

/**
 * http://gameprogrammingpatterns.com/game-loop.html
 */
public class FixStepTicker {
    private final Tickable runnable;
    private final float interval;
    private boolean stop = false;

    public FixStepTicker(Tickable task, int tps) {
        runnable = task;
        interval = 1F / tps;
    }

    public void stop() {
        this.stop = true;
    }

    public boolean isStop() {
        return stop;
    }

    protected double getCurrentTime() {
        return System.nanoTime() / 1_000_000_000.0;
    }

    public void start() {
        double previous = getCurrentTime();
        double lag = 0.0;
        while (!stop) {
            double current = getCurrentTime();
            double elapsed = current - previous;
            previous = current;
            lag += elapsed;

            while (lag >= interval) {
                runnable.tick();
                lag -= interval;
            }
        }
    }
}
