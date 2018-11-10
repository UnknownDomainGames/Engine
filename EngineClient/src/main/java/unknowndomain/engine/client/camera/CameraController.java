package unknowndomain.engine.client.camera;

import org.joml.Vector3d;
import org.joml.Vector3f;
import unknowndomain.engine.client.display.Camera;

public abstract class CameraController {
    protected Camera camera;
    protected boolean paused = false;

    public CameraController(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    public abstract void update(Vector3d position, Vector3f rotation);

    public abstract void handleCursorMove(double x, double y);

    public void handleScroll(double xoffset, double yoffset) {
        // registerRenderer.getCamera().zoom((-yoffset / window.getHeight() * 2 + 1)*1.5);
    }
}
