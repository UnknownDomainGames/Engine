package unknowndomain.engine.math;

import unknowndomain.engine.Tickable;

/**
 * http://gameprogrammingpatterns.com/game-loop.html
 */
public class FixStepTicker {
    
    public static final int logicTick = 20;
    public static final int renderTick = 60;//暂时用常量
    
    protected final Tickable fix;
    protected final int interval;
    protected boolean stop = false;
    protected final int tps;
    protected double lag = 0.0;

    public FixStepTicker(Tickable task, int tps) {
        fix = task;
        interval = 1000000000 / tps;
        System.out.println(interval);
        this.tps = tps;
    }

    public void stop() {
        this.stop = true;
    }

    public boolean isStop() {
        return stop;
    }

    /**
     * @return current time in second
     */
    @Deprecated //TimeNanos in long is enough
    protected double getCurrentTime() {
        return System.nanoTime() / 1e9;
    }

    public void start() {
        long previous = System.nanoTime();
        long current = previous;
        long lag;
        while (!stop) {
            fix.tick();
            current = System.nanoTime();
            if((lag = previous - current + interval << 1) > 0 && current >= previous) {
                previous += interval;
                try {
                    Thread.sleep(lag / 1000000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                previous = current = System.nanoTime();
                try {
                    Thread.sleep((interval << 3) / 10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public double partialTick() {
        return lag / tps;
    }

    public static class RenderTicker extends FixStepTicker {
        private final Tickable.Partial dynamic;
        private static RenderTicker instance;
        public static volatile long currentTick;

        private RenderTicker(Tickable.Partial dyn, int tps) {
            super(null, tps);
            dynamic = dyn;
            currentTick = System.nanoTime();
        }

        public void start() {
            long previous = System.nanoTime();
            long current = previous;
            long lag;
            double lgk = logicTick / 1000000000d;
            while (!stop) {
                dynamic.tick((current - currentTick) * lgk);
                current = System.nanoTime();
                if((lag = previous - current + interval << 1) > 0 && current >= previous) {
                    previous += interval;
                    try {
                        Thread.sleep(lag / 1000000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    previous = current = System.nanoTime();
                    try {
                        Thread.sleep((interval << 3) / 10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        public synchronized static RenderTicker getInstance(Tickable.Partial dyn, int tps) {
            if(instance == null) {
                instance = new RenderTicker(dyn, tps);
            }
            return instance;
        }
    }
    public static class LogicTick extends FixStepTicker {
        private final Tickable.LogicTicker tickertask;

        public LogicTick(Tickable.LogicTicker task) {
            super(null, logicTick);
            tickertask = task;
        }

        public void start() {
            long previous = System.nanoTime();
            long current = previous;
            long lag;
            while (!stop) {
                tickertask.tick(current = System.nanoTime());
                if((lag = previous - current + interval << 1) > 0 && current >= previous) {
                    previous += interval;
                    try {
                        Thread.sleep(lag / 1000000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    previous = current = System.nanoTime();
                    try {
                        Thread.sleep((interval << 3) / 10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
