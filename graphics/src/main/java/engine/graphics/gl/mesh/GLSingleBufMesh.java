package engine.graphics.gl.mesh;

import engine.graphics.gl.GLDrawMode;
import engine.graphics.gl.buffer.GLBufferType;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.mesh.SingleBufMesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

import static org.apache.commons.lang3.Validate.notNull;

public final class GLSingleBufMesh extends GLMesh implements SingleBufMesh {

    private GLVertexBuffer vertexBuffer;
    private VertexFormat vertexFormat = VertexFormat.NONE;

    public static Builder builder() {
        return new Builder();
    }

    private GLSingleBufMesh() {
    }

    @Override
    protected boolean hasIndices() {
        return false;
    }

    @Override
    protected int getIndicesType() {
        return 0;
    }

    public GLVertexBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    @Override
    public DrawMode getDrawMode() {
        return drawMode.peer;
    }

    @Override
    public void setDrawMode(DrawMode drawMode) {
        this.drawMode = GLDrawMode.valueOf(drawMode);
    }

    @Override
    public void uploadData(@Nonnull VertexDataBuf buffer) {
        uploadData(buffer.getVertexFormat(), buffer.getByteBuffer(), buffer.getVertexCount());
    }

    @Override
    public void uploadData(@Nonnull VertexFormat format, @Nonnull ByteBuffer buffer) {
        uploadData(notNull(format), buffer, buffer.limit() / format.getBytes());
    }

    private void uploadData(VertexFormat format, ByteBuffer buffer, int vertexCount) {
        setVertexFormat(format);
        vertexBuffer.uploadData(buffer);
        this.vertexCount = vertexCount;
    }

    private void setVertexFormat(VertexFormat vertexFormat) {
        if (this.vertexFormat.equals(vertexFormat)) return;
        bind();
        vertexBuffer.bind();
        GLHelper.disableVertexFormat(this.vertexFormat);
        GLHelper.enableVertexFormat(vertexFormat);
        this.vertexFormat = vertexFormat;
    }

    @Override
    public void dispose() {
        if (id == 0) return;
        vertexBuffer.dispose();
        disposable.dispose();
        id = 0;
    }

    public static final class Builder implements SingleBufMesh.Builder {
        private GLBufferUsage bufferUsage = GLBufferUsage.STATIC_DRAW;
        private DrawMode drawMode = DrawMode.TRIANGLES;

        private Builder() {
        }

        @Override
        public Builder setStatic() {
            this.bufferUsage = GLBufferUsage.STATIC_DRAW;
            return this;
        }

        @Override
        public Builder setDynamic() {
            this.bufferUsage = GLBufferUsage.DYNAMIC_DRAW;
            return this;
        }

        @Override
        public Builder setStreamed() {
            this.bufferUsage = GLBufferUsage.STREAM_DRAW;
            return this;
        }

        @Override
        public Builder drawMode(DrawMode drawMode) {
            this.drawMode = drawMode;
            return this;
        }

        @Override
        public GLSingleBufMesh build() {
            GLSingleBufMesh mesh = new GLSingleBufMesh();
            mesh.vertexBuffer = new GLVertexBuffer(GLBufferType.ARRAY_BUFFER, bufferUsage);
            mesh.setDrawMode(drawMode);
            return mesh;
        }

        @Override
        public GLSingleBufMesh build(@Nonnull VertexDataBuf buffer) {
            GLSingleBufMesh mesh = build();
            mesh.uploadData(buffer);
            return mesh;
        }

        @Override
        public GLSingleBufMesh build(@Nonnull VertexFormat format, @Nonnull ByteBuffer buffer) {
            GLSingleBufMesh mesh = build();
            mesh.uploadData(format, buffer);
            return mesh;
        }
    }
}
