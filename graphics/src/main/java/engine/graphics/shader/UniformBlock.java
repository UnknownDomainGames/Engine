package engine.graphics.shader;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public interface UniformBlock {

    String getName();

    Value get();

    void set(Value value);

    interface Value {
        ByteBuffer write(MemoryStack stack);
    }
}
