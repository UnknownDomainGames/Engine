package unknowndomain.engine.client.camera;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import unknowndomain.engine.client.display.Camera;

public class ThirdPersonController extends CameraController {
    public ThirdPersonController(Camera camera) {
        super(camera);
    }

    @Override
    public void update(Vector3f position, Vector3f rotation) {
    }

    @Override
    public void handleCursorMove(double x, double y) {
        Vector3f lookAt = camera.getLookAt();
        Vector3f position = camera.getPosition();

        Vector3f dir = position.sub(lookAt, new Vector3f());

        dir.rotate(new Quaternionf().rotate((float) x, (float) y, 0));

        Vector3f newPos = lookAt.add(dir, new Vector3f());
        camera.getPosition().set(newPos);
    }
}
