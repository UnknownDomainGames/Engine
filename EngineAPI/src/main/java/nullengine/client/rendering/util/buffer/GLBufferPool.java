package nullengine.client.rendering.util.buffer;

import com.google.common.collect.Sets;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

@ThreadSafe
public abstract class GLBufferPool {

    public static GLBufferPool createHeapBufferPool() {
        return new HeapBufferPool();
    }

    public static GLBufferPool createDirectBufferPool() {
        return new DirectBufferPool();
    }

    private final Set<GLBuffer> buffers = Sets.newConcurrentHashSet();
    private final Queue<GLBuffer> availableBuffers = new PriorityQueue<>(11, Comparator.comparingInt(o -> -o.getBackingBuffer().capacity()));

    public GLBuffer get() {
        return get(256);
    }

    public GLBuffer get(int capacity) {
        synchronized (availableBuffers) {
            if (!availableBuffers.isEmpty()) {
                for (GLBuffer buffer : availableBuffers) {
                    if (buffer.getBackingBuffer().capacity() >= capacity) {
                        availableBuffers.remove(buffer);
                        return buffer;
                    }
                }
            }
        }
        GLBuffer buffer = createBuffer(capacity);
        buffers.add(buffer);
        return buffer;
    }

    protected abstract GLBuffer createBuffer(int capacity);

    public void free(GLBuffer buffer) {
        if (!buffers.contains(buffer)) {
            throw new IllegalArgumentException("The buffer doesn't belong to this pool.");
        }
        buffer.reset();
        synchronized (availableBuffers) {
            availableBuffers.offer(buffer);
        }
    }

    public void clear() {
        synchronized (availableBuffers) {
            availableBuffers.clear();
        }
        buffers.clear();
    }

    private static class HeapBufferPool extends GLBufferPool {
        @Override
        protected GLBuffer createBuffer(int capacity) {
            return GLBuffer.createHeapBuffer(capacity);
        }
    }

    private static class DirectBufferPool extends GLBufferPool {
        @Override
        protected GLBuffer createBuffer(int capacity) {
            return GLBuffer.createDirectBuffer(capacity);
        }
    }

}
