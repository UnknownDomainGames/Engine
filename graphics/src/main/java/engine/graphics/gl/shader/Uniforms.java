package engine.graphics.gl.shader;

import org.joml.*;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class Uniforms {

    public static void setUniform(int location, int value) {
        GL20C.glUniform1i(location, value);
    }

    public static void setUniform(int location, float value) {
        GL20C.glUniform1f(location, value);
    }

    public static void setUniform(int location, boolean value) {
        GL20C.glUniform1i(location, value ? 1 : 0);
    }

    public static void setUniform(int location, Vector2fc value) {
        GL20C.glUniform2f(location, value.x(), value.y());
    }

    public static void setUniform(int location, Vector3fc value) {
        GL20C.glUniform3f(location, value.x(), value.y(), value.z());
    }

    public static void setUniform(int location, Vector4fc value) {
        GL20C.glUniform4f(location, value.x(), value.y(), value.z(), value.w());
    }

    public static void setUniform(int location, Matrix3fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3 * 3);
            value.get(buffer);
            GL20C.glUniformMatrix3fv(location, false, buffer);
        }
    }

    public static void setUniform(int location, Matrix3x2fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3 * 2);
            value.get(buffer);
            GL20C.glUniformMatrix3fv(location, false, buffer);
        }
    }

    public static void setUniform(int location, Matrix4fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            value.get(buffer);
            GL20C.glUniformMatrix4fv(location, false, buffer);
        }
    }

    public static void setUniform(int location, Matrix4x3fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 3);
            value.get(buffer);
            GL20C.glUniformMatrix4fv(location, false, buffer);
        }
    }

    public static void setUniform(int location, Matrix4fc[] values) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = values != null ? values.length : 0;
            FloatBuffer buffer = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                values[i].get(16 * i, buffer);
            }
            GL20C.glUniformMatrix4fv(location, false, buffer);
        }
    }
}
