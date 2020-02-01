package nullengine.client.event.rendering;

import nullengine.client.rendering.camera.OldCamera;
import nullengine.event.Event;

public final class CameraChangeEvent implements Event {

    private final OldCamera camera;

    public CameraChangeEvent(OldCamera camera) {
        this.camera = camera;
    }

    public OldCamera getCamera() {
        return camera;
    }
}
