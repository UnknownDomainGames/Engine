package unknowndomain.engine.client.shader;

import org.joml.*;
import unknowndomain.engine.api.client.shader.Shader;
import unknowndomain.engine.api.client.shader.ShaderProgram;
import unknowndomain.engine.api.client.shader.ShaderType;
import unknowndomain.engine.client.util.GLHelper;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.lang.Math;

public class ShaderProgramDefault extends ShaderProgram {

    protected Shader vertexShader;
    protected Shader fragmentShader;

    public ShaderProgramDefault(){
        vertexShader = new Shader("/assets/unknowndomain/shader/default.vert", ShaderType.VERTEX_SHADER);
        fragmentShader = new Shader("/assets/unknowndomain/shader/default.frag", ShaderType.FRAGMENT_SHADER);
    }

    @Override
    public void createShader() {

        vertexShader.loadShader();

        fragmentShader.loadShader();

        shaderId = GL20.glCreateProgram();
        attachShader(vertexShader);
        attachShader(fragmentShader);
        GL30.glBindFragDataLocation(shaderId, 0, "fragColor");
        linkShader();
        useShader();

        GL20.glValidateProgram(shaderId);

        setUniform("texImage", 0);

        Matrix4f model = new Matrix4f();
        model.identity();
        setUniform("model", model);

        Matrix4f view = new Matrix4f();
        view.identity();
        view = view.lookAt(new Vector3f(0,0,-5f),new Vector3f(0,0,0f),new Vector3f(0,1,0)); //TODO: Controlled by Camera
        int uniView = getUniformLocation("view");
        setUniform(uniView, view);


        Matrix4f projection = new Matrix4f();
        projection = projection.perspective((float)Math.toRadians(60), 16.0f/9.0f, 0.01f,1000f); //TODO: Controlled by Camera
        int uniProjection = getUniformLocation("projection");
        setUniform(uniProjection, projection);


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
    public int getAttributeLocation(String name){
        return GL20.glGetAttribLocation(shaderId, name);
    }

    @Override
    public void setUniform(String location, int value) {
        setUniform(getUniformLocation(location), value);
    }
    @Override
    public void setUniform(String location, float value) {
        setUniform(getUniformLocation(location), value);
    }
    @Override
    public void setUniform(String location, boolean value) {
        setUniform(getUniformLocation(location), value);
    }
    @Override
    public void setUniform(String location, Vector2f value) {
        setUniform(getUniformLocation(location), value);
    }
    @Override
    public void setUniform(String location, Vector3f value) {
        setUniform(getUniformLocation(location), value);
    }
    @Override
    public void setUniform(String location, Vector4f value) {
        setUniform(getUniformLocation(location), value);
    }
    @Override
    public void setUniform(String location, Matrix3f value) {
        setUniform(getUniformLocation(location), value);
    }
    @Override
    public void setUniform(String location, Matrix4f value) {
        setUniform(getUniformLocation(location), value);
    }
}
