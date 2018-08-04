package unknowndomain.engine.api.client.shader;

import org.joml.*;
import org.lwjgl.opengl.GL20;
import unknowndomain.engine.api.client.rendering.Renderer;

public abstract class RendererShaderProgram implements Renderer {
    protected int shaderId = -1;

    protected void attachShader(Shader shader) {
        GL20.glAttachShader(shaderId, shader.getShaderId());
    }

    protected int getUniformLocation(String name) {
        return GL20.glGetUniformLocation(shaderId, name);
    }

    protected void useShader() {
        GL20.glUseProgram(shaderId);
    }

    protected void linkShader() {
        GL20.glLinkProgram(shaderId);
    }

    public int getAttributeLocation(String name) {
        return GL20.glGetAttribLocation(shaderId, name);
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
