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
            previous += interval;
            current = System.nanoTime();
            if((lag = previous - current + interval) > 0) {
            	try {
					Thread.sleep(lag / 1000000);
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

    public static class Dynamic extends FixStepTicker {
        private final Tickable.Partial dynamic;

        public Dynamic(Tickable task, Tickable.Partial dyn, int tps) {
            super(task, tps);
            dynamic = dyn;
        }

        public void start() {
            long previous = System.nanoTime();
            long current = previous;
            long lag;
            double lgk = logicTick / 1000000000d;
            while (!stop) {
            	dynamic.tick((current - LogicTick.currentTick) * lgk);
                previous += interval;
                current = System.nanoTime();
                if((lag = previous - current + interval) > 0) {
                	try {
    					Thread.sleep(lag / 1000000);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
                }
            }
        }
    }
    public static class LogicTick extends FixStepTicker {
    	public static long currentTick;
    	private static LogicTick instance;

        private LogicTick(Tickable task) {
            super(task, logicTick);
        }

        public void start() {
            long previous = System.nanoTime();
            long current = currentTick = previous;
            long lag;
            while (!stop) {
                fix.tick();
                previous += interval;
                currentTick = current = System.nanoTime();
                if((lag = previous - current + interval) > 0) {
                	try {
						Thread.sleep(lag / 1000000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        }
        public synchronized static LogicTick getInstance(Tickable task) {
        	if(instance == null) {
        		instance = new LogicTick(task);
        	}
			return instance;
        }
    }
}
