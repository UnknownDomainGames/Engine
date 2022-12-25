package engine.graphics.gl.shader;

import engine.graphics.shader.Uniform;
import org.joml.*;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL21C;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class GLUniform implements Uniform {
    private final String name;
    private final int location;

    public GLUniform(String name, int location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    @Override
    public void set(boolean value) {
        GL20C.glUniform1i(location, value ? 1 : 0);
    }

    @Override
    public void set(int value) {
        GL20C.glUniform1i(location, value);
    }

    @Override
    public void set(float value) {
        GL20C.glUniform1f(location, value);
    }

    @Override
    public void set(int x, int y) {
        GL20C.glUniform2i(location, x, y);
    }

    @Override
    public void set(float x, float y) {
        GL20C.glUniform2f(location, x, y);
    }

    @Override
    public void set(int x, int y, int z) {
        GL20C.glUniform3i(location, x, y, z);
    }

    @Override
    public void set(float x, float y, float z) {
        GL20C.glUniform3f(location, x, y, z);
    }

    @Override
    public void set(int x, int y, int z, int w) {
        GL20C.glUniform4i(location, x, y, z, w);
    }

    @Override
    public void set(float x, float y, float z, float w) {
        GL20C.glUniform4f(location, x, y, z, w);
    }

    @Override
    public void set(Matrix2fc matrix) {
        try (var stack = MemoryStack.stackPush()) {
            GL20C.glUniformMatrix2fv(location, false, matrix.get(stack.mallocFloat(4)));
        }
    }

    @Override
    public void set(Matrix3x2fc matrix) {
        try (var stack = MemoryStack.stackPush()) {
            GL21C.glUniformMatrix3x2fv(location, false, matrix.get(stack.mallocFloat(4)));
        }
    }

    @Override
    public void set(Matrix3fc matrix) {
        try (var stack = MemoryStack.stackPush()) {
            GL20C.glUniformMatrix3fv(location, false, matrix.get(stack.mallocFloat(9)));
        }
    }

    @Override
    public void set(Matrix4x3fc matrix) {
        try (var stack = MemoryStack.stackPush()) {
            GL21C.glUniformMatrix4x3fv(location, false, matrix.get(stack.mallocFloat(12)));
        }
    }

    @Override
    public void set(Matrix4fc matrix) {
        try (var stack = MemoryStack.stackPush()) {
            GL20C.glUniformMatrix4fv(location, false, matrix.get(stack.mallocFloat(16)));
        }
    }

    @Override
    public void set(Matrix2fc[] matrices) {
        try (var stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(matrices.length << 2);
            for (int i = 0; i < matrices.length; i++) {
                matrices[i].get(i << 2, buffer);
            }
            GL20C.glUniformMatrix2fv(location, false, buffer);
        }
    }

    @Override
    public void set(Matrix3x2fc[] matrices) {
        try (var stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(matrices.length * 6);
            for (int i = 0; i < matrices.length; i++) {
                matrices[i].get(i * 6, buffer);
            }
            GL21C.glUniformMatrix3x2fv(location, false, buffer);
        }
    }

    @Override
    public void set(Matrix3fc[] matrices) {
        try (var stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(matrices.length * 9);
            for (int i = 0; i < matrices.length; i++) {
                matrices[i].get(i * 9, buffer);
            }
            GL20C.glUniformMatrix3fv(location, false, buffer);
        }
    }

    @Override
    public void set(Matrix4x3fc[] matrices) {
        try (var stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(matrices.length * 12);
            for (int i = 0; i < matrices.length; i++) {
                matrices[i].get(i * 12, buffer);
            }
            GL21C.glUniformMatrix4x3fv(location, false, buffer);
        }
    }

    @Override
    public void set(Matrix4fc[] matrices) {
        try (var stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(matrices.length << 4);
            for (int i = 0; i < matrices.length; i++) {
                matrices[i].get(i << 4, buffer);
            }
            GL20C.glUniformMatrix4fv(location, false, buffer);
        }
    }
}
