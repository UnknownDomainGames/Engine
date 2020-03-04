package engine.graphics.mesh;

import engine.graphics.GraphicsEngine;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

public interface SingleBufMesh {
    static Builder builder() {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createSingleBufferMeshBuilder();
    }

    VertexFormat getVertexFormat();

    DrawMode getDrawMode();

    void setDrawMode(DrawMode drawMode);

    void setStatic();

    void setDynamic();

    void setStreamed();

    void uploadData(@Nonnull VertexDataBuf buffer);

    void uploadData(@Nonnull VertexFormat format, @Nonnull ByteBuffer buffer);

    void bind();

    void draw();

    void dispose();

    boolean isDisposed();

    interface Builder {
        Builder setStatic();

        Builder setDynamic();

        Builder setStreamed();

        Builder drawMode(DrawMode drawMode);

        SingleBufMesh build();

        SingleBufMesh build(@Nonnull VertexDataBuf buffer);

        SingleBufMesh build(@Nonnull VertexFormat format, @Nonnull ByteBuffer buffer);
    }
}
