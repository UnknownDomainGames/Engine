package engine.client.input.controller;

import engine.client.player.ClientPlayer;
import engine.graphics.camera.Camera;
import engine.entity.Entity;
import engine.math.Math2;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class EntityCameraController implements EntityController {

    private static final float SENSIBILITY = 0.05f;
    private static final float MOTION_FACTOR = 0.2f;

    private final Vector3f movingDirection = new Vector3f();
    private final boolean[] motionState = new boolean[6];
    private double lastX, lastY;
    private boolean setupLast = false;

    private ClientPlayer player;
    private Entity entity;

    @Override
    public void setPlayer(ClientPlayer player, Entity entity) {
        this.player = player;
        this.entity = entity;
    }

    @Override
    public void updateCamera(Camera camera, float tpf) {
        Vector3dc position = entity.getPosition();
        Vector3fc motion = entity.getMotion();
        Vector3fc rotation = entity.getRotation();
        camera.look(new Vector3f((float) position.x() + motion.x() * tpf,
                        (float) position.y() + motion.y() * tpf,
                        (float) position.z() + motion.z() * tpf),
                new Vector3f((float) (Math.cos(Math.toRadians(rotation.y())) * Math.cos(Math.toRadians(-rotation.x()))),
                        (float) Math.sin(Math.toRadians(rotation.y())),
                        (float) (Math.cos(Math.toRadians(rotation.y())) * Math.sin(Math.toRadians(-rotation.x())))).normalize());
    }

    @Override
    public void onInputMove(MotionType motionType, boolean state) {
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
    public void onCursorMove(double x, double y) {
        double yaw = (lastX - x) * SENSIBILITY;
        double pitch = (lastY - y) * SENSIBILITY;
        lastX = x;
        lastY = y;
        if (setupLast) {
            Vector3f rotation = player.getControlledEntity().getRotation();
            rotation.y += pitch;
            rotation.y = Math.min(89.0f, Math.max(-89.0f, rotation.y));
            rotation.x = Math2.loop(rotation.x + (float) yaw, 360);
            updateMotion();
        } else setupLast = true;
    }

    private void updateMotion() {
        Vector3f rotation = entity.getRotation();
        if (movingDirection.lengthSquared() != 0) {
            movingDirection.normalize(entity.getMotion())
                    .mul(MOTION_FACTOR)
                    .rotateAxis((float) Math.toRadians(rotation.x), 0, 1, 0);
        } else {
            entity.getMotion().set(0);
        }
    }
}
