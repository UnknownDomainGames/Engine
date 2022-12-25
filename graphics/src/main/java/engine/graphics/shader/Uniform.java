package engine.graphics.shader;

import org.joml.*;

public interface Uniform {
    String getName();

    void set(boolean value);

    void set(int value);

    void set(float value);

    void set(int x, int y);

    void set(float x, float y);

    void set(int x, int y, int z);

    void set(float x, float y, float z);

    void set(int x, int y, int z, int w);

    void set(float x, float y, float z, float w);

    void set(Matrix2fc matrix);

    void set(Matrix3x2fc matrix);

    void set(Matrix3fc matrix);

    void set(Matrix4x3fc matrix);

    void set(Matrix4fc matrix);

    void set(Matrix2fc[] matrices);

    void set(Matrix3x2fc[] matrices);

    void set(Matrix3fc[] matrices);

    void set(Matrix4x3fc[] matrices);

    void set(Matrix4fc[] matrices);
}
