package engine.graphics.mesh;

import engine.graphics.util.DataType;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public interface Indices {
    DataType getType();

    int getElementCount();

    void uploadData(ByteBuffer buffer);

    void uploadData(ShortBuffer buffer);

    void uploadData(IntBuffer buffer);
}
