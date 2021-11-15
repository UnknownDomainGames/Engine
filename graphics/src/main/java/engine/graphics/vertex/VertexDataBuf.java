package engine.graphics.vertex;

import engine.math.Math2;
import engine.util.Color;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nonnull;
import java.nio.*;

import static org.apache.commons.lang3.Validate.notNull;

public abstract class VertexDataBuf {

    private static final ThreadLocal<VertexDataBuf> threadLocalBuffer = ThreadLocal.withInitial(() -> create(4096));

    protected ByteBuffer byteBuffer;

    protected float transX;
    protected float transY;
    protected float transZ;

    private VertexFormat vertexFormat;

    private boolean ready = true;
    private int vertexCount = 0;

    public static VertexDataBuf create(int initialCapacity) {
        return new Direct(initialCapacity);
    }

    @Deprecated
    public static VertexDataBuf currentThreadBuffer() {
        return threadLocalBuffer.get();
    }

    protected VertexDataBuf() {
        this(4096);
    }

    protected VertexDataBuf(int initialCapacity) {
        byteBuffer = createBuffer(initialCapacity);
    }

    protected abstract ByteBuffer createBuffer(int capacity);

    protected abstract void freeBuffer(ByteBuffer buffer);

    public ByteBuffer getByteBuffer() {
        if (!ready) {
            throw new IllegalStateException("Buffer not ready");
        }
        return byteBuffer;
    }

    public boolean isReady() {
        return ready;
    }

    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void begin(@Nonnull VertexFormat format) {
        if (byteBuffer == null) {
            throw new IllegalStateException("Buffer is disposed");
        }
        if (!ready) {
            throw new IllegalStateException("Buffer not ready");
        }
        vertexFormat = notNull(format);
        byteBuffer.clear();
        ensureRemaining(format.getBytes());
        ready = false;
    }

    public void finish() {
        if (ready) {
            throw new IllegalStateException("Buffer ready");
        }
        byteBuffer.flip();
        if (byteBuffer.limit() % vertexFormat.getBytes() != 0) {
            throw new IllegalStateException("Invalid vertex data");
        }
        vertexCount = byteBuffer.limit() / vertexFormat.getBytes();
        ready = true;
    }

    public void ensureCapacity(int minCapacity) {
        if (minCapacity > byteBuffer.capacity()) {
            int newCapacity = byteBuffer.capacity() << 1;
            grow(Math2.ceil(Math.max(newCapacity, minCapacity), 4096));
        }
    }

    public void ensureRemaining(int minRemaining) {
        if (minRemaining > byteBuffer.remaining()) {
            int newCapacity = byteBuffer.capacity() << 1;
            grow(Math2.ceil(Math.max(newCapacity, minRemaining + byteBuffer.capacity()), 4096));
        }
    }

    protected void grow(int newCapacity) {
        ByteBuffer newBuffer = createBuffer(newCapacity).put(byteBuffer.flip());
        freeBuffer(byteBuffer);
        byteBuffer = newBuffer;
    }

    public void dispose() {
        if (byteBuffer == null) {
            return;
        }
        freeBuffer(byteBuffer);
        byteBuffer = null;
    }

    public boolean isDisposed() {
        return byteBuffer == null;
    }

    public VertexDataBuf put(byte value) {
        byteBuffer.put(value);
        return this;
    }

    public VertexDataBuf put(short value) {
        byteBuffer.putShort(value);
        return this;
    }

    public VertexDataBuf put(int value) {
        byteBuffer.putInt(value);
        return this;
    }

    public VertexDataBuf put(float value) {
        byteBuffer.putFloat(value);
        return this;
    }

    public VertexDataBuf put(double value) {
        byteBuffer.putDouble(value);
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
        byteBuffer.put(src, offset, length);
        return this;
    }

    public VertexDataBuf put(short[] src, int offset, int length) {
        ensureRemaining(length << 1);
        byteBuffer.asShortBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuf put(int[] src, int offset, int length) {
        ensureRemaining(length << 2);
        byteBuffer.asIntBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuf put(float[] src, int offset, int length) {
        ensureRemaining(length << 2);
        byteBuffer.asFloatBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuf put(double[] src, int offset, int length) {
        ensureRemaining(length << 3);
        byteBuffer.asDoubleBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuf put(ByteBuffer src) {
        ensureRemaining(src.remaining());
        byteBuffer.put(src);
        return this;
    }

    public VertexDataBuf put(ShortBuffer src) {
        ensureRemaining(src.remaining() << 1);
        byteBuffer.asShortBuffer().put(src);
        return this;
    }

    public VertexDataBuf put(IntBuffer src) {
        ensureRemaining(src.remaining() << 2);
        byteBuffer.asIntBuffer().put(src);
        return this;
    }

    public VertexDataBuf put(FloatBuffer src) {
        ensureRemaining(src.remaining() << 2);
        byteBuffer.asFloatBuffer().put(src);
        return this;
    }

    public VertexDataBuf put(DoubleBuffer src) {
        ensureRemaining(src.remaining() << 3);
        byteBuffer.asDoubleBuffer().put(src);
        return this;
    }

    public VertexDataBuf setTranslation(float x, float y, float z) {
        transX = x;
        transY = y;
        transZ = z;
        return this;
    }

    public VertexDataBuf pos(float x, float y, float z) {
        if (vertexFormat.isUsingPosition()) {
            byteBuffer.putFloat(x + transX).putFloat(y + transY).putFloat(z + transZ);
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
            byteBuffer.putFloat(r).putFloat(g).putFloat(b);
            if (vertexFormat.isUsingAlpha()) {
                byteBuffer.putFloat(1f);
            }
        }
        return this;
    }

    public VertexDataBuf rgba(float r, float g, float b, float a) {
        if (vertexFormat.isUsingColor()) {
            byteBuffer.putFloat(r).putFloat(g).putFloat(b);
            if (vertexFormat.isUsingAlpha()) {
                byteBuffer.putFloat(a);
            }
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
            byteBuffer.putFloat(u).putFloat(v);
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
            byteBuffer.putFloat(nx).putFloat(ny).putFloat(nz);
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
            byteBuffer.putFloat(tx);
            byteBuffer.putFloat(ty);
            byteBuffer.putFloat(tz);
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
            byteBuffer.putFloat(bx).putFloat(by).putFloat(bz);
        }
        return this;
    }

    public VertexDataBuf bitangent(float[] array, int start) {
        return bitangent(array[start], array[start + 1], array[start + 2]);
    }

    public VertexDataBuf endVertex() {
        if (byteBuffer.position() % vertexFormat.getBytes() != 0) {
            throw new IllegalStateException("Invalid vertex data");
        }
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
