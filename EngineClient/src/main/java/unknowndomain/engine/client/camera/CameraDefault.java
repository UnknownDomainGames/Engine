package unknowndomain.engine.client.camera;

import org.joml.Vector3f;
import unknowndomain.engine.client.display.Camera;

public class CameraDefault implements Camera {
    private Vector3f pos = new Vector3f();
    private Vector3f lookAt = new Vector3f(0, 0, -1);

    @Override
    public Vector3f getPosition() {
        return pos;
    }

    @Override
    public Vector3f getLookAt() {
        return lookAt;
    }
}
