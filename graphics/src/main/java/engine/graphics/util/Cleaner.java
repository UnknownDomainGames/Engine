package engine.graphics.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public final class Cleaner {
    private static final ReferenceQueue<Object> QUEUE = new ReferenceQueue<>();

    public static Cleanable register(Object obj, Runnable action) {
        return new CleanableImpl(obj, QUEUE, action);
    }

    public static void clean() {
        CleanableImpl cleanable;
        while ((cleanable = (CleanableImpl) QUEUE.poll()) != null) {
            cleanable.action.run();
        }
    }

    private Cleaner() {
    }

    public interface Cleanable {
        void clean();
    }

    private static final class CleanableImpl extends WeakReference<Object> implements Cleanable {
        private Runnable action;

        public CleanableImpl(Object referent, ReferenceQueue<? super Object> q, Runnable action) {
            super(referent, q);
            this.action = action;
        }

        @Override
        public synchronized void clean() {
            if (action == null) {
                throw new IllegalStateException("Already cleaned");
            }
            clear();
            action.run();
            action = null;
        }
    }
}
