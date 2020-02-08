package engine.graphics.mesh;

import engine.graphics.GraphicsEngine;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

public interface SingleBufferMesh {
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

    void uploadData(@Nonnull ByteBuffer buffer, @Nonnull VertexFormat format);

    void bind();

    void draw();

    void dispose();

    boolean isDisposed();

    interface Builder {
        Builder setStatic();

        Builder setDynamic();

        Builder setStreamed();

        Builder drawMode(DrawMode drawMode);

        SingleBufferMesh build();

        SingleBufferMesh build(@Nonnull VertexDataBuf buffer);

        SingleBufferMesh build(@Nonnull ByteBuffer buffer, @Nonnull VertexFormat format);
    }
}
