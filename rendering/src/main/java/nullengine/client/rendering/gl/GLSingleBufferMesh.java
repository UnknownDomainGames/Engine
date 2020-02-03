package nullengine.client.rendering.gl;

import nullengine.client.rendering.gl.buffer.GLBufferType;
import nullengine.client.rendering.gl.buffer.GLBufferUsage;
import nullengine.client.rendering.gl.buffer.GLVertexBuffer;
import nullengine.client.rendering.gl.util.GLCleaner;
import nullengine.client.rendering.gl.util.GLHelper;
import nullengine.client.rendering.mesh.SingleBufferMesh;
import nullengine.client.rendering.util.Cleaner;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexDataBuf;
import nullengine.client.rendering.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

import static org.apache.commons.lang3.Validate.notNull;

public final class GLSingleBufferMesh implements SingleBufferMesh {

    private int id;
    private Cleaner.Disposable disposable;
    private GLVertexBuffer vertexBuffer;
    private VertexFormat vertexFormat;
    private GLDrawMode drawMode;

    private int vertexCount;

    private GLSingleBufferMesh() {
    }

    public int getId() {
        return id;
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
    public void setStatic() {
        vertexBuffer.setUsage(GLBufferUsage.STATIC_DRAW);
    }

    @Override
    public void setDynamic() {
        vertexBuffer.setUsage(GLBufferUsage.DYNAMIC_DRAW);
    }

    @Override
    public void setStreamed() {
        vertexBuffer.setUsage(GLBufferUsage.STREAM_DRAW);
    }

    @Override
    public void uploadData(@Nonnull VertexDataBuf buffer) {
        uploadData(buffer.getByteBuffer(), buffer.getVertexFormat(), buffer.getVertexCount());
    }

    @Override
    public void uploadData(@Nonnull ByteBuffer buffer, @Nonnull VertexFormat format) {
        uploadData(buffer, notNull(format), buffer.limit() / format.getBytes());
    }

    private void uploadData(ByteBuffer buffer, VertexFormat format, int vertexCount) {
        setVertexFormat(format);
        vertexBuffer.uploadData(buffer);
        this.vertexCount = vertexCount;
    }

    private void setVertexFormat(VertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;
        bind();
        vertexBuffer.bind();
        GLHelper.enableVertexFormat(vertexFormat);
    }

    @Override
    public void bind() {
        if (id == 0) {
            throw new IllegalStateException("Object has been disposed");
        }
        GL30.glBindVertexArray(id);
    }

    @Override
    public void draw() {
        bind();
        drawArrays();
    }

    public void drawArrays() {
        GL11.glDrawArrays(drawMode.gl, 0, vertexCount);
    }

    @Override
    public void dispose() {
        if (id == 0) return;
        vertexBuffer.dispose();
        disposable.dispose();
        id = 0;
    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements SingleBufferMesh.Builder {
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
        public GLSingleBufferMesh build() {
            GLSingleBufferMesh mesh = new GLSingleBufferMesh();
            mesh.vertexBuffer = new GLVertexBuffer(GLBufferType.ARRAY_BUFFER, bufferUsage);
            mesh.id = GL30.glGenVertexArrays();
            mesh.disposable = GLCleaner.registerVertexArray(this, mesh.id);
            mesh.setDrawMode(drawMode);
            return mesh;
        }

        @Override
        public GLSingleBufferMesh build(@Nonnull VertexDataBuf buffer) {
            GLSingleBufferMesh mesh = build();
            mesh.uploadData(buffer);
            return mesh;
        }

        @Override
        public GLSingleBufferMesh build(@Nonnull ByteBuffer buffer, @Nonnull VertexFormat format) {
            GLSingleBufferMesh mesh = build();
            mesh.uploadData(buffer, format);
            return mesh;
        }
    }
}
