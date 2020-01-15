package nullengine.client.rendering3d.example;

import nullengine.client.rendering.camera.FreeCamera;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.callback.CursorCallback;
import nullengine.client.rendering.display.callback.KeyCallback;
import nullengine.input.Action;
import nullengine.input.KeyCode;
import org.joml.Vector3f;

public class FlyCameraInput {

    private final FreeCamera camera;

    private float moveSpeed = 1f;
    private float mouseSensitivity = 0.05f;

    private Vector3f motion = new Vector3f();
    private double yaw;
    private double pitch;

    private Window window;
    private final KeyCallback keyCallback = (window, key, scancode, action, mods) -> onKeyInput(key, action);
    private final CursorCallback cursorCallback = (window, xpos, ypos) -> onCursorInput(xpos, ypos);

    private enum MotionType {
        FORWARD(KeyCode.KEY_W),
        BACKWARD(KeyCode.KEY_S),
        LEFT(KeyCode.KEY_A),
        RIGHT(KeyCode.KEY_D),
        UP(KeyCode.KEY_SPACE),
        DOWN(KeyCode.KEY_LEFT_SHIFT);

        public final KeyCode key;

        MotionType(KeyCode key) {
            this.key = key;
        }
    }

    public FlyCameraInput(FreeCamera camera) {
        this.camera = camera;
    }

    public void bindWindow(Window window) {
        if (this.window != null) {
            this.window.removeKeyCallback(keyCallback);
            this.window.removeCursorCallback(cursorCallback);
        }
        this.window = window;
        if (window != null) {
            window.addKeyCallback(keyCallback);
            window.addCursorCallback(cursorCallback);
        }
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public void setMouseSensitivity(float mouseSensitivity) {
        this.mouseSensitivity = mouseSensitivity;
    }

    public void update(float tpf) {
        Vector3f position;
        if (motion.lengthSquared() == 0) {
            position = new Vector3f(camera.getPosition());
        } else {
            position = motion.normalize(new Vector3f()).mul(moveSpeed * tpf).rotateY((float) Math.toRadians(yaw)).add(camera.getPosition());
        }

        camera.look(position, new Vector3f(0, 0, -1).rotateX((float) Math.toRadians(pitch)).rotateY((float) Math.toRadians(yaw)));
    }

    private void onKeyInput(KeyCode key, Action action) {
        if (action == Action.REPEAT) return;

        for (MotionType type : MotionType.values()) {
            if (type.key == key) {
                onMove(type, action == Action.PRESS);
                return;
            }
        }
    }

    private void onMove(MotionType type, boolean enable) {
        switch (type) {
            case FORWARD:
                if (enable) motion.add(0, 0, -1);
                else motion.sub(0, 0, -1);
                break;
            case BACKWARD:
                if (enable) motion.add(0, 0, 1);
                else motion.sub(0, 0, 1);
                break;
            case UP:
                if (enable) motion.add(0, 1, 0);
                else motion.sub(0, 1, 0);
                break;
            case DOWN:
                if (enable) motion.add(0, -1, 0);
                else motion.sub(0, -1, 0);
                break;
            case LEFT:
                if (enable) motion.add(-1, 0, 0);
                else motion.sub(-1, 0, 0);
                break;
            case RIGHT:
                if (enable) motion.add(1, 0, 0);
                else motion.sub(1, 0, 0);
                break;
        }
    }

    private double lastCursorX;
    private double lastCursorY;

    private void onCursorInput(double x, double y) {
        double deltaCursorX = lastCursorX - x; // Not x - lastCursorX
        double deltaCursorY = lastCursorY - y;
        lastCursorX = x;
        lastCursorY = y;
        pitch = Math.min(89.0f, Math.max(-89.0f, pitch + deltaCursorY * mouseSensitivity));
        yaw += deltaCursorX * mouseSensitivity;
    }
}
