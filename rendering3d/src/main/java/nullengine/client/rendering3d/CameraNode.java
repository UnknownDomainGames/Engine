package nullengine.client.rendering3d;

import nullengine.client.rendering.camera.Camera;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class CameraNode extends Node3D {

    private static final Vector3fc FRONT_VECTOR = new Vector3f(0, 0, -1);

    private final Camera camera;
    private final Vector3f lookAt = new Vector3f();

    public CameraNode(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    protected void refreshTransform() {
        super.refreshTransform();
        getWorldTransform().transform(lookAt.set(FRONT_VECTOR), lookAt);
        camera.lookAt(getWorldTranslation(), lookAt);
    }
}
