package nullengine.client.rendering.scene;

import nullengine.client.rendering.camera.FreeCamera;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class CameraNode extends Node {

    private static final Vector3fc FRONT_VECTOR = new Vector3f(0, 0, -1);

    private final FreeCamera camera;
    private final Vector3f lookAt = new Vector3f();

    public CameraNode(FreeCamera camera) {
        this.camera = camera;
    }

    public FreeCamera getCamera() {
        return camera;
    }

    @Override
    protected void refreshTransform() {
        super.refreshTransform();
        getWorldTransform().transform(lookAt.set(FRONT_VECTOR), lookAt);
        camera.lookAt(getWorldTranslation(), lookAt);
    }
}
