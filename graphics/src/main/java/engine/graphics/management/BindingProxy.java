package engine.graphics.management;

import org.joml.*;

public interface BindingProxy {
    void setUniform(String location, int value);

    void setUniform(String location, float value);

    void setUniform(String location, boolean value);

    void setUniform(String location, Vector2fc value);

    void setUniform(String location, Vector3fc value);

    void setUniform(String location, Vector4fc value);

    void setUniform(String location, Matrix3fc value);

    void setUniform(String location, Matrix4fc value);

    void setUniform(String location, Matrix4fc[] values);
}
