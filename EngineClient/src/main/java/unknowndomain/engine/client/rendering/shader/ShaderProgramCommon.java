package unknowndomain.engine.client.rendering.shader;

import org.lwjgl.opengl.GL20;
import unknowndomain.engine.api.client.shader.Shader;
import unknowndomain.engine.api.client.shader.ShaderProgram;
import unknowndomain.engine.api.client.shader.ShaderType;

public class ShaderProgramCommon extends ShaderProgram {
    private static final int A_POSITION = 0, A_TEXTCOORD = 1, A_NORMAL = 2, A_COLOR = 3;
    protected Shader vertexShader;
    protected Shader fragmentShader;

    public ShaderProgramCommon() {
        vertexShader = new Shader("assets/unknowndomain/shader/common.vert", ShaderType.VERTEX_SHADER);
        fragmentShader = new Shader("assets/unknowndomain/shader/common.frag", ShaderType.FRAGMENT_SHADER);
    }

    @Override
    public void createShader() {
        shaderId = GL20.glCreateProgram();

        vertexShader.loadShader();
        attachShader(vertexShader);
        fragmentShader.loadShader();
        attachShader(fragmentShader);

        linkShader();
        useShader();

        GL20.glValidateProgram(shaderId);

        GL20.glUseProgram(0);
    }

    @Override
    public void deleteShader() {
        GL20.glUseProgram(0);
        vertexShader.deleteShader();
        fragmentShader.deleteShader();
        GL20.glDeleteProgram(shaderId);
        shaderId = -1;
    }

    @Override
    public void linkShader() {
        GL20.glLinkProgram(shaderId);
        System.out.println(GL20.glGetProgramInfoLog(shaderId));
    }

    @Override
    public void useShader() {
        GL20.glUseProgram(shaderId);
    }

    @Override
    public int getUniformLocation(String name) {
        return GL20.glGetUniformLocation(shaderId, name);
    }

    @Override
    public int getAttributeLocation(String name) {
        return GL20.glGetAttribLocation(shaderId, name);
    }

}
