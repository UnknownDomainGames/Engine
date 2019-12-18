package nullengine.client.rendering.gl.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public final class GLCleaner {

    private static final ReferenceQueue<Object> queue = new ReferenceQueue<>();

    private static final PhantomDisposable list = new PhantomDisposable();

    public static Disposable register(Object obj, Runnable runnable) {
        return new PhantomDisposable(obj, queue, runnable);
    }

    public static Disposable registerTexture(Object obj, int textureId) {
        return register(obj, () -> glDeleteTextures(textureId));
    }

    public static Disposable registerBuffer(Object obj, int bufferId) {
        return register(obj, () -> glDeleteBuffers(bufferId));
    }

    public static Disposable registerFrameBuffer(Object obj, int bufferId) {
        return register(obj, () -> glDeleteFramebuffers(bufferId));
    }

    public static Disposable registerArray(Object obj, int bufferId) {
        return register(obj, () -> glDeleteVertexArrays(bufferId));
    }

    public static Disposable registerProgram(Object obj, int programId) {
        return register(obj, () -> glDeleteProgram(programId));
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

        private PhantomDisposable prev = this, next = this;

        PhantomDisposable() {
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
