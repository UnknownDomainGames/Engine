package unknowndomain.engine.api.client.shader;

import org.joml.*;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public abstract class ShaderProgram {

    protected int shaderId = -1;

    public abstract void createShader();

    public int getShaderProgramId(){
        return shaderId;
    }

    public abstract void deleteShader();

    public void attachShader(Shader shader){
        GL20.glAttachShader(shaderId, shader.getShaderId());
    }

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

    public static void setUniform(int location, int value) {
        GL20.glUniform1i(location, value);
    }
    public static void setUniform(int location, float value) {
        GL20.glUniform1f(location, value);
    }
    public static void setUniform(int location, boolean value) {
        GL20.glUniform1i(location, value ? 1 : 0);
    }
    public static void setUniform(int location, Vector2f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2);
            value.get(buffer);
            GL20.glUniformMatrix2fv(location, false, buffer);
        }
    }
    public static void setUniform(int location, Vector3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            value.get(buffer);
            GL20.glUniformMatrix3fv(location, false, buffer);
        }
    }
    public static void setUniform(int location, Vector4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4);
            value.get(buffer);
            GL20.glUniformMatrix4fv(location, false, buffer);
        }
    }
    public static void setUniform(int location, Matrix3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3 * 3);
            value.get(buffer);
            GL20.glUniformMatrix3fv(location, false, buffer);
        }
    }
    public static void setUniform(int location, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            value.get(buffer);
            GL20.glUniformMatrix4fv(location, false, buffer);

        }
    }
    public static void setUniform(int location, Matrix4d value) {
        if(Version.getVersion().startsWith("4")){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer buffer = stack.mallocDouble(4 * 4);
            value.get(buffer);
            GL40.glUniformMatrix4dv(location, false, buffer);
        }}
        else {
            setUniform(location, new Matrix4f(value));
        }
    }

    public void setUniform(String location, int value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, float value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, boolean value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Vector2f value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Vector3f value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Vector4f value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Matrix3f value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Matrix4f value) {
        setUniform(getUniformLocation(location), value);
    }
    public void setUniform(String location, Matrix4d value) {
        setUniform(getUniformLocation(location), value);
    }
}
