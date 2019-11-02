package nullengine.client.rendering.gl;

import nullengine.client.rendering.math.Math2;
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

    private float posOffsetX;
    private float posOffsetY;
    private float posOffsetZ;

    private boolean drawing;
    private GLDrawMode drawMode;
    private GLVertexFormat vertexFormat;

    private int vertexCount;

    private int puttedByteCount;

    protected GLBuffer() {
        this(256);
    }

    protected GLBuffer(int size) {
        backingBuffer = createBuffer(size);
    }

    protected abstract ByteBuffer createBuffer(int capacity);

    protected abstract void freeBuffer(ByteBuffer buffer);

    public ByteBuffer getBackingBuffer() {
        return backingBuffer;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public GLDrawMode getDrawMode() {
        return drawMode;
    }

    public GLVertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void begin(@Nonnull GLDrawMode mode, @Nonnull GLVertexFormat format) {
        if (drawing) {
            throw new IllegalStateException("Already drawing!");
        } else {
            drawing = true;
            reset();
            drawMode = Validate.notNull(mode);
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
        drawMode = null;
        vertexFormat = null;
        vertexCount = 0;
        posOffset(0, 0, 0);
        backingBuffer.clear();
    }

    public void grow(int needLength) {
        if (needLength > backingBuffer.remaining()) {
            int oldSize = this.backingBuffer.capacity();
            int newSize = computeNewSize(oldSize, needLength);
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

    protected abstract int computeNewSize(int oldSize, int needLength);

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
        grow(vertexFormat.getStride());
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
            grow(bits);
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
            grow(bits);
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
            grow(bits);
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

    public GLBuffer posOffset(float x, float y, float z) {
        posOffsetX = x;
        posOffsetY = y;
        posOffsetZ = z;
        return this;
    }

    public GLBuffer pos(float x, float y, float z) {
        if (vertexFormat.isUsingPosition()) {
            putByteCount(Float.BYTES * 3);
            backingBuffer.putFloat(x + posOffsetX);
            backingBuffer.putFloat(y + posOffsetY);
            backingBuffer.putFloat(z + posOffsetZ);
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
        return color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public GLBuffer color(int color) {
        if (vertexFormat.isUsingColor()) {
            float a = ((color >> 24) & 255) / 255f;
            float r = ((color >> 16) & 255) / 255f;
            float g = ((color >> 8) & 255) / 255f;
            float b = (color & 255) / 255f;
            if (vertexFormat.getColorElement().getSize() == 3) {
                return color(r, g, b);
            }
            if (vertexFormat.getColorElement().getSize() == 4) {
                if (a == 0F) {
                    return color(r, g, b, 1);
                } else {
                    return color(r, g, b, a);
                }
            }
        }
        return this;
    }

    public GLBuffer color(float r, float g, float b) {
        if (vertexFormat.isUsingColor()) {
            if (vertexFormat.getColorElement().getSize() == 4) {
                return color(r, g, b, 1);
            }
            putByteCount(Float.BYTES * 3);
            backingBuffer.putFloat(r);
            backingBuffer.putFloat(g);
            backingBuffer.putFloat(b);
        }
        return this;
    }

    public GLBuffer color(float r, float g, float b, float a) {
        if (vertexFormat.isUsingColor()) {
            if (vertexFormat.getColorElement().getSize() == 3) {
                return color(r, g, b);
            }
            putByteCount(Float.BYTES * 4);
            backingBuffer.putFloat(r);
            backingBuffer.putFloat(g);
            backingBuffer.putFloat(b);
            backingBuffer.putFloat(a);
        }
        return this;
    }

    public GLBuffer colorRGB(float[] array, int start) {
        return color(array[start], array[start + 1], array[start + 2]);
    }

    public GLBuffer colorRGBA(float[] array, int start) {
        return color(array[start], array[start + 1], array[start + 2], array[start + 3]);
    }

    public GLBuffer uv(Vector2fc uv) {
        return uv(uv.x(), uv.y());
    }

    public GLBuffer uv(float u, float v) {
        if (vertexFormat.isUsingTextureUV()) {
            putByteCount(Float.BYTES * 2);
            backingBuffer.putFloat(u);
            backingBuffer.putFloat(v);
        }
        return this;
    }

    public GLBuffer uv(float[] array, int start) {
        return uv(array[start], array[start + 1]);
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
        protected int computeNewSize(int oldSize, int needLength) {
            return oldSize + Math2.ceil(needLength, 0x200000);
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
        protected int computeNewSize(int oldSize, int needLength) {
            return Math2.ceilPowerOfTwo(oldSize + needLength);
        }
    }
}
