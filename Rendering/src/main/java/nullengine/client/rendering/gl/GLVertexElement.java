package nullengine.client.rendering.gl;

import javax.annotation.Nonnull;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

public class GLVertexElement {

    private final GLDataType type;
    private final Usage usage;
    private final int size;
    private final int bytes;
    private final boolean normalized;

    public enum Usage {
        POSITION,
        COLOR,
        TEXTURE_UV,
        NORMAL,
        CUSTOM
    }

    public GLVertexElement(@Nonnull GLDataType type, int size) {
        this(type, Usage.CUSTOM, size, false);
    }

    public GLVertexElement(@Nonnull GLDataType type, int size, boolean normalized) {
        this(type, Usage.CUSTOM, size, normalized);
    }

    public GLVertexElement(@Nonnull GLDataType type, @Nonnull Usage usage, int size) {
        this(type, usage, size, false);
    }

    public GLVertexElement(@Nonnull GLDataType type, @Nonnull Usage usage, int size, boolean normalized) {
        this.type = notNull(type);
        this.usage = notNull(usage);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GLVertexElement that = (GLVertexElement) o;
        return size == that.size &&
                bytes == that.bytes &&
                normalized == that.normalized &&
                type == that.type &&
                usage == that.usage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, usage, size, bytes, normalized);
    }
}
