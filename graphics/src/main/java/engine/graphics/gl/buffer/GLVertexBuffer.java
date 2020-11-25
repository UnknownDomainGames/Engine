package engine.graphics.gl.buffer;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.util.Cleaner;
import org.lwjgl.opengl.EXTDirectStateAccess;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL45;

import java.nio.*;

import static org.lwjgl.opengl.GL15.glGenBuffers;

public final class GLVertexBuffer {

    private final GLBufferType type;

    private GLBufferUsage usage;

    private int id;
    private Cleaner.Disposable disposable;

    public GLVertexBuffer(GLBufferType type, GLBufferUsage usage) {
        this.type = type;
        this.usage = usage;
        //In GL45 DSA, object has to be created by glCreate* function so as to initialize their states first, or else INVALID_OPERATION will thrown out in succeeding call
        if (GLHelper.isSupportARBDirectStateAccess()) {
            this.id = GL45.glCreateBuffers();
        } else {
            this.id = glGenBuffers();
        }
        this.disposable = GLCleaner.registerBuffer(this, id);
    }

    public GLVertexBuffer(GLBufferType type, GLBufferUsage usage, ByteBuffer buffer) {
        this(type, usage);
        uploadData(buffer);
    }

    public int getId() {
        return id;
    }

    public GLBufferType getType() {
        return type;
    }

    public GLBufferUsage getUsage() {
        return usage;
    }

    public void setUsage(GLBufferUsage usage) {
        this.usage = usage;
    }

    public void bind() {
        GL15.glBindBuffer(type.gl, id);
    }

    public void unbind() {
        GL15.glBindBuffer(type.gl, 0);
    }

    public void allocateSize(long size) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, size, usage.gl);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, size, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, size, usage.gl);
            unbind();
        }
    }

    public void uploadData(ByteBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, buffer, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(ShortBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, buffer, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(IntBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, buffer, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(LongBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        }
        // EXT version of DSA has no LongBuffer version exposed
        else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(FloatBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, buffer, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(DoubleBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, buffer, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(short[] data) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, data, usage.gl);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, data, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, data, usage.gl);
        }
    }

    public void uploadData(int[] data) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, data, usage.gl);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, data, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, data, usage.gl);
        }
    }

    public void uploadData(long[] data) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, data, usage.gl);
        }
        // EXT version of DSA has no long[] version exposed
        else {
            bind();
            GL15.glBufferData(type.gl, data, usage.gl);
        }
    }

    public void uploadData(float[] data) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, data, usage.gl);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, data, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, data, usage.gl);
        }
    }

    public void uploadData(double[] data) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferData(id, data, usage.gl);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, data, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, data, usage.gl);
        }
    }

    public void uploadSubData(long offset, ByteBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, buffer);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(long offset, ShortBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, buffer);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(long offset, IntBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, buffer);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(long offset, LongBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        }
        // EXT version of DSA has no LongBuffer version exposed
        else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(long offset, FloatBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, buffer);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(long offset, DoubleBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, buffer);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(long offset, short[] data) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferSubData(id, offset, data);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, data);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, data);
        }
    }

    public void uploadSubData(long offset, int[] data) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferSubData(id, offset, data);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, data);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, data);
        }
    }

    public void uploadSubData(long offset, long[] data) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferSubData(id, offset, data);
        }
        // EXT version of DSA has no long[] version exposed
        else {
            bind();
            GL15.glBufferSubData(type.gl, offset, data);
        }
    }

    public void uploadSubData(long offset, float[] data) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferSubData(id, offset, data);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, data);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, data);
        }
    }

    public void uploadSubData(long offset, double[] data) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glNamedBufferSubData(id, offset, data);
        } else if (GLHelper.isSupportEXTDirectStateAccess()) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, data);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, data);
        }
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
