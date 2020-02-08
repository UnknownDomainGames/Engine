package engine.client.event.rendering;

import engine.graphics.camera.OldCamera;
import engine.event.Event;

public final class CameraChangeEvent implements Event {

    private final OldCamera camera;

    public CameraChangeEvent(OldCamera camera) {
        this.camera = camera;
    }

    public OldCamera getCamera() {
        return camera;
    }
}
