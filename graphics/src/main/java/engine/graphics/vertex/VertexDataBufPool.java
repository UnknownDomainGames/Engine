package engine.graphics.vertex;

import com.google.common.collect.Sets;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

@ThreadSafe
public abstract class VertexDataBufPool {

    private final Set<VertexDataBuf> buffers = Sets.newConcurrentHashSet();
    private final Queue<VertexDataBuf> availableBuffers = new PriorityQueue<>(11, Comparator.comparingInt(o -> -o.getByteBuffer().capacity()));

    private final int bufferInitialCapacity;
    private final int poolCapacity;

    public static VertexDataBufPool create(int bufferInitialCapacity, int poolCapacity) {
        return new DirectBufferPool(bufferInitialCapacity, poolCapacity);
    }

    public static VertexDataBufPool create() {
        return create(4096, Integer.MAX_VALUE);
    }

    protected VertexDataBufPool(int bufferInitialCapacity, int poolCapacity) {
        this.bufferInitialCapacity = bufferInitialCapacity;
        this.poolCapacity = poolCapacity;
    }

    public VertexDataBuf get() throws InterruptedException {
        return get(bufferInitialCapacity);
    }

    public VertexDataBuf get(int capacity) throws InterruptedException {
        while (true) {
            synchronized (availableBuffers) {
                VertexDataBuf buffer = availableBuffers.poll();
                if (buffer != null) {
                    return buffer;
                }

                if (buffers.size() < poolCapacity) {
                    buffer = createBuffer(capacity);
                    buffers.add(buffer);
                    return buffer;
                }

                availableBuffers.wait();
            }
        }
    }

    protected abstract VertexDataBuf createBuffer(int initialCapacity);

    public void free(VertexDataBuf buffer) {
        if (!buffers.contains(buffer)) {
            throw new IllegalArgumentException("The buffer doesn't belong to this pool.");
        }
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

    private static class DirectBufferPool extends VertexDataBufPool {

        private DirectBufferPool(int bufferInitialCapacity, int poolCapacity) {
            super(bufferInitialCapacity, poolCapacity);
        }

        @Override
        protected VertexDataBuf createBuffer(int initialCapacity) {
            return VertexDataBuf.create(initialCapacity);
        }
    }

}
