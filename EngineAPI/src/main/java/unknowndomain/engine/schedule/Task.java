package unknowndomain.engine.schedule;

public abstract class Task {

    private boolean cancelled = false;

    public abstract void run();

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void stop() {

    }

    public void delay(long delay) {

    }

    public static final class Next {
        private long delay;

        private Next(long delay) {
            this.delay = delay;
        }

        public long getDelay() {
            return delay;
        }
    }
}
