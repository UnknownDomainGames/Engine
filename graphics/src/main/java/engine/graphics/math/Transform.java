package engine.graphics.math;

import org.joml.*;

public class Transform {

    public static final Transform IDENTITY = new Transform();

    private final Vector3f translation = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Vector3f scale = new Vector3f();

    public Transform() {
        identity();
    }

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

    public void setTranslation(Vector3fc translation) {
        this.translation.set(translation);
    }

    public void setTranslation(float x, float y, float z) {
        this.translation.set(x, y, z);
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Vector3fc rotation) {
        this.rotation.rotateXYZ(rotation.x(), rotation.y(), rotation.z());
    }

    public void setRotation(float angleX, float angleY, float angleZ) {
        this.rotation.rotateXYZ(angleX, angleY, angleZ);
    }

    public void setRotation(Quaternionfc rotation) {
        this.rotation.set(rotation);
    }

    public void setRotation(float x, float y, float z, float w) {
        this.rotation.set(x, y, z, w);
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3fc scale) {
        this.scale.set(scale);
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
    }

    public Transform set(Transform transform) {
        translation.set(transform.translation);
        rotation.set(transform.rotation);
        scale.set(transform.scale);
        return this;
    }

    public Vector3f transform(Vector3fc vec) {
        return transform(vec, new Vector3f());
    }

    public Vector3f transform(Vector3fc vec, Vector3f dest) {
        return rotation.transform(vec.mul(scale, dest), dest).add(translation);
    }

    public void identity() {
        translation.set(0, 0, 0);
        rotation.set(0, 0, 0, 1);
        scale.set(1, 1, 1);
    }

    public void applyParent(Transform parent) {
        translation.mul(parent.scale);
        parent.rotation.transform(translation, translation).add(parent.translation);
        parent.rotation.mul(rotation, rotation);
        scale.mul(parent.scale);
    }

    public Matrix4f getTransformMatrix() {
        return getTransformMatrix(new Matrix4f());
    }

    public Matrix4f getTransformMatrix(Matrix4f matrix4f) {
        return matrix4f.identity().scale(scale).rotate(rotation).translate(translation);
    }

    @Override
    public String toString() {
        return "Transform{" +
                "translation=" + translation +
                ", rotation=" + rotation +
                ", scale=" + scale +
                '}';
    }
}
