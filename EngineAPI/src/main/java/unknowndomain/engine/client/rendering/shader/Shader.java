package unknowndomain.engine.client.rendering.shader;

import org.apache.commons.io.IOUtils;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import unknowndomain.engine.Engine;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int shaderId;
    private ShaderType type;

    public Shader(int shaderId, ShaderType type) {
        this.shaderId = shaderId;
        this.type = type;
    }

    public static void enableVertexAttrib(int location) {
        GL20.glEnableVertexAttribArray(location);
    }

    public static void disableVertexAttrib(int location) {
        GL20.glDisableVertexAttribArray(location);
    }

    public static void pointVertexAttribute(int location, int size, int stride, int offset) {
        GL20.glVertexAttribPointer(location, size, GL11.GL_FLOAT, false, stride, offset);
    }

    public static void setUniform(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    public static void setUniform(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    public static void setUniform(int location, boolean value) {
        GL20.glUniform1i(location, value ? 1 : 0);
    }

    public static void setUniform(int location, Vector2fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2);
            value.get(buffer);
            GL20.glUniform2fv(location, buffer);
        }
    }

    public static void setUniform(int location, Vector3fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            value.get(buffer);
            GL20.glUniform3fv(location, buffer);
        }
    }

    public static void setUniform(int location, Vector4fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4);
            value.get(buffer);
            GL20.glUniform4fv(location, buffer);
        }
    }

    public static void setUniform(int location, Matrix3fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3 * 3);
            value.get(buffer);
            GL20.glUniformMatrix3fv(location, false, buffer);
        }
    }

    public static void setUniform(int location, Matrix4fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            value.get(buffer);
            GL20.glUniformMatrix4fv(location, false, buffer);
        }
    }

    public ShaderType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Shader{" + "programId=" + shaderId + ", type=" + type + '}';
    }

    public void deleteShader() {
        if (shaderId != -1) {
            glDeleteShader(shaderId);
            shaderId = -1;
        }
    }

    public int getShaderId() {
        return shaderId;
    }

    public static Shader create(byte[] content, ShaderType type) throws IOException {
        return create(IOUtils.toString(content, "utf-8"), type);
    }

    public static Shader create(String content, ShaderType type) {
        int shaderId = glCreateShader(type.getGlEnum());
        glShaderSource(shaderId, content);

        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            Engine.getLogger().warn(String.format("Error compiling shader code for %s, log: %s", content,
                    glGetShaderInfoLog(shaderId, 2048)));
        }
        return new Shader(shaderId, type);
    }
}
