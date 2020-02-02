package nullengine.client.rendering.mesh;

import nullengine.client.rendering.RenderEngine;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexDataBuf;
import nullengine.client.rendering.vertex.VertexFormat;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

public interface SingleBufferMesh {
    static Builder builder() {
        return RenderEngine.getManager().getResourceFactory().createSingleBufferMeshBuilder();
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
