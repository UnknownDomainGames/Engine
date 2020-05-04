package engine.graphics.mesh;

import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;

import java.nio.ByteBuffer;

public interface Attribute {
    VertexFormat getFormat();

    int getComponentCount();

    void uploadData(VertexDataBuf buf);

    void uploadData(ByteBuffer buffer);

    void uploadSubData(long offset, VertexDataBuf buf);

    void uploadSubData(long offset, ByteBuffer buffer);
}
