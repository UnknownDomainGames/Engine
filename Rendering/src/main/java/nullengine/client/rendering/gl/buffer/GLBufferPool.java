package nullengine.client.rendering.gl.buffer;

import com.google.common.collect.Sets;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

@ThreadSafe
public abstract class GLBufferPool {

    private final Set<GLBuffer> buffers = Sets.newConcurrentHashSet();
    private final Queue<GLBuffer> availableBuffers = new PriorityQueue<>(11, Comparator.comparingInt(o -> -o.getBackingBuffer().capacity()));

    private final int capacity;
    private final int limit;

    public static GLBufferPool createHeapBufferPool(int capacity, int limit) {
        return new HeapBufferPool(capacity, limit);
    }

    public static GLBufferPool createHeapBufferPool() {
        return createHeapBufferPool(256, Integer.MAX_VALUE);
    }

    public static GLBufferPool createDirectBufferPool(int capacity, int limit) {
        return new DirectBufferPool(capacity, limit);
    }

    public static GLBufferPool createDirectBufferPool() {
        return createDirectBufferPool(256, Integer.MAX_VALUE);
    }

    protected GLBufferPool(int capacity, int limit) {
        this.capacity = capacity;
        this.limit = limit;
    }

    public GLBuffer get() throws InterruptedException {
        return get(capacity);
    }

    public GLBuffer get(int capacity) throws InterruptedException {
        while (true) {
            synchronized (availableBuffers) {
                GLBuffer buffer = availableBuffers.poll();
                if (buffer != null) {
                    return buffer;
                }

                if (buffers.size() < limit) {
                    buffer = createBuffer(capacity);
                    buffers.add(buffer);
                    return buffer;
                }

                availableBuffers.wait();
            }
        }
    }

    protected abstract GLBuffer createBuffer(int capacity);

    public void free(GLBuffer buffer) {
        if (!buffers.contains(buffer)) {
            throw new IllegalArgumentException("The buffer doesn't belong to this pool.");
        }
        buffer.reset();
        synchronized (availableBuffers) {
            availableBuffers.offer(buffer);
            availableBuffers.notify();
        }
    }

    public void clear() {
        synchronized (availableBuffers) {
            availableBuffers.clear();
            buffers.clear();
        }
    }

    private static class HeapBufferPool extends GLBufferPool {
        private HeapBufferPool(int capacity, int limit) {
            super(capacity, limit);
        }

        @Override
        protected GLBuffer createBuffer(int capacity) {
            return GLBuffer.createHeapBuffer(capacity);
        }
    }

    private static class DirectBufferPool extends GLBufferPool {
        private DirectBufferPool(int capacity, int limit) {
            super(capacity, limit);
        }

        @Override
        protected GLBuffer createBuffer(int capacity) {
            return GLBuffer.createDirectBuffer(capacity);
        }
    }

}
