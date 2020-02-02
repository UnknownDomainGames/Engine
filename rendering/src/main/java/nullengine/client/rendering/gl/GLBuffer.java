package nullengine.client.rendering.gl;

import nullengine.client.rendering.vertex.VertexFormat;
import nullengine.math.Math2;
import nullengine.util.Color;
import org.apache.commons.lang3.Validate;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

public abstract class GLBuffer {

    public static GLBuffer createDirectBuffer(int size) {
        return new GLDirectBuffer(size);
    }

    public static GLBuffer createHeapBuffer(int size) {
        return new GLHeapBuffer(size);
    }

    private ByteBuffer backingBuffer;

    private float translationX;
    private float translationY;
    private float translationZ;

    private boolean drawing;
    private VertexFormat vertexFormat;

    private int vertexCount;

    private int puttedByteCount;

    protected GLBuffer() {
        this(256);
    }

    protected GLBuffer(int initialCapacity) {
        backingBuffer = createBuffer(initialCapacity);
    }

    protected abstract ByteBuffer createBuffer(int capacity);

    protected abstract void freeBuffer(ByteBuffer buffer);

    public ByteBuffer getBackingBuffer() {
        return backingBuffer;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void begin(@Nonnull VertexFormat format) {
        if (drawing) {
            throw new IllegalStateException("Already drawing!");
        } else {
            drawing = true;
            reset();
            this.vertexFormat = Validate.notNull(format);
            backingBuffer.limit(backingBuffer.capacity());
        }
    }

    public void finish() {
        if (!drawing) {
            throw new IllegalStateException("Not yet drawn!");
        } else {
            drawing = false;
            backingBuffer.position(0);
            backingBuffer.limit(vertexCount * vertexFormat.getStride());
        }
    }

    public void reset() {
        vertexFormat = null;
        vertexCount = 0;
        setTranslation(0, 0, 0);
        backingBuffer.clear();
    }

    public void ensureRemaining(int minRemaining) {
        if (minRemaining > backingBuffer.remaining()) {
            int oldSize = this.backingBuffer.capacity();
            int newSize = computeNewCapacity(oldSize, minRemaining);
            int oldPosition = backingBuffer.position();
            ByteBuffer newBuffer = createBuffer(newSize);
            this.backingBuffer.position(0);
            newBuffer.put(this.backingBuffer);
            newBuffer.rewind();
            freeBuffer(backingBuffer);
            this.backingBuffer = newBuffer;
            newBuffer.position(oldPosition);
        }
    }

    protected abstract int computeNewCapacity(int oldCapacity, int minRemaining);

    public void dispose() {
        freeBuffer(backingBuffer);
        backingBuffer = null;
    }

    public boolean isDisposed() {
        return backingBuffer == null;
    }

    public void endVertex() {
        if (puttedByteCount != vertexFormat.getStride()) {
            throw new IllegalStateException("Mismatch vertex data.");
        }
        puttedByteCount = 0;
        vertexCount++;
        ensureRemaining(vertexFormat.getStride());
    }

    public GLBuffer put(byte value) {
        putByteCount(Byte.BYTES);
        backingBuffer.put(value);
        return this;
    }

    public GLBuffer put(int value) {
        putByteCount(Integer.BYTES);
        backingBuffer.putInt(value);
        return this;
    }

    public GLBuffer put(float value) {
        putByteCount(Float.BYTES);
        backingBuffer.putFloat(value);
        return this;
    }

    public GLBuffer put(double value) {
        putByteCount(Double.BYTES);
        backingBuffer.putDouble(value);
        return this;
    }

    public GLBuffer put(byte... bytes) {
        int bits = bytes.length * Byte.BYTES;
        if (puttedByteCount == 0) {
            if (bits % vertexFormat.getStride() != 0) {
                throw new IllegalArgumentException();
            }
            ensureRemaining(bits);
            backingBuffer.put(bytes);
            vertexCount += bits / vertexFormat.getStride();
        } else {
            putByteCount(bits);
            backingBuffer.put(bytes);
        }
        return this;
    }

    public GLBuffer put(int... ints) {
        int bits = ints.length * Integer.BYTES;
        if (puttedByteCount == 0) {
            if (bits % vertexFormat.getStride() != 0) {
                throw new IllegalArgumentException();
            }
            ensureRemaining(bits);
            for (int i = 0; i < ints.length; i++) {
                backingBuffer.putInt(ints[i]);
            }
            vertexCount += bits / vertexFormat.getStride();
        } else {
            putByteCount(bits);
            for (int i = 0; i < ints.length; i++) {
                backingBuffer.putInt(ints[i]);
            }
        }
        return this;
    }

    public GLBuffer put(float... floats) {
        int bits = floats.length * Float.BYTES;
        if (puttedByteCount == 0) {
            if (bits % vertexFormat.getStride() != 0) {
                throw new IllegalArgumentException();
            }
            ensureRemaining(bits);
            for (int i = 0; i < floats.length; i++) {
                backingBuffer.putFloat(floats[i]);
            }
            vertexCount += bits / vertexFormat.getStride();
        } else {
            putByteCount(bits);
            for (int i = 0; i < floats.length; i++) {
                backingBuffer.putFloat(floats[i]);
            }
        }
        return this;
    }

    public GLBuffer setTranslation(float x, float y, float z) {
        translationX = x;
        translationY = y;
        translationZ = z;
        return this;
    }

    public GLBuffer pos(float x, float y, float z) {
        if (vertexFormat.isUsingPosition()) {
            putByteCount(Float.BYTES * 3);
            backingBuffer.putFloat(x + translationX);
            backingBuffer.putFloat(y + translationY);
            backingBuffer.putFloat(z + translationZ);
        }
        return this;
    }

    public GLBuffer pos(Vector3fc vec) {
        return pos(vec.x(), vec.y(), vec.z());
    }

    public GLBuffer pos(float[] array, int start) {
        return pos(array[start], array[start + 1], array[start + 2]);
    }

    public GLBuffer color(Color color) {
        return rgba(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public GLBuffer color(int argb) {
        if (vertexFormat.isUsingColor()) {
            float a = ((argb >> 24) & 255) / 255f;
            float r = ((argb >> 16) & 255) / 255f;
            float g = ((argb >> 8) & 255) / 255f;
            float b = (argb & 255) / 255f;
            if (!vertexFormat.isUsingAlpha()) {
                return rgb(r, g, b);
            } else {
                return rgba(r, g, b, a);
            }
        }
        return this;
    }

    public GLBuffer rgb(float r, float g, float b) {
        if (vertexFormat.isUsingColor()) {
            if (vertexFormat.isUsingAlpha()) {
                return rgba(r, g, b, 1);
            }
            putByteCount(Float.BYTES * 3);
            backingBuffer.putFloat(r);
            backingBuffer.putFloat(g);
            backingBuffer.putFloat(b);
        }
        return this;
    }

    public GLBuffer rgba(float r, float g, float b, float a) {
        if (vertexFormat.isUsingColor()) {
            if (!vertexFormat.isUsingAlpha()) {
                return rgb(r, g, b);
            }
            putByteCount(Float.BYTES * 4);
            backingBuffer.putFloat(r);
            backingBuffer.putFloat(g);
            backingBuffer.putFloat(b);
            backingBuffer.putFloat(a);
        }
        return this;
    }

    public GLBuffer rgb(float[] array, int start) {
        return rgb(array[start], array[start + 1], array[start + 2]);
    }

    public GLBuffer rgba(float[] array, int start) {
        return rgba(array[start], array[start + 1], array[start + 2], array[start + 3]);
    }

    public GLBuffer tex(Vector2fc uv) {
        return tex(uv.x(), uv.y());
    }

    public GLBuffer tex(float u, float v) {
        if (vertexFormat.isUsingTexCoord()) {
            putByteCount(Float.BYTES * 2);
            backingBuffer.putFloat(u);
            backingBuffer.putFloat(v);
        }
        return this;
    }

    public GLBuffer tex(float[] array, int start) {
        return tex(array[start], array[start + 1]);
    }

    public GLBuffer normal(Vector3fc vec) {
        return normal(vec.x(), vec.y(), vec.z());
    }

    public GLBuffer normal(float nx, float ny, float nz) {
        if (vertexFormat.isUsingNormal()) {
            putByteCount(Float.BYTES * 3);
            backingBuffer.putFloat(nx);
            backingBuffer.putFloat(ny);
            backingBuffer.putFloat(nz);
        }
        return this;
    }

    public GLBuffer normal(float[] array, int start) {
        return normal(array[start], array[start + 1], array[start + 2]);
    }

    private void putByteCount(int count) {
        puttedByteCount += count;
    }

    public static class GLDirectBuffer extends GLBuffer {

        public GLDirectBuffer(int size) {
            super(size);
        }

        @Override
        protected ByteBuffer createBuffer(int capacity) {
            return MemoryUtil.memAlloc(capacity);
        }

        @Override
        protected void freeBuffer(ByteBuffer buffer) {
            MemoryUtil.memFree(buffer);
        }

        @Override
        protected int computeNewCapacity(int oldCapacity, int minRemaining) {
            return oldCapacity + Math2.ceil(minRemaining, 0x200000);
        }
    }

    public static class GLHeapBuffer extends GLBuffer {

        public GLHeapBuffer(int size) {
            super(size);
        }

        @Override
        protected ByteBuffer createBuffer(int capacity) {
            return ByteBuffer.allocate(capacity);
        }

        @Override
        protected void freeBuffer(ByteBuffer buffer) {
            // Don't need free heap buffer.
        }

        @Override
        protected int computeNewCapacity(int oldCapacity, int minRemaining) {
            return Math2.ceilPowerOfTwo(oldCapacity + minRemaining);
        }
    }
}
