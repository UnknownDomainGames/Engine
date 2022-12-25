package engine.graphics.vertex;

import engine.math.Math2;
import engine.util.Color;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nonnull;
import java.nio.*;

public class VertexDataBuffer {

    private static final ThreadLocal<VertexDataBuffer> threadLocalBuffer = ThreadLocal.withInitial(() -> create(4096));

    protected ByteBuffer byteBuffer;

    protected float transX;
    protected float transY;
    protected float transZ;

    protected VertexFormat vertexFormat;

    private boolean ready = true;
    private int vertexCount = 0;

    @Deprecated
    public static VertexDataBuffer currentThreadBuffer() {
        return threadLocalBuffer.get();
    }

    public static VertexDataBuffer create(int initialCapacity) {
        return new VertexDataBuffer(initialCapacity);
    }

    public VertexDataBuffer() {
        this(4096);
    }

    public VertexDataBuffer(int initialCapacity) {
        byteBuffer = createBuffer(initialCapacity);
    }

    public ByteBuffer getByteBuffer() {
        if (!ready) {
            throw new IllegalStateException("Buffer not ready");
        }
        return byteBuffer;
    }

    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public boolean isReady() {
        return ready;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    protected ByteBuffer createBuffer(int capacity) {
        return MemoryUtil.memAlloc(capacity);
    }

    protected void freeBuffer(ByteBuffer buffer) {
        MemoryUtil.memFree(buffer);
    }

    protected void grow(int newCapacity) {
        ByteBuffer newBuffer = createBuffer(newCapacity);
        MemoryUtil.memCopy(byteBuffer.flip(), newBuffer);
        freeBuffer(byteBuffer);
        byteBuffer = newBuffer;
    }

    public void begin(@Nonnull VertexFormat format) {
        if (format == null) {
            throw new NullPointerException("Format cannot be null");
        }
        if (byteBuffer == null) {
            throw new IllegalStateException("Buffer is disposed");
        }
        if (!ready) {
            throw new IllegalStateException("Buffer not ready");
        }
        vertexFormat = format;
        byteBuffer.clear();
        ensureRemaining(format.getBytes());
        ready = false;
    }

    public void finish() {
        if (ready) {
            throw new IllegalStateException("Buffer is ready");
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

    @Deprecated
    public VertexDataBuffer setTranslation(float x, float y, float z) {
        transX = x;
        transY = y;
        transZ = z;
        return this;
    }

    public VertexDataBuffer put(byte value) {
        byteBuffer.put(value);
        return this;
    }

    public VertexDataBuffer put(short value) {
        byteBuffer.putShort(value);
        return this;
    }

    public VertexDataBuffer put(int value) {
        byteBuffer.putInt(value);
        return this;
    }

    public VertexDataBuffer put(float value) {
        byteBuffer.putFloat(value);
        return this;
    }

    public VertexDataBuffer put(double value) {
        byteBuffer.putDouble(value);
        return this;
    }

    public VertexDataBuffer put(byte[] src) {
        return put(src, 0, src.length);
    }

    public VertexDataBuffer put(short[] src) {
        return put(src, 0, src.length);
    }

    public VertexDataBuffer put(int[] src) {
        return put(src, 0, src.length);
    }

    public VertexDataBuffer put(float[] src) {
        return put(src, 0, src.length);
    }

    public VertexDataBuffer put(double[] src) {
        return put(src, 0, src.length);
    }

    public VertexDataBuffer put(byte[] src, int offset, int length) {
        ensureRemaining(length);
        byteBuffer.put(src, offset, length);
        return this;
    }

    public VertexDataBuffer put(short[] src, int offset, int length) {
        ensureRemaining(length << 1);
        byteBuffer.asShortBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuffer put(int[] src, int offset, int length) {
        ensureRemaining(length << 2);
        byteBuffer.asIntBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuffer put(float[] src, int offset, int length) {
        ensureRemaining(length << 2);
        byteBuffer.asFloatBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuffer put(double[] src, int offset, int length) {
        ensureRemaining(length << 3);
        byteBuffer.asDoubleBuffer().put(src, offset, length);
        return this;
    }

    public VertexDataBuffer put(ByteBuffer src) {
        ensureRemaining(src.remaining());
        byteBuffer.put(src);
        return this;
    }

    public VertexDataBuffer put(ShortBuffer src) {
        ensureRemaining(src.remaining() << 1);
        byteBuffer.asShortBuffer().put(src);
        return this;
    }

    public VertexDataBuffer put(IntBuffer src) {
        ensureRemaining(src.remaining() << 2);
        byteBuffer.asIntBuffer().put(src);
        return this;
    }

    public VertexDataBuffer put(FloatBuffer src) {
        ensureRemaining(src.remaining() << 2);
        byteBuffer.asFloatBuffer().put(src);
        return this;
    }

    public VertexDataBuffer put(DoubleBuffer src) {
        ensureRemaining(src.remaining() << 3);
        byteBuffer.asDoubleBuffer().put(src);
        return this;
    }

    public VertexDataBuffer pos(float x, float y, float z) {
        if (vertexFormat.isUsingPosition()) {
            byteBuffer.putFloat(x + transX).putFloat(y + transY).putFloat(z + transZ);
        }
        return this;
    }

    public VertexDataBuffer pos(Vector3fc vec) {
        return pos(vec.x(), vec.y(), vec.z());
    }

    public VertexDataBuffer pos(float[] array, int start) {
        return pos(array[start], array[start + 1], array[start + 2]);
    }

    public VertexDataBuffer color(Color color) {
        return rgba(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public VertexDataBuffer color(int argb) {
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

    public VertexDataBuffer rgb(float r, float g, float b) {
        if (vertexFormat.isUsingColor()) {
            byteBuffer.putFloat(r).putFloat(g).putFloat(b);
            if (vertexFormat.isUsingAlpha()) {
                byteBuffer.putFloat(1f);
            }
        }
        return this;
    }

    public VertexDataBuffer rgba(float r, float g, float b, float a) {
        if (vertexFormat.isUsingColor()) {
            byteBuffer.putFloat(r).putFloat(g).putFloat(b);
            if (vertexFormat.isUsingAlpha()) {
                byteBuffer.putFloat(a);
            }
        }
        return this;
    }

    public VertexDataBuffer rgb(float[] array, int start) {
        return rgb(array[start], array[start + 1], array[start + 2]);
    }

    public VertexDataBuffer rgba(float[] array, int start) {
        return rgba(array[start], array[start + 1], array[start + 2], array[start + 3]);
    }

    public VertexDataBuffer tex(Vector2fc uv) {
        return tex(uv.x(), uv.y());
    }

    public VertexDataBuffer tex(float u, float v) {
        if (vertexFormat.isUsingTexCoord()) {
            byteBuffer.putFloat(u).putFloat(v);
        }
        return this;
    }

    public VertexDataBuffer tex(float[] array, int start) {
        return tex(array[start], array[start + 1]);
    }

    public VertexDataBuffer normal(Vector3fc vec) {
        return normal(vec.x(), vec.y(), vec.z());
    }

    public VertexDataBuffer normal(float nx, float ny, float nz) {
        if (vertexFormat.isUsingNormal()) {
            byteBuffer.putFloat(nx).putFloat(ny).putFloat(nz);
        }
        return this;
    }

    public VertexDataBuffer normal(float[] array, int start) {
        return normal(array[start], array[start + 1], array[start + 2]);
    }

    public VertexDataBuffer tangent(Vector3fc vec) {
        return tangent(vec.x(), vec.y(), vec.z());
    }

    public VertexDataBuffer tangent(float tx, float ty, float tz) {
        if (vertexFormat.isUsingTangent()) {
            byteBuffer.putFloat(tx).putFloat(ty).putFloat(tz);
        }
        return this;
    }

    public VertexDataBuffer tangent(float[] array, int start) {
        return tangent(array[start], array[start + 1], array[start + 2]);
    }

    public VertexDataBuffer bitangent(Vector3fc vec) {
        return bitangent(vec.x(), vec.y(), vec.z());
    }

    public VertexDataBuffer bitangent(float bx, float by, float bz) {
        if (vertexFormat.isUsingBitangent()) {
            byteBuffer.putFloat(bx).putFloat(by).putFloat(bz);
        }
        return this;
    }

    public VertexDataBuffer bitangent(float[] array, int start) {
        return bitangent(array[start], array[start + 1], array[start + 2]);
    }

    public VertexDataBuffer endVertex() {
        if (byteBuffer.position() % vertexFormat.getBytes() != 0) {
            throw new IllegalStateException("Invalid vertex data");
        }
        ensureRemaining(vertexFormat.getBytes());
        return this;
    }
}
