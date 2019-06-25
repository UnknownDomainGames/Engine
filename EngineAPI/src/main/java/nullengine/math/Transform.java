package nullengine.math;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Transform {

    private final Vector3f translation = new Vector3f();
    private final Rotation rotation = new Rotation();
    private final Vector3f scale = new Vector3f();

    public Vector3f getTranslation() {
        return translation;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Transform(Vector3fc translation, Vector3fc rotation, Vector3fc scale) {
        this.translation.set(translation);
        this.scale.set(scale);
    }
}
