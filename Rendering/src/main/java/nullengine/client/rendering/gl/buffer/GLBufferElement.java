package nullengine.client.rendering.gl.buffer;

import nullengine.client.rendering.gl.GLDataType;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class GLBufferElement {
    private final GLDataType type;
    private final Usage usage;
    private final int size;
    private final int bytes;

    public GLBufferElement(@Nonnull GLDataType type, int size) {
        this(type, Usage.CUSTOM, size);
    }

    public GLBufferElement(@Nonnull GLDataType type, Usage usage, int size) {
        this.type = requireNonNull(type);
        this.usage = requireNonNull(usage);
        this.size = size;
        this.bytes = size * type.bytes;
    }

    public GLDataType getType() {
        return type;
    }

    public Usage getUsage() {
        return usage;
    }

    public int getSize() {
        return size;
    }

    public int getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return "GLBufferElement{" +
                "type=" + type +
                ", size=" + size +
                '}';
    }

    public static enum Usage {
        POSITION,
        COLOR,
        TEXTURE_UV,
        NORMAL,
        CUSTOM
    }
}
