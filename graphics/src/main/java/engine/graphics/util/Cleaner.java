package engine.graphics.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public final class Cleaner {

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

    private Cleaner() {
    }

    public interface Disposable {
        void dispose();
    }

    private static final class PhantomDisposable extends PhantomReference<Object> implements Disposable {

        private static final PhantomDisposable list = new PhantomDisposable();

        private final Runnable runnable;

        private PhantomDisposable prev = this, next = this;

        private PhantomDisposable() {
            super(null, null);
            runnable = null;
        }

        public PhantomDisposable(Object referent, ReferenceQueue<? super Object> q, Runnable runnable) {
            super(referent, q);
            this.runnable = runnable;
            insert();
        }

        @Override
        public void dispose() {
            if (remove()) {
                clear();
                runnable.run();
            }
        }

        private void insert() {
            prev = list;
            next = list.next;
            next.prev = this;
            list.next = this;
        }

        private boolean remove() {
            if (next == this) return false;

            next.prev = prev;
            prev.next = next;
            prev = this;
            next = this;
            return true;
        }
    }
}
