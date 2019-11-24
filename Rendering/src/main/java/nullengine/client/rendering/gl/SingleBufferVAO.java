package nullengine.client.rendering.gl;

import nullengine.client.rendering.gl.vertex.GLVertexFormat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class SingleBufferVAO {

    private int id;
    private VertexBufferObject vbo;
    private GLVertexFormat vertexFormat;

    private int vertexCount;

    public SingleBufferVAO() {
        this(GLBufferUsage.STATIC_DRAW);
    }

    public SingleBufferVAO(GLBufferUsage usage) {
        vbo = new VertexBufferObject(GLBufferType.ARRAY_BUFFER, usage);
        id = GL30.glGenVertexArrays();
    }

    public void setVertexFormat(GLVertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;
        bind();
        vertexFormat.apply();
        unbind();
    }

    public GLVertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public void bind() {
        if (id == 0) {
            throw new IllegalStateException("Object has been disposed");
        }
        GL30.glBindVertexArray(id);
        vbo.bind();
        if (vertexFormat != null) {
            vertexFormat.enable();
        }
    }

    public void unbind() {
        if (vertexFormat != null) {
            vertexFormat.disable();
        }
        vbo.unbind();
        GL30.glBindVertexArray(0);
    }

    public void uploadData(GLBuffer buffer) {
        uploadData(buffer.getBackingBuffer(), buffer.getVertexCount());
    }

    public void uploadData(ByteBuffer buffer, int vertexCount) {
        vbo.uploadData(buffer);
        this.vertexCount = vertexCount;
    }

    public void uploadData(FloatBuffer buffer, int vertexCount) {
        vbo.uploadData(buffer);
        this.vertexCount = vertexCount;
    }

    public void drawArrays(int glMode) {
        bind();
        GL11.glDrawArrays(glMode, 0, this.vertexCount);
        unbind();
    }

    public void drawArrays(GLDrawMode mode) {
        drawArrays(mode.gl);
    }

    public void dispose() {
        if (id != 0) {
            vbo.dispose();
            GL30.glDeleteVertexArrays(id);
            id = 0;
        }
    }

    public boolean isDisposed() {
        return id == 0;
    }
}
