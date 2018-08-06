package unknowndomain.engine.client.block;

import com.google.common.collect.Lists;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.client.display.Camera;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Player implements unknowndomain.engine.entity.Player {
    private Camera camera;

    private MoveSystem moveSystem = new MoveSystem();
    private Vector3f motion = new Vector3f(0, 0, 0);
    private Vector3f position = new Vector3f(0, 0, 0);

    public Player(Camera camera) {
        this.camera = camera;
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

    public static class MoveSystem {
        public static final int RUNNING = 0, SNEAKING = 1,
                FORWARD = 2, BACKWARD = 3, LEFT = 4, RIGHT = 5,
                JUMPING = 6;

        private Vector3f movingDirection = new Vector3f();
        private boolean[] phases = new boolean[16];

        private void forwardAndBackward(int direction, boolean state) {
            if (phases[direction]) return;
            this.phases[direction] = state;

            float factor = 0.1f;
            if (this.phases[FORWARD] == this.phases[BACKWARD]) {
                movingDirection.z = 0;
            }
            if (this.phases[FORWARD]) movingDirection.z = -factor;
            else if (this.phases[BACKWARD]) movingDirection.z = +factor;
        }

        public List<Action> getActions() {
            return Lists.newArrayList(
                    Action.builder("player.move.forward").setStartHandler((c) -> accept(null, FORWARD, true))
                            .setEndHandler((c) -> accept(null, FORWARD, false)).build(),
                    Action.builder("player.move.backward").setStartHandler((c) -> accept(null, BACKWARD, true)).build(),
                    Action.builder("player.move.left").setStartHandler((c) -> accept(null, LEFT, true)).build(),
                    Action.builder("player.move.right").setStartHandler((c) -> accept(null, RIGHT, true)).build(),
                    Action.builder("player.move.jump").setStartHandler((c) -> accept(null, JUMPING, true)).build(),
                    Action.builder("player.move.sneak").setStartHandler((c) -> accept(null, BACKWARD, true)).build()
            );
        }

        public void accept(Player player, int action, boolean phase) {
            if (phases[action] == phase) return;
            this.phases[action] = phase;

            Vector3f movingDirection = this.movingDirection;
            float factor = 0.1f;
            switch (action) {
                case FORWARD:
                case BACKWARD:
                    if (this.phases[FORWARD] == this.phases[BACKWARD]) {
                        movingDirection.z = 0;
                    }
                    if (this.phases[FORWARD]) movingDirection.z = -factor;
                    else if (this.phases[BACKWARD]) movingDirection.z = +factor;
                    break;
                case LEFT:
                case RIGHT:
                    if (this.phases[LEFT] == this.phases[RIGHT]) {
                        movingDirection.x = 0;
                    }
                    if (this.phases[RIGHT]) movingDirection.x = factor;
                    else if (this.phases[LEFT]) movingDirection.x = -factor;
                    break;

                case RUNNING:
                    break;
                case JUMPING:
                case SNEAKING:
                    if (this.phases[JUMPING] && !this.phases[SNEAKING]) {
                        movingDirection.y = factor;
                    } else {
                        movingDirection.y = 0;
                    }
                    if (this.phases[JUMPING]) {
                        movingDirection.y = factor;
                    }
                    if (this.phases[SNEAKING]) {
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
