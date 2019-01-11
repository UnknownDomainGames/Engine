package unknowndomain.engine.client.rendering.util.buffer;

import unknowndomain.engine.client.rendering.util.GLDataType;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class GLBufferElement {
    private final GLDataType type;
    private final int size;
    private final int bytes;

    public GLBufferElement(@Nonnull GLDataType type, int size) {
        this.type = requireNonNull(type);
        this.size = size;
        this.bytes = size * type.bytes;
    }

    public GLDataType getType() {
        return type;
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
}
