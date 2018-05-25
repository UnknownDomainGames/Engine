package com.github.unknownstudio.unknowndomain.engine.client.shader;

import org.joml.Matrix4f;

public abstract class ShaderProgram {


    public abstract void createShader();

    public abstract void deleteShader();

    public abstract void linkShader();

    public abstract void useShader();

    public abstract int getUniformLocation(String name);

    public abstract int getAttributeLocation(String name);

    public abstract void enableVertexAttrib(int location);

    public abstract void pointVertexAttribute(int location, int size, int stride, int offset);

    public abstract void setUniform(int location, Matrix4f value);
    public abstract void setUniform(String location, Matrix4f value);
}
