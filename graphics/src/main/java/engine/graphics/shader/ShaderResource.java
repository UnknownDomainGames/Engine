package engine.graphics.shader;

import org.joml.*;

public interface ShaderResource {

    UniformBlock getUniformBlock(String name);

    UniformTexture getUniformTexture(String name);

    @Deprecated
    void setUniform(String name, int value);

    @Deprecated
    void setUniform(String name, float value);

    @Deprecated
    void setUniform(String name, boolean value);

    @Deprecated
    void setUniform(String name, Vector2fc value);

    @Deprecated
    void setUniform(String name, Vector3fc value);

    @Deprecated
    void setUniform(String name, Vector4fc value);

    @Deprecated
    void setUniform(String name, Matrix3fc value);

    @Deprecated
    void setUniform(String name, Matrix3x2fc value);

    @Deprecated
    void setUniform(String name, Matrix4fc value);

    @Deprecated
    void setUniform(String name, Matrix4x3fc value);

    @Deprecated
    void setUniform(String name, Matrix4fc[] values);

    void refresh();
}
