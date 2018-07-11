package unknowndomain.engine.api.client.shader;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public abstract class ShaderProgram {


    public abstract void createShader();

    public abstract void deleteShader();

    public abstract void linkShader();

    public abstract void useShader();

    public abstract int getUniformLocation(String name);

    public abstract int getAttributeLocation(String name);

    public static void enableVertexAttrib(int location) {
        GL20.glEnableVertexAttribArray(location);
    }

    public static void disableVertexAttrib(int location) {
        GL20.glDisableVertexAttribArray(location);
    }

    public static void pointVertexAttribute(int location, int size, int stride, int offset) {
        GL20.glVertexAttribPointer(location, size, GL11.GL_FLOAT, false, stride, offset);
    }

    public static void setUniform(int location, Object value) {
        if (value instanceof Integer) {
            GL20.glUniform1i(location, ((Integer) value));
        } else if (value instanceof Float) {
            GL20.glUniform1f(location, ((Float) value));
        } else if (value instanceof Boolean) {
            GL20.glUniform1i(location, (Boolean) value ? 1 : 0);
        } else if (value instanceof Matrix4f) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer buffer = stack.mallocFloat(4 * 4);
                ((Matrix4f) value).get(buffer);
                GL20.glUniformMatrix4fv(location, false, buffer);
            }
        } else if (value instanceof Matrix3f) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer buffer = stack.mallocFloat(3 * 3);
                ((Matrix3f) value).get(buffer);
                GL20.glUniformMatrix3fv(location, false, buffer);
            }
        }
    }

    public abstract void setUniform(String location, Object value);
}
