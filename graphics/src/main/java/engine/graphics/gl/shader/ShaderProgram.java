package engine.graphics.gl.shader;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.util.Cleaner;
import org.joml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShaderProgram.class);

    private int id;
    private Cleaner.Disposable disposable;

    public ShaderProgram(CompiledShader... shaders) {
        id = glCreateProgram();
        disposable = GLCleaner.registerProgram(this, id);

        for (CompiledShader shader : shaders) {
            glAttachShader(this.id, shader.getId());
        }

        glLinkProgram(id);
        if (glGetProgrami(id, GL_LINK_STATUS) != GL_TRUE) {
            LOGGER.warn("Error initializing shader program (id: {}), log: {}", id, glGetProgramInfoLog(id));
        }

        use();
        glValidateProgram(id);
        if (glGetProgrami(id, GL_VALIDATE_STATUS) != GL_TRUE) {
            LOGGER.warn("Error initializing shader program (id: {}), log: {}", id, glGetProgramInfoLog(id));
        }
        glUseProgram(0);

        for (CompiledShader shader : shaders) {
            shader.dispose();
        }
    }

    public int getId() {
        return id;
    }

    public void use() {
        glUseProgram(id);
    }

    public void dispose() {
        if (isDisposed()) {
            return;
        }

        glUseProgram(0);
        disposable.dispose();
        id = 0;
    }

    public boolean isDisposed() {
        return id == 0;
    }

    public int getUniformLocation(String name) {
        return glGetUniformLocation(id, name);
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
