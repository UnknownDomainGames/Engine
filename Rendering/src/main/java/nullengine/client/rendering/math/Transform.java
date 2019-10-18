package nullengine.client.rendering.math;

import org.joml.*;

public class Transform {

    public static final Transform DEFAULT = new Transform(Vectors.VEC3F_ZERO, Vectors.VEC3F_ZERO, Vectors.VEC3F_ONE);

    private final Vector3f translation = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Vector3f scale = new Vector3f();

    public Transform(Vector3fc translation, Vector3fc rotation, Vector3fc scale) {
        this.translation.set(translation);
        this.rotation.rotateXYZ(rotation.x(), rotation.y(), rotation.z());
        this.scale.set(scale);
    }

    public Transform(Vector3fc translation, Quaternionfc rotation, Vector3fc scale) {
        this.translation.set(translation);
        this.rotation.set(rotation);
        this.scale.set(scale);
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Vector3f transform(Vector3fc vec) {
        return transform(vec, new Vector3f());
    }

    public Vector3f transform(Vector3fc vec, Vector3f dest) {
        return rotation.transform(vec.mul(scale, dest), dest).add(translation);
    }

    public Matrix4f toTransformMatrix() {
        return new Matrix4f().scale(this.scale).rotate(rotation).translate(this.translation);
    }
}
