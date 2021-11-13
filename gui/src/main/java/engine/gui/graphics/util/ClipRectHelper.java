package engine.gui.graphics.util;

import org.joml.Matrix4fc;
import org.joml.primitives.Rectanglei;

import static org.joml.Matrix4fc.*;

public class ClipRectHelper {
    public static Rectanglei setAndTransform(float x, float y, float width, float height, Matrix4fc m, Rectanglei dest) {
        setAndSort(x, y, x + width, y + height, dest);
        return transform(dest, m, dest);
    }

    public static Rectanglei transform(Rectanglei src, Matrix4fc m) {
        return transform(src, m, src);
    }

    public static Rectanglei transform(Rectanglei src, Matrix4fc m, Rectanglei dest) {
        switch (m.properties()) {
            case (PROPERTY_IDENTITY | PROPERTY_AFFINE | PROPERTY_TRANSLATION | PROPERTY_ORTHONORMAL):
                return dest.set(src);
            case (PROPERTY_AFFINE | PROPERTY_TRANSLATION | PROPERTY_ORTHONORMAL):
                return setAndSort(
                        src.minX + m.m30(), src.minY + m.m31(),
                        src.maxX + m.m30(), src.maxY + m.m31(), dest);
            default:
                return setAndSort(
                        src.minX * m.m00() + src.minY * m.m10() + m.m30(),
                        src.minX * m.m01() + src.minY * m.m11() + m.m31(),
                        src.maxX * m.m00() + src.maxY * m.m10() + m.m30(),
                        src.maxX * m.m01() + src.maxY * m.m11() + m.m31(), dest);

        }
    }

    private static Rectanglei setAndSort(float x0, float y0, float x1, float y1, Rectanglei dest) {
        dest.minX = (int) Math.floor(Math.min(x0, x1));
        dest.minY = (int) Math.floor(Math.min(y0, y1));
        dest.maxX = (int) Math.ceil(Math.max(x0, x1));
        dest.maxY = (int) Math.ceil(Math.max(y0, y1));
        return dest;
    }
}
