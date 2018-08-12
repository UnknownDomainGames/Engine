package unknowndomain.engine.client;

import com.google.common.collect.Lists;
import org.joml.AABBd;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.event.Cancellable;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.LogicWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class PlayerClient implements unknowndomain.engine.entity.Player {
    private Camera camera;
    private MoveSystem moveSystem = new MoveSystem();
    private Vector3f motion = new Vector3f(0, 0, 0);
    private AABBd boundingBox = new AABBd(-0.4, -0.5, -0.4, 0.4, 0.5, 0.4);
    //        private AABBd boundingBox = new AABBd(0, 0, 0, 1, 1, 1);
//    private AABBd boundingBox = new AABBd(-10, -10, -10, 10, 10, 10);
    private Item mainHand;

    public PlayerClient(Camera camera) {
        this.camera = camera;
    }

    public Item getMainHand() {
        return mainHand;
    }

    public void setMainHand(Item mainHand) {
        this.mainHand = mainHand;
    }

    public Vector3f getPosition() {
        return camera.getPosition();
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    public Vector3f getMotion() {
        return motion;
    }

    @Override
    public AABBd getBoundingBox() {
        return boundingBox;
    }

    public List<Action> getActions() {
        List<Action> list = moveSystem.getActions();
        list.addAll(Lists.newArrayList(
                Action.builder("player.debug.addZ").setStartHandler(c -> {
                    this.boundingBox.minZ += 0.05;
                    this.boundingBox.maxZ += 0.05;
                    System.out.println(this.boundingBox.minZ);
                }).build(),
                Action.builder("player.debug.subZ").setStartHandler(c -> {
                    this.boundingBox.minZ -= 0.05;
                    this.boundingBox.maxZ -= 0.05;
                    System.out.println(this.boundingBox.minZ);
                }).build(),
                Action.builder("player.debug.addX").setStartHandler(c -> {
                    this.boundingBox.minX += 0.05;
                    this.boundingBox.maxX += 0.05;
                    System.out.println(this.boundingBox.minX);
                }).build(),
                Action.builder("player.debug.subX").setStartHandler(c -> {
                    this.boundingBox.minX -= 0.05;
                    this.boundingBox.maxX -= 0.05;
                    System.out.println(this.boundingBox.minX);
                }).build(),
                Action.builder("player.mouse.right").setStartHandler((c) -> {
                    LogicWorld world = UnknownDomain.getEngine().getWorld();
                    BlockPrototype.Hit hit = world.rayHit(camera.getPosition(), camera.getFrontVector(), 5);
                    if (mainHand != null) {
                        if (hit != null) {
                            mainHand.onUseBlockStart(world, this, mainHand, hit);
                        } else {
                            mainHand.onUseStart(world, this, mainHand);
                        }
                    }
                    if (hit != null) {
                        if (hit.block.shouldActivated(world, this, hit.position, hit.block)) {
                            hit.block.onActivated(world, this, hit.position, hit.block);
                        }
                    }
                }).build(),
                Action.builder("player.mouse.left").setStartHandler((c) -> {
                    LogicWorld world = UnknownDomain.getEngine().getWorld();
                    BlockPrototype.Hit hit = world.rayHit(camera.getPosition(), camera.getFrontVector(), 5);
                    if (mainHand != null) {
                        if (hit != null) {
                            world.setBlock(hit.position, null);
//                            mainHand.onUseBlockStart(world, this, mainHand, hit);
                        } else {
//                            mainHand.onUseStart(world, this, mainHand);
                        }
                    }
                    if (hit != null) {
                        world.setBlock(hit.position, null);
//                        if (hit.block.shouldActivated(world, this, hit.position, hit.block)) {
//                            hit.block.onActivated(world, this, hit.position, hit.block);
//                        }
                    }
                }).build()
        ));
        return list;
    }

    public static class PlayerPlaceBlockEvent implements Event, Cancellable {
        public final PlayerClient player;
        public final BlockPos position;
        private boolean cancelled;

        public PlayerPlaceBlockEvent(PlayerClient player, BlockPos position) {
            this.player = player;
            this.position = position;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
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
//        UnknownDomain.getEngine().getWorld().getBlock();
//        camera.move(motion.x, motion.y, motion.z); // this should not be here...
    }

    public static class MoveSystem {
        public static final int RUNNING = 0, SNEAKING = 1,
                FORWARD = 2, BACKWARD = 3, LEFT = 4, RIGHT = 5,
                JUMPING = 6;

        private Vector3f movingDirection = new Vector3f();
        private boolean[] phases = new boolean[16];

        public List<Action> getActions() {
            return Lists.newArrayList(
                    Action.builder("player.move.forward").setStartHandler((c) -> accept(UnknownDomain.getEngine().getPlayer(), FORWARD, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getPlayer(), FORWARD, false)).build(),
                    Action.builder("player.move.backward").setStartHandler((c) -> accept(UnknownDomain.getEngine().getPlayer(), BACKWARD, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getPlayer(), BACKWARD, false)).build(),
                    Action.builder("player.move.left").setStartHandler((c) -> accept(UnknownDomain.getEngine().getPlayer(), LEFT, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getPlayer(), LEFT, false)).build(),
                    Action.builder("player.move.right").setStartHandler((c) -> accept(UnknownDomain.getEngine().getPlayer(), RIGHT, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getPlayer(), RIGHT, false)).build(),
                    Action.builder("player.move.jump").setStartHandler((c) -> accept(UnknownDomain.getEngine().getPlayer(), JUMPING, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getPlayer(), JUMPING, false)).build(),
                    Action.builder("player.move.sneak").setStartHandler((c) -> accept(UnknownDomain.getEngine().getPlayer(), SNEAKING, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getPlayer(), SNEAKING, false)).build()
            );
        }

        private void accept(PlayerClient player, int action, boolean phase) {
            if (phases[action] == phase) return;
            this.phases[action] = phase;

            Vector3f movingDirection = this.movingDirection;
            float factor = 0.07f;
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
                    } else if (this.phases[SNEAKING] && !this.phases[JUMPING]) {
                        movingDirection.y = -factor;
                    } else {
                        movingDirection.y = 0;
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
