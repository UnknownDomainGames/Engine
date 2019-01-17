package unknowndomain.engine.client.rendering.shader;

import org.joml.*;
import org.lwjgl.opengl.GL20;
import unknowndomain.engine.util.Disposable;

public class ShaderProgram implements Disposable {

    protected int programId = -1;

    void init(Shader... shaders) {
        programId = GL20.glCreateProgram();

        for (Shader s : shaders)
            attachShader(s);

        linkProgram();
        use();

        GL20.glValidateProgram(programId);

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
        Shader.setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, float value) {
        Shader.setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, boolean value) {
        Shader.setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Vector2f value) {
        Shader.setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Vector3f value) {
        Shader.setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Vector4f value) {
        Shader.setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Matrix3f value) {
        Shader.setUniform(getUniformLocation(location), value);
    }

    public void setUniform(String location, Matrix4f value) {
        Shader.setUniform(getUniformLocation(location), value);
    }
}
