package engine.graphics.util;

import java.nio.ByteBuffer;

public abstract class StructDefinition<T> {

    private final Class<T> structClass;
    private final int sizeof;

    public StructDefinition(Class<T> structClass, int sizeof) {
        this.structClass = structClass;
        this.sizeof = sizeof;
    }

    public final Class<T> getStructClass() {
        return structClass;
    }

    public final int sizeof() {
        return sizeof;
    }

    public ByteBuffer toBytes(ByteBuffer buffer, T value) {
        return toBytes(0, buffer, value);
    }

    public abstract ByteBuffer toBytes(int index, ByteBuffer buffer, T value);
}
