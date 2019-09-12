package nullengine.client.event.rendering;

import nullengine.client.rendering.camera.Camera;
import nullengine.event.Event;

public final class CameraChangeEvent implements Event {

    private final Camera camera;

    public CameraChangeEvent(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }
}
