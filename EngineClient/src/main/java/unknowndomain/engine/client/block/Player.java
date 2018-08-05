package unknowndomain.engine.client.block;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Player implements Entity {
    private Camera camera;

    private MoveBehavior moveBehavior = new MoveBehavior();
    private Vector3f motion = new Vector3f(0, 0, 0);
    private Vector3f position = new Vector3f(0, 0, 0);

    public Player(Camera camera) {
        this.camera = camera;
    }

    public void onAction(int action, Phase phase) {
        moveBehavior.accept(this, action, phase);
    }

    public Vector3f getPosition() {
        return position;
    }

    @Override
    public UUID getUUid() {
        return null;
    }

    public Vector3f getMotion() {
        return motion;
    }

    public void jump() {
        motion.y = 1f;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        return null;
    }

    @Override
    public void tick() {
        camera.move(motion.x, motion.y, motion.z); // this should not be here...
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

        private Vector3f movingDirection = new Vector3f();
        private Phase[] phases = new Phase[]{
                Phase.STOP, Phase.STOP, Phase.STOP, Phase.STOP, Phase.STOP, Phase.STOP, Phase.STOP
        };

        public void accept(Player player, int action, Phase phase) {
            if (phases[action] == phase) return;
            this.phases[action] = phase;

            Vector3f movingDirection = this.movingDirection;
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
                        movingDirection.y = factor;
                    }
                    if (this.phases[SNEAKING].active) {
                        movingDirection.y = -factor;
                    }
                    break;
            }
            Vector3f f = player.camera.getFrontVector();
            f.y = 0;
            movingDirection.rotate(new Quaternionf().rotateTo(new Vector3f(0, 0, -1), f),
                    player.motion);
        }
    }
}
