package nullengine.client.rendering.scene;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class CameraNode extends Node {

    private static final Vector3fc FRONT_VECTOR = new Vector3f(0, 0, -1);

    public Vector3fc getPosition() {
        return getWorldTranslation();
    }

    public Vector3fc getLookAt() {
        return getWorldTransform().transform(FRONT_VECTOR);
    }

    public Vector3fc getLookAt(Vector3f dest) {
        return getWorldTransform().transform(FRONT_VECTOR, dest);
    }
}
