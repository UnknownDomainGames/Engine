package nullengine.util;

import com.google.common.collect.Sets;
import nullengine.math.Math2;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

@ThreadSafe
public abstract class BufferPool {

    public static BufferPool createHeapBufferPool() {
        return new HeapBufferPool();
    }

    public static BufferPool createDirectBufferPool() {
        return new DirectBufferPool();
    }

    private final Set<ByteBuffer> buffers = Sets.newConcurrentHashSet();
    private final Queue<ByteBuffer> availableBuffers = new PriorityQueue<>(11, Comparator.comparingInt(o -> -o.capacity()));

    public ByteBuffer get(int capacity) {
        synchronized (availableBuffers) {
            if (!availableBuffers.isEmpty()) {
                for (ByteBuffer byteBuffer : availableBuffers) {
                    if (byteBuffer.capacity() >= capacity) {
                        availableBuffers.remove(byteBuffer);
                        return byteBuffer;
                    }
                }
            }
        }
        ByteBuffer byteBuffer = createBuffer(capacity);
        buffers.add(byteBuffer);
        return byteBuffer;
    }

    protected abstract ByteBuffer createBuffer(int capacity);

    public void free(ByteBuffer buffer) {
        if (!buffers.contains(buffer)) {
            throw new IllegalArgumentException("The buffer doesn't belong to this pool.");
        }
        buffer.clear();
        synchronized (availableBuffers) {
            availableBuffers.offer(buffer);
        }
    }

    public void clear() {
        availableBuffers.clear();
        buffers.clear();
    }

    private static class HeapBufferPool extends BufferPool {
        @Override
        protected ByteBuffer createBuffer(int capacity) {
            return ByteBuffer.allocate(Math.max(256, Math2.ceilPowerOfTwo(capacity)));
        }
    }

    private static class DirectBufferPool extends BufferPool {
        @Override
        protected ByteBuffer createBuffer(int capacity) {
            return ByteBuffer.allocateDirect(Math.max(256, Math2.ceilPowerOfTwo(capacity)));
        }
    }
}
