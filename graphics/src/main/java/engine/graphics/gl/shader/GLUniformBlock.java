package engine.graphics.gl.shader;

import engine.graphics.gl.buffer.GLBufferType;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.shader.UniformBlock;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryStack;

public final class GLUniformBlock implements UniformBlock {
    private final String name;
    private final int binding;
    private final GLVertexBuffer buffer;

    private Value value;

    public GLUniformBlock(String name, int binding) {
        this.name = name;
        this.binding = binding;
        this.buffer = new GLVertexBuffer(GLBufferType.UNIFORM_BUFFER, GLBufferUsage.STREAM_DRAW);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Value get() {
        return value;
    }

    @Override
    public void set(Value value) {
        this.value = value;
    }

    public void bind() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            buffer.uploadData(value.write(stack).flip());
            GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, binding, buffer.getId());
        }
    }
}
