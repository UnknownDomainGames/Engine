package nullengine.client.rendering.vertex;

import nullengine.math.Math2;
import nullengine.util.Color;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.apache.commons.lang3.Validate.notNull;

public abstract class VertexDataBuf {

    public static VertexDataBuf create(int initialCapacity) {
        return new Direct(initialCapacity);
    }

    private VertexFormat vertexFormat;

    private ByteBuffer backingBuffer;

    private float translationX;
    private float translationY;
    private float translationZ;

    private boolean ready;

    protected VertexDataBuf() {
        this(4096);
    }

    protected VertexDataBuf(int initialCapacity) {
        backingBuffer = createBuffer(initialCapacity);
    }

    protected abstract ByteBuffer createBuffer(int capacity);

    protected abstract void freeBuffer(ByteBuffer buffer);

    public ByteBuffer getBackingBuffer() {
        return backingBuffer;
    }

    public boolean isReady() {
        return ready;
    }

    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public int getVertexCount() {
        return backingBuffer.capacity() / vertexFormat.getStride();
    }

    public void begin(@Nonnull VertexFormat format) {
        if (!ready) {
            throw new IllegalStateException("Buffer not ready");
        }
        ready = false;
        vertexFormat = notNull(format);
        setTranslation(0, 0, 0);
        backingBuffer.clear();
    }

    public void finish() {
        if (ready) {
            throw new IllegalStateException("Buffer ready");
        }
        validateVertexData();
        ready = true;
        backingBuffer.flip();
    }

    protected void validateVertexData() {
        if (backingBuffer.capacity() % vertexFormat.getStride() != 0) {
            throw new IllegalStateException("Invalid vertex data");
        }
    }

    public void ensureCapacity(int minCapacity) {
        if (minCapacity > backingBuffer.capacity()) {
            int newCapacity = backingBuffer.capacity() << 1;
            grow(Math2.ceil(Math.max(newCapacity, minCapacity), 4096));
        }
    }

    public void ensureRemaining(int minRemaining) {
        if (minRemaining > backingBuffer.remaining()) {
            int newCapacity = backingBuffer.capacity() << 1;
            grow(Math2.ceil(Math.max(newCapacity, minRemaining + backingBuffer.capacity()), 4096));
        }
    }

    protected void grow(int newCapacity) {
        ByteBuffer newBuffer = createBuffer(newCapacity).put(backingBuffer.flip());
        freeBuffer(backingBuffer);
        backingBuffer = newBuffer;
    }

    public void dispose() {
        freeBuffer(backingBuffer);
        backingBuffer = null;
    }

    public boolean isDisposed() {
        return backingBuffer == null;
    }

    public void endVertex() {
        validateVertexData();
        ensureRemaining(vertexFormat.getStride());
    }

    public VertexDataBuf put(float value) {
        backingBuffer.putFloat(value);
        return this;
    }

    public VertexDataBuf put(float[] floats) {
        int bits = floats.length * Float.BYTES;
        ensureRemaining(bits);
        backingBuffer.asFloatBuffer().put(floats);
        return this;
    }

    public VertexDataBuf put(FloatBuffer buffer) {
        ensureRemaining(buffer.limit());
        backingBuffer.asFloatBuffer().put(buffer);
        return this;
    }

    public VertexDataBuf setTranslation(float x, float y, float z) {
        translationX = x;
        translationY = y;
        translationZ = z;
        return this;
    }

    public VertexDataBuf pos(float x, float y, float z) {
        if (vertexFormat.isUsingPosition()) {
            backingBuffer.putFloat(x + translationX);
            backingBuffer.putFloat(y + translationY);
            backingBuffer.putFloat(z + translationZ);
        }
        return this;
    }

    public VertexDataBuf pos(Vector3fc vec) {
        return pos(vec.x(), vec.y(), vec.z());
    }

    public VertexDataBuf pos(float[] array, int start) {
        return pos(array[start], array[start + 1], array[start + 2]);
    }

    public VertexDataBuf color(Color color) {
        return rgba(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public VertexDataBuf color(int argb) {
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

    public VertexDataBuf rgb(float r, float g, float b) {
        if (vertexFormat.isUsingColor()) {
            if (vertexFormat.isUsingAlpha()) {
                return rgba(r, g, b, 1);
            }
            backingBuffer.putFloat(r);
            backingBuffer.putFloat(g);
            backingBuffer.putFloat(b);
        }
        return this;
    }

    public VertexDataBuf rgba(float r, float g, float b, float a) {
        if (vertexFormat.isUsingColor()) {
            if (!vertexFormat.isUsingAlpha()) {
                return rgb(r, g, b);
            }
            backingBuffer.putFloat(r);
            backingBuffer.putFloat(g);
            backingBuffer.putFloat(b);
            backingBuffer.putFloat(a);
        }
        return this;
    }

    public VertexDataBuf rgb(float[] array, int start) {
        return rgb(array[start], array[start + 1], array[start + 2]);
    }

    public VertexDataBuf rgba(float[] array, int start) {
        return rgba(array[start], array[start + 1], array[start + 2], array[start + 3]);
    }

    public VertexDataBuf tex(Vector2fc uv) {
        return tex(uv.x(), uv.y());
    }

    public VertexDataBuf tex(float u, float v) {
        if (vertexFormat.isUsingTexCoord()) {
            backingBuffer.putFloat(u);
            backingBuffer.putFloat(v);
        }
        return this;
    }

    public VertexDataBuf tex(float[] array, int start) {
        return tex(array[start], array[start + 1]);
    }

    public VertexDataBuf normal(Vector3fc vec) {
        return normal(vec.x(), vec.y(), vec.z());
    }

    public VertexDataBuf normal(float nx, float ny, float nz) {
        if (vertexFormat.isUsingNormal()) {
            backingBuffer.putFloat(nx);
            backingBuffer.putFloat(ny);
            backingBuffer.putFloat(nz);
        }
        return this;
    }

    public VertexDataBuf normal(float[] array, int start) {
        return normal(array[start], array[start + 1], array[start + 2]);
    }

    public VertexDataBuf tangent(Vector3fc vec) {
        return tangent(vec.x(), vec.y(), vec.z());
    }

    public VertexDataBuf tangent(float tx, float ty, float tz) {
        if (vertexFormat.isUsingTangent()) {
            backingBuffer.putFloat(tx);
            backingBuffer.putFloat(ty);
            backingBuffer.putFloat(tz);
        }
        return this;
    }

    public VertexDataBuf tangent(float[] array, int start) {
        return tangent(array[start], array[start + 1], array[start + 2]);
    }

    public VertexDataBuf bitangent(Vector3fc vec) {
        return bitangent(vec.x(), vec.y(), vec.z());
    }

    public VertexDataBuf bitangent(float bx, float by, float bz) {
        if (vertexFormat.isUsingBitangent()) {
            backingBuffer.putFloat(bx);
            backingBuffer.putFloat(by);
            backingBuffer.putFloat(bz);
        }
        return this;
    }

    public VertexDataBuf bitangent(float[] array, int start) {
        return bitangent(array[start], array[start + 1], array[start + 2]);
    }

    public static class Direct extends VertexDataBuf {

        public Direct(int size) {
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
    }
}
