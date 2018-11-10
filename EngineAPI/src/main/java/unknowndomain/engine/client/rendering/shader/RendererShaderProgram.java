package unknowndomain.engine.client.rendering.shader;

import org.joml.*;
import org.lwjgl.opengl.GL20;
import unknowndomain.engine.client.rendering.Renderer;

public abstract class RendererShaderProgram implements Renderer {
    protected int programId = -1;

    protected void attachShader(Shader shader) {
        GL20.glAttachShader(programId, shader.getShaderId());
    }

    protected int getUniformLocation(String name) {
        return GL20.glGetUniformLocation(programId, name);
    }

    protected void useProgram() {
        GL20.glUseProgram(programId);
    }

    protected void unuseProgram() {
        GL20.glUseProgram(0);
    }

    protected void createShader(Shader... shaders) {
        programId = GL20.glCreateProgram();

        for (Shader s : shaders)
            attachShader(s);

        linkProgram();
        useProgram();

        GL20.glValidateProgram(programId);

        for (Shader s : shaders)
            s.deleteShader();

        unuseProgram();
    }

    protected void linkProgram() {
        GL20.glLinkProgram(programId);
    }

    public int getAttributeLocation(String name) {
        return GL20.glGetAttribLocation(programId, name);
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
