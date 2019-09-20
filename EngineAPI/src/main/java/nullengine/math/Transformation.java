package nullengine.math;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Transformation {

    public static final Transformation DEFAULT = new Transformation(new Vector3f(), new Vector3f(), new Vector3f(1));

    private final Vector3f translation = new Vector3f();
    private final Vector3f rotation = new Rotation();
    private final Vector3f scale = new Vector3f();
    private final Matrix4f matrix = new Matrix4f();

    public Vector3fc getTranslation() {
        return translation;
    }

    public Vector3fc getRotation() {
        return rotation;
    }

    public Vector3fc getScale() {
        return scale;
    }

    public Matrix4fc getTransformMatrix() {
        return matrix;
    }

    public Transformation(Vector3fc translation, Vector3fc rotation, Vector3fc scale) {
        this.translation.set(translation);
        this.rotation.set(rotation);
        this.scale.set(scale);
        this.matrix.scale(this.scale).rotateXYZ(this.rotation).translate(this.translation);
    }
}
