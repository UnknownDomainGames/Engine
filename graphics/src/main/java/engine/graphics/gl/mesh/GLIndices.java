package engine.graphics.gl.mesh;

import engine.graphics.gl.buffer.GLBufferType;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.mesh.Indices;
import engine.graphics.util.DataType;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

final class GLIndices implements Indices {

    private final GLVertexBuffer buffer;
    private final DataType type;
    private final int glType;

    private int elementCount;

    public GLIndices(Buffer buffer, GLBufferUsage usage) {
        this.buffer = new GLVertexBuffer(GLBufferType.ELEMENT_ARRAY_BUFFER, usage);
        if (buffer instanceof IntBuffer) {
            this.type = DataType.UNSIGNED_INT;
            uploadData((IntBuffer) buffer);
        } else if (buffer instanceof ShortBuffer) {
            this.type = DataType.UNSIGNED_SHORT;
            uploadData((ShortBuffer) buffer);
        } else if (buffer instanceof ByteBuffer) {
            this.type = DataType.UNSIGNED_BYTE;
            uploadData((ByteBuffer) buffer);
        } else {
            throw new IllegalArgumentException();
        }
        this.glType = GLHelper.toGLDataType(this.type);
    }

    @Override
    public DataType getType() {
        return type;
    }

    public int getGLType() {
        return glType;
    }

    public GLVertexBuffer getBuffer() {
        return buffer;
    }

    @Override
    public int getElementCount() {
        return elementCount;
    }

    @Override
    public void uploadData(ByteBuffer buffer) {
        if (type != DataType.UNSIGNED_BYTE) throw new IllegalArgumentException();
        this.buffer.uploadData(buffer);
        this.elementCount = buffer.limit();
    }

    @Override
    public void uploadData(ShortBuffer buffer) {
        if (type != DataType.UNSIGNED_SHORT) throw new IllegalArgumentException();
        this.buffer.uploadData(buffer);
        this.elementCount = buffer.limit();
    }

    @Override
    public void uploadData(IntBuffer buffer) {
        if (type != DataType.UNSIGNED_INT) throw new IllegalArgumentException();
        this.buffer.uploadData(buffer);
        this.elementCount = buffer.limit();
    }
}
