package engine.graphics.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class BufferUtils {
    private BufferUtils() {
    }

    public static ByteBuffer createByteBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public static ByteBuffer wrapAsByteBuffer(byte... src) {
        return createByteBuffer(src.length).put(src).flip();
    }

    public static ByteBuffer wrapAsByteBuffer(short... src) {
        ByteBuffer byteBuffer = createByteBuffer(src.length << 1);
        byteBuffer.asShortBuffer().put(src).flip();
        return byteBuffer;
    }

    public static ByteBuffer wrapAsByteBuffer(int... src) {
        ByteBuffer byteBuffer = createByteBuffer(src.length << 2);
        byteBuffer.asIntBuffer().put(src).flip();
        return byteBuffer;
    }

    public static ByteBuffer wrapAsByteBuffer(long... src) {
        ByteBuffer byteBuffer = createByteBuffer(src.length << 3);
        byteBuffer.asLongBuffer().put(src).flip();
        return byteBuffer;
    }

    public static ByteBuffer wrapAsByteBuffer(float... src) {
        ByteBuffer byteBuffer = createByteBuffer(src.length << 2);
        byteBuffer.asFloatBuffer().put(src).flip();
        return byteBuffer;
    }

    public static ByteBuffer wrapAsByteBuffer(double... src) {
        ByteBuffer byteBuffer = createByteBuffer(src.length << 3);
        byteBuffer.asDoubleBuffer().put(src).flip();
        return byteBuffer;
    }

    public static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        return createByteBuffer(newCapacity).put(buffer.flip());
    }

    public static byte[] toBytes(ByteBuffer buffer) {
        if (!buffer.isDirect()) return buffer.array();

        int capacity = buffer.capacity();
        byte[] bytes = new byte[capacity];
        buffer.get(bytes, 0, capacity);
        return bytes;
    }
}
