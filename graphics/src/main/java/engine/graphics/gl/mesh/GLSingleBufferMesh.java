package engine.graphics.gl.mesh;

import engine.graphics.gl.GLDrawMode;
import engine.graphics.gl.buffer.GLBufferType;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.gl.util.GLCleaner;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.mesh.SingleBufferMesh;
import engine.graphics.util.Cleaner;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

import static org.apache.commons.lang3.Validate.notNull;

public final class GLSingleBufferMesh implements SingleBufferMesh {

    private int id;
    private Cleaner.Disposable disposable;
    private GLVertexBuffer vertexBuffer;
    private VertexFormat vertexFormat = VertexFormat.NONE;
    private GLDrawMode drawMode;

    private int vertexCount;

    private GLSingleBufferMesh() {
        id = GL30.glGenVertexArrays();
        disposable = GLCleaner.registerVertexArray(this, id);
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
    public void bind() {
        if (id == 0) {
            throw new IllegalStateException("Object has been disposed");
        }
        GL30.glBindVertexArray(id);
    }

    @Override
    public void draw() {
        bind();
        drawArrays(0, vertexCount);
    }

    public void draw(int first, int count) {
        bind();
        drawArrays(first, count);
    }

    public void drawArrays(int first, int count) {
        GL11.glDrawArrays(drawMode.gl, first, count);
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
        public GLSingleBufferMesh build(@Nonnull VertexFormat format, @Nonnull ByteBuffer buffer) {
            GLSingleBufferMesh mesh = build();
            mesh.uploadData(format, buffer);
            return mesh;
        }
    }
}
