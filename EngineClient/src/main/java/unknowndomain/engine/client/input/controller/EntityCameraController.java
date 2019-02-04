package unknowndomain.engine.client.input.controller;

import org.joml.Vector3f;
import unknowndomain.engine.math.Math2;
import unknowndomain.engine.player.Player;

public class EntityCameraController extends EntityController {

    private static final float SENSIBILITY = 0.05f;
    private static final float MOTION_FACTOR = 0.2f;

    private final Vector3f movingDirection = new Vector3f();
    private final boolean[] motionState = new boolean[6];
    private double lastX, lastY;
    private boolean setupLast = false;

    public EntityCameraController(Player player) {
        super(player);
    }

    @Override
    public void handleMotion(MotionType motionType, boolean state) {
        if (motionState[motionType.ordinal()] == state) {
            return;
        }
        this.motionState[motionType.ordinal()] = state;

        switch (motionType) {
            case FORWARD:
            case BACKWARD:
                if (motionState[MotionType.FORWARD.ordinal()] == motionState[MotionType.BACKWARD.ordinal()]) {
                    movingDirection.x = 0;
                } else if (motionState[MotionType.FORWARD.ordinal()]) {
                    movingDirection.x = 1;
                } else if (motionState[MotionType.BACKWARD.ordinal()]) {
                    movingDirection.x = -1;
                }
                break;
            case LEFT:
            case RIGHT:
                if (motionState[MotionType.LEFT.ordinal()] == motionState[MotionType.RIGHT.ordinal()]) {
                    movingDirection.z = 0;
                } else if (motionState[MotionType.LEFT.ordinal()]) {
                    movingDirection.z = -1;
                } else if (motionState[MotionType.RIGHT.ordinal()]) {
                    movingDirection.z = 1;
                }
                break;
            case UP:
            case DOWN:
                if (motionState[MotionType.UP.ordinal()] == motionState[MotionType.DOWN.ordinal()]) {
                    movingDirection.y = 0;
                } else if (motionState[MotionType.UP.ordinal()]) {
                    movingDirection.y = 1;
                } else if (motionState[MotionType.DOWN.ordinal()]) {
                    movingDirection.y = -1;
                }
                break;
        }
        updateMotion();
    }

    @Override
    public void handleCursorMove(double x, double y) {
        double yaw = (x - lastX) * SENSIBILITY;
        double pitch = (lastY - y) * SENSIBILITY;
        lastX = x;
        lastY = y;
        if (setupLast) {
            Vector3f rotation = getPlayer().getControlledEntity().getRotation();
            rotation.y += pitch;
            rotation.y = Math.min(89.0f, Math.max(-89.0f, rotation.y));
            rotation.x = Math2.loop(rotation.x + (float) yaw, 360);
            updateMotion();
        } else setupLast = true;
    }

    private void updateMotion() {
        Vector3f rotation = getPlayer().getControlledEntity().getRotation();
        if (movingDirection.lengthSquared() != 0) {
            movingDirection.normalize(getPlayer().getControlledEntity().getMotion()).mul(MOTION_FACTOR).rotateAxis((float) -Math.toRadians(rotation.x), 0, 1, 0);
        } else {
            getPlayer().getControlledEntity().getMotion().set(0);
        }
//                .rotateAxis(rotation.x, 0, 1, 0).rotateY(rotation.y);
    }
}
