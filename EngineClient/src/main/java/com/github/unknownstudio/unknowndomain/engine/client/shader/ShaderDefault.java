package com.github.unknownstudio.unknowndomain.engine.client.shader;

import org.lwjgl.opengl.GL20;

public class ShaderDefault extends Shader {

    private int shaderId = -1;
    private int vertexShaderId = -1;
    private int fragmentShaderId = -1;

    @Override
    public void createShader() {
        vertexShaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShaderId, "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
                "}");
        GL20.glCompileShader(vertexShaderId);
        fragmentShaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShaderId, "#version 330 core\n" +
                "layout (location = 1) vec4 vertColor;\n" +
                "\n" +
                "void main(){\n" +
                "    gl_FragColor = vertColor;\n" +
                "}");
        GL20.glCompileShader(fragmentShaderId);
        shaderId = GL20.glCreateProgram();
        GL20.glAttachShader(shaderId, vertexShaderId);
        GL20.glAttachShader(shaderId, fragmentShaderId);
        GL20.glLinkProgram(shaderId);
    }

    @Override
    public void deleteShader() {
        GL20.glDeleteShader(vertexShaderId);
        vertexShaderId = -1;
        GL20.glDeleteShader(fragmentShaderId);
        fragmentShaderId = -1;
        GL20.glDeleteProgram(shaderId);
        shaderId = -1;
    }

    @Override
    public void useShader() {
        GL20.glUseProgram(shaderId);
    }
}
