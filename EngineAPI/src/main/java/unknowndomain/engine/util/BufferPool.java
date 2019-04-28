package unknowndomain.engine.util;

import com.google.common.collect.Sets;
import unknowndomain.engine.math.Math2;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@ThreadSafe
public abstract class BufferPool {

    private static BufferPool DEFAULT_HEAP_BUFFER_POOL = createHeapBufferPool();

    public static BufferPool getDefaultHeapBufferPool() {
        return DEFAULT_HEAP_BUFFER_POOL;
    }

    public static BufferPool createHeapBufferPool() {
        return new HeapBufferPool();
    }

    public static BufferPool createDirectBufferPool() {
        return new DirectBufferPool();
    }

    private final Set<ByteBuffer> buffers = Sets.newConcurrentHashSet();
    private final BlockingQueue<ByteBuffer> availableBuffers = new PriorityBlockingQueue<>(11, Comparator.comparingInt(o -> -o.capacity()));

    public ByteBuffer get(int capacity) {
        if (!availableBuffers.isEmpty()) {
            for (ByteBuffer byteBuffer : availableBuffers) {
                if (byteBuffer.capacity() >= capacity) {
                    availableBuffers.remove(byteBuffer);
                    return byteBuffer;
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
        availableBuffers.offer(buffer);
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
