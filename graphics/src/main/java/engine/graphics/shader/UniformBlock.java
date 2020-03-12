package engine.graphics.shader;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public interface UniformBlock {

    String getName();

    Value get();

    void set(Value value);

    interface Value {
        ByteBuffer get(MemoryStack stack);

        default ByteBuffer get(ByteBuffer buffer) {
            return get(0, buffer);
        }

        ByteBuffer get(int index, ByteBuffer buffer);
    }
}
