package unknowndomain.engine.math;

import unknowndomain.engine.Tickable;

/**
 * http://gameprogrammingpatterns.com/game-loop.html
 */
public class FixStepTicker {
    protected final Tickable fix;
    protected final float interval;
    protected boolean stop = false;
    protected final int tps;

    public FixStepTicker(Tickable task, int tps) {
        fix = task;
        interval = 1F / tps;
        this.tps = tps;
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
                fix.tick();
                lag -= interval;
            }
        }
    }

    public static class Dynamic extends FixStepTicker {
        private final Tickable.Partial dynamic;

        public Dynamic(Tickable task, Tickable.Partial dyn, int tps) {
            super(task, tps);
            dynamic = dyn;
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
                    fix.tick();
                    lag -= interval;
                }
                dynamic.tick(lag / tps);
            }
        }
    }
}
