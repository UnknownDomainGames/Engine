package nullengine.client.rendering.gl;

import nullengine.client.rendering.gl.buffer.GLBufferType;
import nullengine.client.rendering.gl.buffer.GLBufferUsage;
import nullengine.client.rendering.gl.buffer.GLVertexBuffer;
import nullengine.client.rendering.gl.util.GLCleaner;
import nullengine.client.rendering.gl.util.GLHelper;
import nullengine.client.rendering.util.Cleaner;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexDataBuf;
import nullengine.client.rendering.vertex.VertexFormat;
import org.apache.commons.lang3.Validate;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

public class GLSingleBufferMesh {

    private int id;
    private Cleaner.Disposable disposable;
    private GLVertexBuffer vertexBuffer;
    private VertexFormat vertexFormat;
    private GLDrawMode drawMode;

    private int vertexCount;

    private GLSingleBufferMesh() {
    }

    public GLVertexBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public DrawMode getDrawMode() {
        return drawMode.peer;
    }

    public void setDrawMode(DrawMode drawMode) {
        this.drawMode = GLDrawMode.valueOf(drawMode);
    }

    public void setStatic() {
        vertexBuffer.setUsage(GLBufferUsage.STATIC_DRAW);
    }

    public void setDynamic() {
        vertexBuffer.setUsage(GLBufferUsage.DYNAMIC_DRAW);
    }

    public void setStreamed() {
        vertexBuffer.setUsage(GLBufferUsage.STREAM_DRAW);
    }

    public void uploadData(@Nonnull VertexDataBuf buffer) {
        uploadData(buffer.getByteBuffer(), buffer.getVertexFormat(), buffer.getVertexCount());
    }

    public void uploadData(ByteBuffer buffer, @Nonnull VertexFormat format) {
        uploadData(buffer, format, buffer.limit() / format.getBytes());
    }

    private void uploadData(ByteBuffer buffer, @Nonnull VertexFormat format, int vertexCount) {
        setVertexFormat(Validate.notNull(format));
        vertexBuffer.uploadData(buffer);
        this.vertexCount = vertexCount;
    }

    private void setVertexFormat(VertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;
        bind();
        vertexBuffer.bind();
        GLHelper.enableVertexFormat(vertexFormat);
    }

    public void bind() {
        if (id == 0) {
            throw new IllegalStateException("Object has been disposed");
        }
        GL30.glBindVertexArray(id);
    }

    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void draw() {
        bind();
        drawArrays();
    }

    public void drawArrays() {
        GL11.glDrawArrays(drawMode.gl, 0, this.vertexCount);
    }

    public void dispose() {
        if (id == -1) return;
        vertexBuffer.dispose();
        disposable.dispose();
        id = -1;
    }

    public boolean isDisposed() {
        return id == -1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private GLBufferUsage bufferUsage = GLBufferUsage.STATIC_DRAW;
        private DrawMode drawMode = DrawMode.TRIANGLES;

        private Builder() {
        }

        public Builder setStatic() {
            this.bufferUsage = GLBufferUsage.STATIC_DRAW;
            return this;
        }

        public Builder setDynamic() {
            this.bufferUsage = GLBufferUsage.DYNAMIC_DRAW;
            return this;
        }

        public Builder setStreamed() {
            this.bufferUsage = GLBufferUsage.STREAM_DRAW;
            return this;
        }

        public Builder drawMode(DrawMode drawMode) {
            this.drawMode = drawMode;
            return this;
        }

        public GLSingleBufferMesh build() {
            return build(null);
        }

        public GLSingleBufferMesh build(VertexDataBuf buffer) {
            GLSingleBufferMesh mesh = new GLSingleBufferMesh();
            mesh.vertexBuffer = new GLVertexBuffer(GLBufferType.ARRAY_BUFFER, bufferUsage);
            mesh.id = GL30.glGenVertexArrays();
            mesh.disposable = GLCleaner.registerVertexArray(this, mesh.id);
            mesh.setDrawMode(drawMode);
            if (buffer != null) {
                mesh.uploadData(buffer);
            }
            return mesh;
        }
    }
}
