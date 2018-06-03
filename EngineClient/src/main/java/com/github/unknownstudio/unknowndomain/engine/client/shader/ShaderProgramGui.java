package com.github.unknownstudio.unknowndomain.engine.client.shader;

import com.github.unknownstudio.unknowndomain.engine.client.util.GLHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ShaderProgramGui extends ShaderProgramDefault {
    @Override
    public void createShader() {

        vertexShaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

        //TODO: maybe we should extract shader into a separated class?
        GL20.glShaderSource(vertexShaderId, GLHelper.readText("/assets/unknowndomain/shader/gui.vert"));
        GL20.glCompileShader(vertexShaderId);
        if (GL20.glGetShaderi(vertexShaderId, GL20.GL_COMPILE_STATUS) == 0) {
            System.out.println("Error compiling Shader code: " + GL20.glGetShaderInfoLog(vertexShaderId, 1024));
        }

        fragmentShaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        GL20.glShaderSource(fragmentShaderId, GLHelper.readText("/assets/unknowndomain/shader/gui.frag"));
        GL20.glCompileShader(fragmentShaderId);
        if (GL20.glGetShaderi(fragmentShaderId, GL20.GL_COMPILE_STATUS) == 0) {
            System.out.println("Error compiling Shader code: " + GL20.glGetShaderInfoLog(fragmentShaderId, 1024));
        }

        shaderId = GL20.glCreateProgram();
        GL20.glAttachShader(shaderId, vertexShaderId);
        GL20.glAttachShader(shaderId, fragmentShaderId);
        GL30.glBindFragDataLocation(shaderId, 0, "fragColor");
        GL20.glLinkProgram(shaderId);
        useShader();

        GL20.glValidateProgram(shaderId);

        //We can deleter the shader after linking to the shader program
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);

        int uniTex = getUniformLocation("texImage");
        GL20.glUniform1i(uniTex, 0);

        Matrix4f model = new Matrix4f();
        model.identity();
        int uniModel = getUniformLocation("model");
        setUniform(uniModel, model);

        Matrix4f view = new Matrix4f();
        view.identity();
        int uniView = getUniformLocation("view");
        setUniform(uniView, view);


        Matrix4f projection = new Matrix4f().identity();
        projection = projection.ortho(0,854,480,0,-1000f,2000f);
        int uniProjection = getUniformLocation("projection");
        setUniform(uniProjection, projection);

        int unicolor = getUniformLocation("color");
        GL20.glUniform4fv(unicolor, new float[]{1f,1f,1f,1f});


        GL20.glUseProgram(0);
    }
}
