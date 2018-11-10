package unknowndomain.engine.client.camera;

import org.joml.Vector3d;
import org.joml.Vector3f;
import unknowndomain.engine.client.display.Camera;

public class FirstPersonController extends CameraController {
    private static final float SENSIBILITY = 0.05f;
    private float yaw, pitch, roll;
    private double lastX, lastY;
    private boolean setupLast = false;

    public FirstPersonController(Camera camera) {
        super(camera);
    }

    @Override
    public void update(Vector3d position, Vector3f rotation) {
        camera.getPosition().set(position);

        Vector3f front = new Vector3f((float) (Math.cos(Math.toRadians(this.pitch)) * Math.cos(Math.toRadians(this.yaw))), (float) Math.sin(Math.toRadians(this.pitch)), (float) (Math.cos(Math.toRadians(this.pitch)) * Math.sin(Math.toRadians(this.yaw)))).normalize();
        camera.getPosition().add(front, camera.getLookAt());

        rotation.set(camera.getLookAt());
    }

    @Override
    public void handleCursorMove(double x, double y) {
        if (!paused) {
            double yaw = (x - lastX) * SENSIBILITY;
            double pitch = (lastY - y) * SENSIBILITY;
            lastX = x;
            lastY = y;
            if (setupLast) {
                this.pitch += pitch;
                this.pitch = Math.min(89.0f, Math.max(-89.0f, this.pitch));
                this.yaw += yaw;
            } else setupLast = true;
        }
    }
}
