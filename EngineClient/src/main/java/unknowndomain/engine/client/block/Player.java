package unknowndomain.engine.client.block;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import unknowndomain.engine.client.display.Camera;

public class Player {
    private Camera camera;
    private MoveBehavior moveBehavior = new MoveBehavior();
    private Vector3f movingDirection = new Vector3f();
    private Vector3f movingDelta = new Vector3f(0, 0, 0);

    public Player(Camera camera) {
        this.camera = camera;
    }

    public void onAction(int action, Phase phase) {
        moveBehavior.accept(this, action, phase);
    }

    public Vector3f getPosition() {
        return camera.getPosition();
    }

    public Vector3f getMovingDelta() {
        return movingDelta;
    }

    public void jump() {
        movingDelta.y = 1f;
    }

    public void update() {
        camera.move(movingDelta.x, movingDelta.y, movingDelta.z);
        if (movingDelta.y > 0) movingDelta.y -= 0.01f;
        else if (movingDelta.y < 0) movingDelta.y += 0.01f;
        if (Math.abs(movingDelta.y) <= 0.01f) movingDelta.y = 0;
    }

    public enum Phase {
        START(true), KEEP(true), STOP(false);

        public final boolean active;

        Phase(boolean active) {
            this.active = active;
        }
    }

    public static class MoveBehavior {
        public static final int RUNNING = 0, SNEAKING = 1,
                FORWARD = 2, BACKWARD = 3, LEFT = 4, RIGHT = 5,
                JUMPING = 6;

        private Phase[] phases = new Phase[]{
                Phase.STOP, Phase.STOP, Phase.STOP, Phase.STOP, Phase.STOP, Phase.STOP, Phase.STOP
        };

        public void accept(Player player, int action, Phase phase) {
            if (phases[action] == phase) return;
            this.phases[action] = phase;

            Vector3f movingDirection = player.movingDirection;
            float factor = 0.1f;
            switch (action) {
                case FORWARD:
                case BACKWARD:
                    if (this.phases[FORWARD].active == this.phases[BACKWARD].active) {
                        movingDirection.z = 0;
                    }
                    if (this.phases[FORWARD].active) movingDirection.z = -factor;
                    else if (this.phases[BACKWARD].active) movingDirection.z = +factor;
                    break;
                case LEFT:
                case RIGHT:
                    if (this.phases[LEFT].active == this.phases[RIGHT].active) {
                        movingDirection.x = 0;
                    }
                    if (this.phases[RIGHT].active) movingDirection.x = factor;
                    else if (this.phases[LEFT].active) movingDirection.x = -factor;
                    break;

                case RUNNING:
                    break;
                case JUMPING:
                case SNEAKING:
                    if (this.phases[JUMPING].active && !this.phases[SNEAKING].active) {
                        movingDirection.y = factor;
                    } else {
                        movingDirection.y = 0;
                    }
                    if (this.phases[JUMPING].active) {
                        movingDirection.y = -factor;
                    }
                    break;
            }
            Vector3f f = player.camera.getFrontVector();
            f.y = 0;
            movingDirection.rotate(new Quaternionf().rotateTo(new Vector3f(0, 0, -1), f),
                    player.movingDelta);
        }
    }
}
