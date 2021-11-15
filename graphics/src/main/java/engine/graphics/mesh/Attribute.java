package engine.graphics.mesh;

import engine.graphics.vertex.VertexDataBuffer;
import engine.graphics.vertex.VertexFormat;

import java.nio.ByteBuffer;

public interface Attribute {
    VertexFormat getFormat();

    int getComponentCount();

    void uploadData(VertexDataBuffer buffer);

    void uploadData(ByteBuffer buffer);

    void uploadSubData(long offset, VertexDataBuffer buffer);

    void uploadSubData(long offset, ByteBuffer buffer);
}
