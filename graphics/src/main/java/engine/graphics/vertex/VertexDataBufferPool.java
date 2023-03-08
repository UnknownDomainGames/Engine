package engine.graphics.vertex;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

@ThreadSafe
public final class VertexDataBufferPool {
    private final Queue<VertexDataBuffer> queue;
    private final int bufferInitialCapacity;
    private final int poolCapacity;
    private int poolObjectCount;

    public static VertexDataBufferPool create(int bufferInitialCapacity, int poolCapacity) {
        return new VertexDataBufferPool(bufferInitialCapacity, poolCapacity);
    }

    public static VertexDataBufferPool create() {
        return new VertexDataBufferPool(4096, Integer.MAX_VALUE);
    }

    private VertexDataBufferPool(int bufferInitialCapacity, int poolCapacity) {
        this.queue = new PriorityQueue<>(11, Comparator.comparingInt(o -> -o.getByteBuffer().capacity()));
        this.bufferInitialCapacity = bufferInitialCapacity;
        this.poolCapacity = poolCapacity;
    }

    public VertexDataBuffer get() throws InterruptedException {
        return get(bufferInitialCapacity);
    }

    public VertexDataBuffer get(int capacity) throws InterruptedException {
        synchronized (queue) {
            while (true) {
                VertexDataBuffer buffer = queue.poll();
                if (buffer != null) {
                    return buffer;
                }

                if (poolObjectCount < poolCapacity) {
                    buffer = createBuffer(capacity);
                    poolObjectCount++;
                    return buffer;
                }

                queue.wait();
            }
        }
    }

    public void free(VertexDataBuffer buffer) {
        synchronized (queue) {
            queue.offer(buffer);
            queue.notify();
        }
    }

    private VertexDataBuffer createBuffer(int initialCapacity) {
        return VertexDataBuffer.create(initialCapacity);
    }
}
