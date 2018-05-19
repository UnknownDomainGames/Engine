package com.github.unknownstudio.unknowndomain.engine.client.shader;

import com.github.unknownstudio.unknowndomain.engine.client.util.GLHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;

public class ShaderDefault extends Shader {

    private int shaderId = -1;
    private int vertexShaderId = -1;
    private int fragmentShaderId = -1;
    private int vaoId = -1;

    @Override
    public void createShader() {
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        vertexShaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        /*GL20.glShaderSource(vertexShaderId, "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
                "}");*/
        GL20.glShaderSource(vertexShaderId, GLHelper.readText("/assets/unknowndomain/shader/default.vert"));
        GL20.glCompileShader(vertexShaderId);
        fragmentShaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        /*GL20.glShaderSource(fragmentShaderId, "#version 330 core\n" +
                "layout (location = 1) vec4 vertColor;\n" +
                "\n" +
                "void main(){\n" +
                "    gl_FragColor = vertColor;\n" +
                "}");*/
        GL20.glShaderSource(fragmentShaderId, GLHelper.readText("/assets/unknowndomain/shader/default.frag"));
        GL20.glCompileShader(fragmentShaderId);
        shaderId = GL20.glCreateProgram();
        GL20.glAttachShader(shaderId, vertexShaderId);
        GL20.glAttachShader(shaderId, fragmentShaderId);
        GL20.glLinkProgram(shaderId);


        Matrix4f model = new Matrix4f();
        int uniModel = getUniformLocation("model");
        setUniform(uniModel, model);

        /* Set view matrix to identity matrix */
        Matrix4f view = GLHelper.lookAt(new Vector3f(4,3,3),new Vector3f(0,0,0),new Vector3f(0,1,0));
        int uniView = getUniformLocation("view");
        setUniform(uniView, view);


        Matrix4f projection = GLHelper.perspective((float)Math.toRadians(45), 4.0f/3.0f, 0.1f,100f);
        int uniProjection = getUniformLocation("projection");
        setUniform(uniProjection, projection);
    }

    @Override
    public void deleteShader() {
        GL30.glDeleteVertexArrays(vaoId);

        GL20.glDeleteShader(vertexShaderId);
        vertexShaderId = -1;
        GL20.glDeleteShader(fragmentShaderId);
        fragmentShaderId = -1;
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
    public void enableVertexAttrib(int location){
        GL20.glEnableVertexAttribArray(location);
    }

    @Override
    public void pointVertexAttribute(int location, int size, int stride, int offset) {
        GL20.glVertexAttribPointer(location, size, GL11.GL_FLOAT, false, stride, offset);
    }

    public void setUniform(int location, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            GLHelper.toBuffer(value, buffer);
            GL20.glUniformMatrix4fv(location, false, buffer);
        }
    }

}
