package engine.graphics.shader;

import org.joml.*;

public interface ShaderResource {

    UniformBlock getUniformBlock(String name);

    UniformTexture getUniformTexture(String name);

    void setUniform(String name, int value);

    void setUniform(String name, float value);

    void setUniform(String name, boolean value);

    void setUniform(String name, Vector2fc value);

    void setUniform(String name, Vector3fc value);

    void setUniform(String name, Vector4fc value);

    void setUniform(String name, Matrix3fc value);

    void setUniform(String name, Matrix3x2fc value);

    void setUniform(String name, Matrix4fc value);

    void setUniform(String name, Matrix4x3fc value);

    void setUniform(String name, Matrix4fc[] values);

    void refresh();
}
