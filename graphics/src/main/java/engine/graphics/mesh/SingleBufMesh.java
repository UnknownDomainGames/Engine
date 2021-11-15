package engine.graphics.mesh;

import engine.graphics.GraphicsEngine;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuffer;
import engine.graphics.vertex.VertexFormat;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

public interface SingleBufMesh extends Mesh {
    static Builder builder() {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createSingleBufMeshBuilder();
    }

    VertexFormat getVertexFormat();

    void setDrawMode(DrawMode drawMode);

    void uploadData(@Nonnull VertexDataBuffer buffer);

    void uploadData(@Nonnull VertexFormat format, @Nonnull ByteBuffer buffer);

    interface Builder {
        Builder setStatic();

        Builder setDynamic();

        Builder setStreamed();

        Builder drawMode(DrawMode drawMode);

        SingleBufMesh build();

        SingleBufMesh build(@Nonnull VertexDataBuffer buffer);

        SingleBufMesh build(@Nonnull VertexFormat format, @Nonnull ByteBuffer buffer);
    }
}
