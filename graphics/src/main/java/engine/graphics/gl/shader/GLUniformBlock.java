package engine.graphics.gl.shader;

import engine.graphics.gl.buffer.GLBufferType;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.shader.UniformBlock;
import engine.graphics.util.CachedBuffer;
import engine.graphics.util.Struct;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL31C;

public final class GLUniformBlock implements UniformBlock {
    private static CachedBuffer cachedBuffer = new CachedBuffer(4096);

    private final String name;
    private final int binding;
    private final GLVertexBuffer buffer;

    private Struct value;

    public GLUniformBlock(String name, int binding) {
        this(name, binding, GLBufferUsage.STREAM_DRAW);
    }

    public GLUniformBlock(String name, int binding, GLBufferUsage drawMode) {
        this.name = name;
        this.binding = binding;
        this.buffer = new GLVertexBuffer(GLBufferType.UNIFORM_BUFFER, drawMode);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Struct get() {
        return value;
    }

    @Override
    public void set(Struct value) {
        this.value = value;
    }

    public void bind() {
        buffer.uploadData(value.get(cachedBuffer.get(value.sizeof())));
        GL30C.glBindBufferBase(GL31C.GL_UNIFORM_BUFFER, binding, buffer.getId());
    }
}
