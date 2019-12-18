package nullengine.client.rendering.gl;

import nullengine.client.rendering.gl.util.GLCleaner;
import org.lwjgl.opengl.GL15;

import java.nio.*;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

public class VertexBufferObject {

    private final GLBufferType type;
    private final GLBufferUsage usage;

    private int id;
    private GLCleaner.Disposable disposable;

    public VertexBufferObject(GLBufferType type, GLBufferUsage usage) {
        this.type = type;
        this.usage = usage;
        var id = glGenBuffers();
        this.id = id;
        this.disposable = GLCleaner.register(this, () -> glDeleteBuffers(id));
    }

    public VertexBufferObject(GLBufferType type, GLBufferUsage usage, ByteBuffer buffer) {
        this(type, usage);
        uploadData(buffer);
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
    }

    public void uploadData(ShortBuffer buffer) {
        bind();
        GL15.glBufferData(type.gl, buffer, usage.gl);
    }

    public void uploadData(IntBuffer buffer) {
        bind();
        GL15.glBufferData(type.gl, buffer, usage.gl);
    }

    public void uploadData(LongBuffer buffer) {
        bind();
        GL15.glBufferData(type.gl, buffer, usage.gl);
    }

    public void uploadData(FloatBuffer buffer) {
        bind();
        GL15.glBufferData(type.gl, buffer, usage.gl);
    }

    public void uploadData(DoubleBuffer buffer) {
        bind();
        GL15.glBufferData(type.gl, buffer, usage.gl);
    }

    public void uploadData(short[] data) {
        bind();
        GL15.glBufferData(type.gl, data, usage.gl);
    }

    public void uploadData(int[] data) {
        bind();
        GL15.glBufferData(type.gl, data, usage.gl);
    }

    public void uploadData(long[] data) {
        bind();
        GL15.glBufferData(type.gl, data, usage.gl);
    }

    public void uploadData(float[] data) {
        bind();
        GL15.glBufferData(type.gl, data, usage.gl);
    }

    public void uploadData(double[] data) {
        bind();
        GL15.glBufferData(type.gl, data, usage.gl);
    }

    public void uploadSubData(ByteBuffer buffer, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, buffer);
    }

    public void uploadSubData(ShortBuffer buffer, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, buffer);
    }

    public void uploadSubData(IntBuffer buffer, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, buffer);
    }

    public void uploadSubData(LongBuffer buffer, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, buffer);
    }

    public void uploadSubData(FloatBuffer buffer, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, buffer);
    }

    public void uploadSubData(DoubleBuffer buffer, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, buffer);
    }

    public void uploadSubData(short[] data, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, data);
    }

    public void uploadSubData(int[] data, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, data);
    }

    public void uploadSubData(long[] data, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, data);
    }

    public void uploadSubData(float[] data, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, data);
    }

    public void uploadSubData(double[] data, long offset) {
        bind();
        GL15.glBufferSubData(type.gl, offset, data);
    }

    public void dispose() {
        if (id == 0) {
            return;
        }
        disposable.dispose();
        id = 0;
    }

    public boolean isDisposed() {
        return id == 0;
    }
}
