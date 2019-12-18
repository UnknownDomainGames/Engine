package nullengine.client.rendering.gl.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public final class GLCleaner {

    private static final ReferenceQueue<Object> queue = new ReferenceQueue<>();

    public static Disposable register(Object obj, Runnable runnable) {
        return new PhantomDisposable(obj, queue, runnable);
    }

    public static void clean() {
        Disposable ref;
        while ((ref = (Disposable) queue.poll()) != null) {
            ref.dispose();
        }
    }

    private GLCleaner() {
    }

    public interface Disposable {
        void dispose();
    }

    private static final class PhantomDisposable extends PhantomReference<Object> implements Disposable {

        private final Runnable runnable;

        public PhantomDisposable(Object referent, ReferenceQueue<? super Object> q, Runnable runnable) {
            super(referent, q);
            this.runnable = runnable;
        }

        @Override
        public void dispose() {
            clear();
            runnable.run();
        }
    }
}
