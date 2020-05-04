package engine.graphics.gl.mesh;

import engine.graphics.gl.buffer.GLBufferType;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.mesh.Attribute;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;

import java.nio.ByteBuffer;

class GLAttribute implements Attribute {

    private final VertexFormat format;
    private final GLVertexBuffer buffer;

    private int componentCount;

    public GLAttribute(VertexFormat format, ByteBuffer buffer, GLBufferUsage usage) {
        this.format = format;
        this.buffer = new GLVertexBuffer(GLBufferType.ARRAY_BUFFER, usage);
        if (buffer != null) uploadData(buffer);
    }

    @Override
    public VertexFormat getFormat() {
        return format;
    }

    public GLVertexBuffer getBuffer() {
        return buffer;
    }

    @Override
    public int getComponentCount() {
        return componentCount;
    }

    @Override
    public void uploadData(VertexDataBuf buf) {
        if (!format.equals(buf.getVertexFormat())) throw new IllegalArgumentException("vertex format");
        uploadData(buf.getByteBuffer());
    }

    @Override
    public void uploadData(ByteBuffer buffer) {
        this.componentCount = buffer.limit() / format.getBytes();
        this.buffer.uploadData(buffer);
    }

    @Override
    public void uploadSubData(long offset, VertexDataBuf buf) {
        if (!format.equals(buf.getVertexFormat())) throw new IllegalArgumentException("vertex format");
        uploadSubData(offset, buf.getByteBuffer());
    }

    @Override
    public void uploadSubData(long offset, ByteBuffer buffer) {
        this.buffer.uploadSubData(offset, buffer);
    }
}
