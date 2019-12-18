package nullengine.client.rendering.gl.shader;

import nullengine.client.rendering.gl.util.GLCleaner;
import nullengine.client.rendering.management.BindingProxy;
import org.joml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram implements BindingProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShaderProgram.class);

    private int id;
    private GLCleaner.Disposable disposable;

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

    @Override
    public void setUniform(String location, int value) {
        Uniforms.setUniform(getUniformLocation(location), value);
    }

    @Override
    public void setUniform(String location, float value) {
        Uniforms.setUniform(getUniformLocation(location), value);
    }

    @Override
    public void setUniform(String location, boolean value) {
        Uniforms.setUniform(getUniformLocation(location), value);
    }

    @Override
    public void setUniform(String location, Vector2fc value) {
        Uniforms.setUniform(getUniformLocation(location), value);
    }

    @Override
    public void setUniform(String location, Vector3fc value) {
        Uniforms.setUniform(getUniformLocation(location), value);
    }

    @Override
    public void setUniform(String location, Vector4fc value) {
        Uniforms.setUniform(getUniformLocation(location), value);
    }

    @Override
    public void setUniform(String location, Matrix3fc value) {
        Uniforms.setUniform(getUniformLocation(location), value);
    }

    @Override
    public void setUniform(String location, Matrix4fc value) {
        Uniforms.setUniform(getUniformLocation(location), value);
    }

    @Override
    public void setUniform(String location, Matrix4fc[] values) {
        Uniforms.setUniform(getUniformLocation(location), values);
    }
}
