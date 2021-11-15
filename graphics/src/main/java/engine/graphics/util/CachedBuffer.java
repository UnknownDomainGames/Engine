package engine.graphics.util;

import engine.math.Math2;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class CachedBuffer {
    private ByteBuffer buffer;

    public CachedBuffer(int initialCapacity) {
        this.buffer = MemoryUtil.memAlloc(initialCapacity);
    }

    public ByteBuffer get(int limit) {
        ensureCapacity(limit);
        return buffer.limit(limit).position(0);
    }

    public void ensureCapacity(int capacity) {
        if (capacity > buffer.capacity()) {
            int newCapacity = Math2.ceil(Math.max(capacity, buffer.capacity() << 1), 8);
            MemoryUtil.memFree(buffer);
            buffer = MemoryUtil.memAlloc(newCapacity);
        }
    }

    public boolean isDisposed() {
        return buffer == null;
    }

    public void dispose() {
        if (buffer == null) return;
        MemoryUtil.memFree(buffer);
        buffer = null;
    }
}
