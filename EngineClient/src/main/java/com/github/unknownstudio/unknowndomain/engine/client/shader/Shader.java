package com.github.unknownstudio.unknowndomain.engine.client.shader;

public abstract class Shader {


    public abstract void createShader();

    public abstract void deleteShader();

    public abstract void linkShader();

    public abstract void useShader();

    public abstract int getUniformLocation(String name);

}
