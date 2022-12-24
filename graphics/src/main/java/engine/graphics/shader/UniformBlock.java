package engine.graphics.shader;

import engine.graphics.util.Struct;
import org.joml.*;

public interface UniformBlock extends Uniform {
    void set(long offset, int value);

    void set(long offset, float value);

    void set(long offset, int x, int y);

    void set(long offset, float x, float y);

    void set(long offset, int x, int y, int z);

    void set(long offset, float x, float y, float z);

    void set(long offset, int x, int y, int z, int w);

    void set(long offset, float x, float y, float z, float w);

    void set(long offset, Matrix2fc matrix);

    void set(long offset, Matrix3x2fc matrix);

    void set(long offset, Matrix3fc matrix);

    void set(long offset, Matrix4x3fc matrix);

    void set(long offset, Matrix4fc matrix);

    void set(long offset, Struct struct);
}
