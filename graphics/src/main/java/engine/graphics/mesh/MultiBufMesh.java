package engine.graphics.mesh;

import engine.graphics.GraphicsEngine;
import engine.graphics.util.DataType;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public interface MultiBufMesh extends Mesh {
    static Builder builder() {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createMultiBufMeshBuilder();
    }

    Attribute[] getAttributes();

    Attribute getAttribute(VertexFormat format);

    Indices getIndices();

    interface Attribute {
        VertexFormat getFormat();

        void uploadData(VertexDataBuf buf);

        void uploadData(ByteBuffer buffer);
    }

    interface Indices {
        DataType getType();

        void uploadData(ByteBuffer buffer);

        void uploadData(ShortBuffer buffer);

        void uploadData(IntBuffer buffer);
    }

    interface Builder {
        Builder setStatic();

        Builder setDynamic();

        Builder setStreamed();

        Builder drawMode(DrawMode drawMode);

        Builder attribute(VertexDataBuf buf);

        Builder attribute(VertexFormat format, ByteBuffer buffer);

        Builder indices(ByteBuffer buffer);

        Builder indices(ShortBuffer buffer);

        Builder indices(IntBuffer buffer);

        MultiBufMesh build();
    }
}
