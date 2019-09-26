package nullengine.math;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Transform {

    public static final Transform DEFAULT = new Transform(new Vector3f(), new Vector3f(), new Vector3f(1));

    private final Vector3f translate = new Vector3f();
    private final Vector3f rotate = new Rotation();
    private final Vector3f scale = new Vector3f();
    private final Matrix4f matrix = new Matrix4f();

    public Vector3fc getTranslate() {
        return translate;
    }

    public Vector3fc getRotate() {
        return rotate;
    }

    public Vector3fc getScale() {
        return scale;
    }

    public Matrix4fc getTransformMatrix() {
        return matrix;
    }

    public Transform(Vector3fc translate, Vector3fc rotate, Vector3fc scale) {
        this.translate.set(translate);
        this.rotate.set(rotate);
        this.scale.set(scale);
        this.matrix.scale(this.scale).rotateXYZ(this.rotate).translate(this.translate);
    }
}
