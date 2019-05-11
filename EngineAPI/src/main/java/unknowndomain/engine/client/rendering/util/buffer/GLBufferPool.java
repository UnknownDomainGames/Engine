package unknowndomain.engine.client.rendering.util.buffer;

import com.google.common.collect.Sets;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

@NotThreadSafe
public abstract class GLBufferPool {
    private static GLBufferPool DEFAULT_HEAP_BUFFER_POOL = createHeapBufferPool();

    public static GLBufferPool getDefaultHeapBufferPool() {
        return DEFAULT_HEAP_BUFFER_POOL;
    }

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
        if (!availableBuffers.isEmpty()) {
            for (GLBuffer byteBuffer : availableBuffers) {
                if (byteBuffer.getBackingBuffer().capacity() >= capacity) {
                    availableBuffers.remove(byteBuffer);
                    return byteBuffer;
                }
            }
        }
        GLBuffer byteBuffer = createBuffer(capacity);
        buffers.add(byteBuffer);
        return byteBuffer;
    }

    protected abstract GLBuffer createBuffer(int capacity);

    public void free(GLBuffer buffer) {
        if (!buffers.contains(buffer)) {
            throw new IllegalArgumentException("The buffer doesn't belong to this pool.");
        }
        buffer.reset();
        availableBuffers.offer(buffer);
    }

    public void clear() {
        availableBuffers.clear();
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
