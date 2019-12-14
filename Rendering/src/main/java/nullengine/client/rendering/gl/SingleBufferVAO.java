package nullengine.client.rendering.gl;

import nullengine.client.rendering.gl.vertex.GLVertexFormat;
import nullengine.client.rendering.scene.Renderable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

public class SingleBufferVAO implements Renderable {

    private int id;
    private VertexBufferObject vbo;
    private GLVertexFormat vertexFormat;
    private GLDrawMode drawMode;

    private int vertexCount;

    public SingleBufferVAO() {
        this(GLBufferUsage.STATIC_DRAW, GLDrawMode.TRIANGLES);
    }

    public SingleBufferVAO(GLDrawMode drawMode) {
        this(GLBufferUsage.STATIC_DRAW, drawMode);
    }

    public SingleBufferVAO(GLBufferUsage usage) {
        this(usage, GLDrawMode.TRIANGLES);
    }

    public SingleBufferVAO(GLBufferUsage usage, GLDrawMode drawMode) {
        vbo = new VertexBufferObject(GLBufferType.ARRAY_BUFFER, usage);
        id = GL30.glGenVertexArrays();
        this.drawMode = drawMode;
    }

    public VertexBufferObject getVbo() {
        return vbo;
    }

    public GLVertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public void setVertexFormat(GLVertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;
        bind();
        vbo.bind();
        vertexFormat.enableAndApply();
    }

    public GLDrawMode getDrawMode() {
        return drawMode;
    }

    public void setDrawMode(GLDrawMode drawMode) {
        this.drawMode = drawMode;
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

    public void uploadData(GLBuffer buffer) {
        uploadData(buffer.getBackingBuffer(), buffer.getVertexCount());
    }

    public void uploadData(ByteBuffer buffer) {
        uploadData(buffer, buffer.limit() / vertexFormat.getStride());
    }

    public void uploadData(ByteBuffer buffer, int vertexCount) {
        vbo.uploadData(buffer);
        this.vertexCount = vertexCount;
    }

    public void draw() {
        bind();
        drawArrays();
    }

    public void drawArrays() {
        GL11.glDrawArrays(drawMode.gl, 0, this.vertexCount);
    }

    @Override
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

    @Override
    public void render() {
        draw();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private GLBufferUsage bufferUsage = GLBufferUsage.STATIC_DRAW;
        private GLVertexFormat vertexFormat;
        private GLDrawMode drawMode = GLDrawMode.TRIANGLES;

        private Builder() {
        }

        public Builder bufferUsage(GLBufferUsage bufferUsage) {
            this.bufferUsage = bufferUsage;
            return this;
        }

        public Builder vertexFormat(GLVertexFormat vertexFormat) {
            this.vertexFormat = vertexFormat;
            return this;
        }

        public Builder drawMode(GLDrawMode drawMode) {
            this.drawMode = drawMode;
            return this;
        }

        public SingleBufferVAO build() {
            SingleBufferVAO singleBufferVAO = new SingleBufferVAO(bufferUsage);
            singleBufferVAO.setVertexFormat(vertexFormat);
            singleBufferVAO.setDrawMode(drawMode);
            return singleBufferVAO;
        }

        public SingleBufferVAO build(GLBuffer buffer) {
            SingleBufferVAO singleBufferVAO = new SingleBufferVAO(bufferUsage);
            singleBufferVAO.setVertexFormat(vertexFormat);
            singleBufferVAO.setDrawMode(drawMode);
            singleBufferVAO.uploadData(buffer);
            return singleBufferVAO;
        }
    }
}
