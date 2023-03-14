package engine.graphics.gl.shader;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.util.Cleaner;
import org.joml.*;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL20C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShaderProgram {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShaderProgram.class);

    private int id;
    private Cleaner.Cleanable cleanable;

    public ShaderProgram(CompiledShader... shaders) {
        id = GL20C.glCreateProgram();
        cleanable = GLCleaner.registerProgram(this, id);

        for (CompiledShader shader : shaders) {
            GL20C.glAttachShader(this.id, shader.getId());
        }

        GL20C.glLinkProgram(id);
        if (GL20C.glGetProgrami(id, GL20C.GL_LINK_STATUS) != GL11C.GL_TRUE) {
            LOGGER.warn("Error initializing shader program (id: {}), log: {}", id, GL20C.glGetProgramInfoLog(id));
        }

        GL20C.glValidateProgram(id);
        if (GL20C.glGetProgrami(id, GL20C.GL_VALIDATE_STATUS) != GL11C.GL_TRUE) {
            LOGGER.warn("Error initializing shader program (id: {}), log: {}", id, GL20C.glGetProgramInfoLog(id));
        }
    }

    public int getId() {
        return id;
    }

    public void use() {
        GL20C.glUseProgram(id);
    }

    public void dispose() {
        if (isDisposed()) {
            return;
        }

        cleanable.clean();
        id = 0;
    }

    public boolean isDisposed() {
        return id == 0;
    }

    public int getUniformLocation(String name) {
        return GL20C.glGetUniformLocation(id, name);
    }

    public void setUniform(String name, int value) {
        Uniforms.setUniform(getUniformLocation(name), value);
    }

    public void setUniform(String name, float value) {
        Uniforms.setUniform(getUniformLocation(name), value);
    }

    public void setUniform(String name, boolean value) {
        Uniforms.setUniform(getUniformLocation(name), value);
    }

    public void setUniform(String name, Vector2fc value) {
        Uniforms.setUniform(getUniformLocation(name), value);
    }

    public void setUniform(String name, Vector3fc value) {
        Uniforms.setUniform(getUniformLocation(name), value);
    }

    public void setUniform(String name, Vector4fc value) {
        Uniforms.setUniform(getUniformLocation(name), value);
    }

    public void setUniform(String name, Matrix3fc value) {
        Uniforms.setUniform(getUniformLocation(name), value);
    }

    public void setUniform(String name, Matrix3x2fc value) {
        Uniforms.setUniform(getUniformLocation(name), value);
    }

    public void setUniform(String name, Matrix4fc value) {
        Uniforms.setUniform(getUniformLocation(name), value);
    }

    public void setUniform(String name, Matrix4x3fc value) {
        Uniforms.setUniform(getUniformLocation(name), value);
    }

    public void setUniform(String name, Matrix4fc[] values) {
        Uniforms.setUniform(getUniformLocation(name), values);
    }
}
