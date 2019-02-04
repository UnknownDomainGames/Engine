package unknowndomain.engine.client.rendering.shader;

import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import unknowndomain.engine.Platform;
import unknowndomain.engine.util.Disposable;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;

public class ShaderProgram implements Disposable {

    protected int programId = -1;

    private static void setUniform(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    private static void setUniform(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    private static void setUniform(int location, boolean value) {
        GL20.glUniform1i(location, value ? 1 : 0);
    }

    private static void setUniform(int location, Vector2fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2);
            value.get(buffer);
            GL20.glUniform2fv(location, buffer);
        }
    }

    private static void setUniform(int location, Vector3fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            value.get(buffer);
            GL20.glUniform3fv(location, buffer);
        }
    }

    private static void setUniform(int location, Vector4fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4);
            value.get(buffer);
            GL20.glUniform4fv(location, buffer);
        }
    }

    private static void setUniform(int location, Matrix3fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3 * 3);
            value.get(buffer);
            GL20.glUniformMatrix3fv(location, false, buffer);
        }
    }

    private static void setUniform(int location, Matrix4fc value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            value.get(buffer);
            GL20.glUniformMatrix4fv(location, false, buffer);
        }
    }

    private static void setUniform(int location, Matrix4fc[] values) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = values != null ? values.length : 0;
            FloatBuffer buffer = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                values[i].get(16 * i, buffer);
            }
            GL20.glUniformMatrix4fv(location, false, buffer);
        }
    }

    @Deprecated
    public static void enableVertexAttrib(int location) {
        GL20.glEnableVertexAttribArray(location);
    }

    public static void disableVertexAttrib(int location) {
        GL20.glDisableVertexAttribArray(location);
    }

    @Deprecated
    public static void pointVertexAttribute(int location, int size, int stride, int offset) {
        GL20.glVertexAttribPointer(location, size, GL11.GL_FLOAT, false, stride, offset);
    }

    void init(Shader... shaders) {
        programId = GL20.glCreateProgram();

        for (Shader s : shaders)
            attachShader(s);

        linkProgram();
        if(GL20.glGetProgrami(programId, GL_LINK_STATUS) != GL_TRUE){
            Platform.getLogger().warn(String.format("Error initializing shader program (id:%d), log: %s", programId,
                    GL20.glGetProgramInfoLog(programId, 2048)));
        }
        use();

        GL20.glValidateProgram(programId);
        if(GL20.glGetProgrami(programId, GL_VALIDATE_STATUS) != GL_TRUE){
            Platform.getLogger().warn(String.format("Error initializing shader program (id:%d), log: %s", programId,
                    GL20.glGetProgramInfoLog(programId, 2048)));
        }

        for (Shader shader : shaders)
            shader.deleteShader();

        unuse();
    }

    void use() {
        GL20.glUseProgram(programId);
    }

    @Deprecated
    public void unuse() {
        GL20.glUseProgram(0);
    }

    protected void attachShader(Shader shader) {
        GL20.glAttachShader(programId, shader.getShaderId());
    }

    public void linkProgram() {
        GL20.glLinkProgram(programId);
    }

    public int getAttributeLocation(String name) {
        return GL20.glGetAttribLocation(programId, name);
    }

    @Override
    public void dispose() {
        unuse();

        GL20.glDeleteProgram(programId);
        programId = -1;
    }

    public int getUniformLocation(String name) {
        return GL20.glGetUniformLocation(programId, name);
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

    public void setUniform(String location, Vector2fc value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Vector3fc value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Vector4fc value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Matrix3fc value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Matrix4fc value) {
        setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Matrix4fc[] values) {
        setUniform(getUniformLocation(location), values);
    }
}
