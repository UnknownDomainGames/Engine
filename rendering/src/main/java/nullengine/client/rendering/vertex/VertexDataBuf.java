package nullengine.client.rendering.vertex;

import nullengine.math.Math2;
import nullengine.util.Color;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nonnull;
import java.nio.*;

import static org.apache.commons.lang3.Validate.notNull;

public abstract class VertexDataBuf {

    protected ByteBuffer buffer;

    protected float translationX;
    protected float translationY;
    protected float translationZ;

    private VertexFormat vertexFormat;

    private boolean ready;

    public static VertexDataBuf create(int initialCapacity) {
        return new Direct(initialCapacity);
    }

    protected VertexDataBuf() {
        this(4096);
    }

    protected VertexDataBuf(int initialCapacity) {
        buffer = createBuffer(initialCapacity);
    }

    protected abstract ByteBuffer createBuffer(int capacity);

    protected abstract void freeBuffer(ByteBuffer buffer);

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public boolean isReady() {
        return ready;
    }

    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public int getVertexCount() {
        return buffer.capacity() / vertexFormat.getBytes();
    }

    public void begin(@Nonnull VertexFormat format) {
        if (!ready) {
            throw new IllegalStateException("Buffer not ready");
        }
        if (isDisposed()) {
            throw new IllegalStateException("Buffer has been disposed");
        }
        vertexFormat = notNull(format);
        buffer.clear();
        setTranslation(0, 0, 0);
        ensureRemaining(format.getBytes());
        ready = false;
    }

    public void finish() {
        if (ready) {
            throw new IllegalStateException("Buffer ready");
        }
        buffer.flip();
        validateVertexData();
        ready = true;
    }

    protected void validateVertexData() {
        if (buffer.limit() % vertexFormat.getBytes() != 0) {
            throw new IllegalStateException("Invalid vertex data");
        }
    }

    public void ensureCapacity(int minCapacity) {
        if (minCapacity > buffer.capacity()) {
            int newCapacity = buffer.capacity() << 1;
            grow(Math2.ceil(Math.max(newCapacity, minCapacity), 4096));
        }
    }

    public void ensureRemaining(int minRemaining) {
        if (minRemaining > buffer.remaining()) {
            int newCapacity = buffer.capacity() << 1;
            grow(Math2.ceil(Math.max(newCapacity, minRemaining + buffer.capacity()), 4096));
        }
    }

    protected void grow(int newCapacity) {
        ByteBuffer newBuffer = createBuffer(newCapacity).put(buffer.flip());
        freeBuffer(buffer);
        buffer = newBuffer;
    }

    public void dispose() {
        freeBuffer(buffer);
        buffer = null;
    }

    public boolean isDisposed() {
        return buffer == null;
    }

    public VertexDataBuf put(byte value) {
        buffer.put(value);
        return this;
    }

    public VertexDataBuf put(short value) {
        buffer.putShort(value);
        return this;
    }

    public VertexDataBuf put(int value) {
        buffer.putInt(value);
        return this;
    }

    public VertexDataBuf put(float value) {
        buffer.putFloat(value);
        return this;
    }

    public VertexDataBuf put(double value) {
        buffer.putDouble(value);
        return this;
    }

    public VertexDataBuf put(byte[] src) {
        return put(src, 0, src.length);
    }

    public VertexDataBuf put(short[] src) {
        return put(src, 0, src.length);
    }

    public VertexDataBuf put(int[] src) {
        return put(src, 0, src.length);
    }

    public VertexDataBuf put(float[] src) {
        return put(src, 0, src.length);
    }

    public VertexDataBuf put(double[] src) {
        return put(src, 0, src.length);
    }

    public VertexDataBuf put(byte[] src, int offset, int length) {
        ensureRemaining(length);
        buffer.put(src, offset, length);
        return this;
    }

    public VertexDataBuf put(short[] src, int offset, int length) {
        ensureRemaining(length * Short.BYTES);
        buffer.asShortBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuf put(int[] src, int offset, int length) {
        ensureRemaining(length * Integer.BYTES);
        buffer.asIntBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuf put(float[] src, int offset, int length) {
        ensureRemaining(length * Float.BYTES);
        buffer.asFloatBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuf put(double[] src, int offset, int length) {
        ensureRemaining(length * Double.BYTES);
        buffer.asDoubleBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuf put(ByteBuffer src) {
        ensureRemaining(src.limit());
        buffer.put(src);
        return this;
    }

    public VertexDataBuf put(ShortBuffer src) {
        ensureRemaining(src.limit() * Short.BYTES);
        buffer.asShortBuffer().put(src);
        return this;
    }

    public VertexDataBuf put(IntBuffer src) {
        ensureRemaining(src.limit() * Integer.BYTES);
        buffer.asIntBuffer().put(src);
        return this;
    }

    public VertexDataBuf put(FloatBuffer src) {
        ensureRemaining(src.limit() * Float.BYTES);
        buffer.asFloatBuffer().put(src);
        return this;
    }

    public VertexDataBuf put(DoubleBuffer src) {
        ensureRemaining(src.limit() * Double.BYTES);
        buffer.asDoubleBuffer().put(src);
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
            buffer.putFloat(x + translationX);
            buffer.putFloat(y + translationY);
            buffer.putFloat(z + translationZ);
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
            buffer.putFloat(r);
            buffer.putFloat(g);
            buffer.putFloat(b);
        }
        return this;
    }

    public VertexDataBuf rgba(float r, float g, float b, float a) {
        if (vertexFormat.isUsingColor()) {
            if (!vertexFormat.isUsingAlpha()) {
                return rgb(r, g, b);
            }
            buffer.putFloat(r);
            buffer.putFloat(g);
            buffer.putFloat(b);
            buffer.putFloat(a);
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
            buffer.putFloat(u);
            buffer.putFloat(v);
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
            buffer.putFloat(nx);
            buffer.putFloat(ny);
            buffer.putFloat(nz);
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
            buffer.putFloat(tx);
            buffer.putFloat(ty);
            buffer.putFloat(tz);
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
            buffer.putFloat(bx);
            buffer.putFloat(by);
            buffer.putFloat(bz);
        }
        return this;
    }

    public VertexDataBuf bitangent(float[] array, int start) {
        return bitangent(array[start], array[start + 1], array[start + 2]);
    }

    public VertexDataBuf endVertex() {
        validateVertexData();
        ensureRemaining(vertexFormat.getBytes());
        return this;
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
