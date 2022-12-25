package engine.graphics.gl.shader;

import engine.graphics.gl.buffer.GLBufferType;
import engine.graphics.gl.buffer.GLBufferUsage;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.shader.UniformBlock;
import engine.graphics.util.Struct;
import org.joml.*;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL31C;
import org.lwjgl.system.MemoryStack;

public final class GLUniformBlock implements UniformBlock {
    private final String name;
    private final long size;
    private final int binding;
    private final GLVertexBuffer vbo;

    public GLUniformBlock(String name, long size, int binding) {
        this.name = name;
        this.size = size;
        this.binding = binding;
        this.vbo = new GLVertexBuffer(GLBufferType.UNIFORM_BUFFER, GLBufferUsage.DYNAMIC_DRAW);
        this.vbo.allocateSize(size);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void set(long offset, int value) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, stack.malloc(4).putInt(value).flip());
        }
    }

    @Override
    public void set(long offset, float value) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, stack.malloc(4).putFloat(value).flip());
        }
    }

    @Override
    public void set(long offset, int x, int y) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, stack.malloc(8).putInt(x).putInt(y).flip());
        }
    }

    @Override
    public void set(long offset, float x, float y) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, stack.malloc(8).putFloat(x).putFloat(y).flip());
        }
    }

    @Override
    public void set(long offset, int x, int y, int z) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, stack.malloc(12).putInt(x).putInt(y).putInt(z).flip());
        }
    }

    @Override
    public void set(long offset, float x, float y, float z) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, stack.malloc(12).putFloat(x).putFloat(y).putFloat(z).flip());
        }
    }

    @Override
    public void set(long offset, int x, int y, int z, int w) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, stack.malloc(16).putInt(x).putInt(y).putInt(z).putInt(w).flip());
        }
    }

    @Override
    public void set(long offset, float x, float y, float z, float w) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, stack.malloc(16).putFloat(x).putFloat(y).putFloat(z).putFloat(w).flip());
        }
    }

    @Override
    public void set(long offset, Matrix2fc matrix) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, matrix.get(stack.malloc(16)));
        }
    }

    @Override
    public void set(long offset, Matrix3x2fc matrix) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, matrix.get(stack.malloc(24)));
        }
    }

    @Override
    public void set(long offset, Matrix3fc matrix) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, matrix.get(stack.malloc(36)));
        }
    }

    @Override
    public void set(long offset, Matrix4x3fc matrix) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, matrix.get(stack.malloc(48)));
        }
    }

    @Override
    public void set(long offset, Matrix4fc matrix) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, matrix.get(stack.malloc(64)));
        }
    }

    @Override
    public void set(long offset, Struct struct) {
        try (var stack = MemoryStack.stackPush()) {
            vbo.uploadSubData(offset, struct.get(stack.malloc(struct.sizeof())));
        }
    }

    public void bind() {
        GL30C.glBindBufferBase(GL31C.GL_UNIFORM_BUFFER, binding, vbo.getId());
    }
}
