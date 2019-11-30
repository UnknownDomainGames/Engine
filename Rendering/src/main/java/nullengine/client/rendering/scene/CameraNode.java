package nullengine.client.rendering.scene;

import nullengine.client.rendering.camera.FixedCamera;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class CameraNode extends Node {

    private static final Vector3fc FRONT_VECTOR = new Vector3f(0, 0, -1);

    private final FixedCamera camera;
    private final Vector3f lookAt = new Vector3f();

    public CameraNode(FixedCamera camera) {
        this.camera = camera;
    }

    public FixedCamera getCamera() {
        return camera;
    }

    @Override
    protected void refreshTransform() {
        super.refreshTransform();
        getWorldTransform().transform(lookAt.set(FRONT_VECTOR), lookAt);
        camera.lookAt(getWorldTranslation(), lookAt);
    }
}
