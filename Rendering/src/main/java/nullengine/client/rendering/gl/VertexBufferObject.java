package nullengine.client.rendering.gl;

import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class VertexBufferObject {

    private final GLBufferType type;
    private final GLBufferUsage usage;

    private int id;

    public VertexBufferObject(GLBufferType type, GLBufferUsage usage) {
        this.type = type;
        this.usage = usage;
        id = GL15.glGenBuffers();
    }

    public GLBufferType getType() {
        return type;
    }

    public GLBufferUsage getUsage() {
        return usage;
    }

    public void bind() {
        GL15.glBindBuffer(type.gl, id);
    }

    public void unbind() {
        GL15.glBindBuffer(type.gl, 0);
    }

    public void uploadData(ByteBuffer buffer) {
        bind();
        GL15.glBufferData(type.gl, buffer, usage.gl);
        unbind();
    }

    public void uploadData(FloatBuffer buffer) {
        bind();
        GL15.glBufferData(type.gl, buffer, usage.gl);
        unbind();
    }

    public void uploadSubData(ByteBuffer buffer, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, buffer);
        unbind();
    }

    public void uploadSubData(FloatBuffer buffer, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, buffer);
        unbind();
    }

    public void dispose() {
        if (id != 0) {
            GL15.glDeleteBuffers(id);
            id = 0;
        }
    }

    public boolean isDisposed() {
        return id == 0;
    }
}
