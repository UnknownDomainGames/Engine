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
        if (GLHelper.isOpenGL45()) {
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

    public void uploadData(ByteBuffer buffer) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, buffer, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(ShortBuffer buffer) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, buffer, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(IntBuffer buffer) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, buffer, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(LongBuffer buffer) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        }
        // EXT version of DSA has no LongBuffer version exposed
        else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(FloatBuffer buffer) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, buffer, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(DoubleBuffer buffer) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferData(id, buffer, usage.gl);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, buffer, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, buffer, usage.gl);
        }
    }

    public void uploadData(short[] data) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferData(id, data, usage.gl);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, data, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, data, usage.gl);
        }
    }

    public void uploadData(int[] data) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferData(id, data, usage.gl);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, data, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, data, usage.gl);
        }
    }

    public void uploadData(long[] data) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferData(id, data, usage.gl);
        }
        // EXT version of DSA has no long[] version exposed
        else {
            bind();
            GL15.glBufferData(type.gl, data, usage.gl);
        }
    }

    public void uploadData(float[] data) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferData(id, data, usage.gl);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, data, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, data, usage.gl);
        }
    }

    public void uploadData(double[] data) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferData(id, data, usage.gl);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferDataEXT(id, data, usage.gl);
        } else {
            bind();
            GL15.glBufferData(type.gl, data, usage.gl);
        }
    }

    public void uploadSubData(ByteBuffer buffer, long offset) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, buffer);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(ShortBuffer buffer, long offset) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, buffer);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(IntBuffer buffer, long offset) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, buffer);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(LongBuffer buffer, long offset) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        }
        // EXT version of DSA has no LongBuffer version exposed
        else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(FloatBuffer buffer, long offset) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, buffer);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(DoubleBuffer buffer, long offset) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferSubData(id, offset, buffer);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, buffer);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, buffer);
        }
    }

    public void uploadSubData(short[] data, long offset) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferSubData(id, offset, data);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, data);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, data);
        }
    }

    public void uploadSubData(int[] data, long offset) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferSubData(id, offset, data);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, data);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, data);
        }
    }

    public void uploadSubData(long[] data, long offset) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferSubData(id, offset, data);
        }
        // EXT version of DSA has no long[] version exposed
        else {
            bind();
            GL15.glBufferSubData(type.gl, offset, data);
        }
    }

    public void uploadSubData(float[] data, long offset) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferSubData(id, offset, data);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
            EXTDirectStateAccess.glNamedBufferSubDataEXT(id, offset, data);
        } else {
            bind();
            GL15.glBufferSubData(type.gl, offset, data);
        }
    }

    public void uploadSubData(double[] data, long offset) {
        if (GLHelper.isOpenGL45()) {
            GL45.glNamedBufferSubData(id, offset, data);
        } else if (GLHelper.getCapabilities().GL_EXT_direct_state_access) {
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
