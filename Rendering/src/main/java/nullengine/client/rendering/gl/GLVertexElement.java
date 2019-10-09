package nullengine.client.rendering.gl;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class GLVertexElement {
    private final GLDataType type;
    private final Usage usage;
    private final int size;
    private final int bytes;
    private final boolean normalized;

    public GLVertexElement(@Nonnull GLDataType type, int size) {
        this(type, Usage.CUSTOM, size, false);
    }

    public GLVertexElement(@Nonnull GLDataType type, Usage usage, int size) {
        this(type, usage, size, false);
    }

    public GLVertexElement(@Nonnull GLDataType type, Usage usage, int size, boolean normalized) {
        this.type = requireNonNull(type);
        this.usage = requireNonNull(usage);
        this.size = size;
        this.bytes = size * type.bytes;
        this.normalized = normalized;
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

    public boolean isNormalized() {
        return normalized;
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
