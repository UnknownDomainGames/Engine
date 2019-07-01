package nullengine.util.disposer;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public abstract class DisposerBase implements Disposer, Runnable {

    protected final ReferenceQueue<?> queue;

    public DisposerBase() {
        this.queue = new ReferenceQueue<>();
    }

    public DisposablePhantomReference register(Object obj, Runnable action) {
        return new DisposablePhantomReference(obj, queue, action);
    }

    public static class DisposablePhantomReference extends PhantomReference{

        private final Runnable action;

        private volatile boolean disposed = false;

        public DisposablePhantomReference(Object referent, ReferenceQueue q, Runnable action) {
            super(referent, q);
            this.action = action;
        }

        public void close() {
            disposed = true;
            action.run();
        }

        public boolean isClosed() {
            return disposed;
        }
    }
}
