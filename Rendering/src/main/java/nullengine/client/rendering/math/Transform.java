package nullengine.client.rendering.math;

import org.joml.*;

public class Transform {

    public static final Transform DEFAULT = new Transform(new Vector3f(), new Vector3f(), new Vector3f(1));

    private final Vector3f translate = new Vector3f();
    private final Vector3f rotate = new Vector3f();
    private final Vector3f scale = new Vector3f();
    private final Matrix4f transformationMatrix = new Matrix4f();

    public Vector3fc getTranslate() {
        return translate;
    }

    public Vector3fc getRotate() {
        return rotate;
    }

    public Vector3fc getScale() {
        return scale;
    }

    public Transform(Vector3fc translate, Vector3fc rotate, Vector3fc scale) {
        this.translate.set(translate);
        this.rotate.set(rotate);
        this.scale.set(scale);
        this.transformationMatrix.scale(this.scale).rotateXYZ(this.rotate).translate(this.translate);
    }

    public Vector4f transform(Vector4f v) {
        return transformationMatrix.transform(v);
    }

    public Vector4f transform(Vector4fc v, Vector4f dest) {
        return transformationMatrix.transform(v, dest);
    }
}
