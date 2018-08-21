package unknowndomain.engine.client.player;

import com.google.common.collect.Lists;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.action.ActionBuilderImpl;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.world.World;

import java.util.List;

public class FirstPersonController extends PlayerController {
    private MoveSystem moveSystem = new MoveSystem();

    @Override
    public void tick() {
        super.tick();
        camera.getPosition().set(player.getMountingEntity().getPosition());
    }

    @Override
    public List<Action> getActions() {
        List<Action> list = moveSystem.getActions();
        list.addAll(Lists.newArrayList(
                ActionBuilderImpl.create("player.mouse.right").setStartHandler((c) -> {
                    Player player = getPlayer();

                    if (player == null) return;
                    Entity mountingEntity = player.getMountingEntity();
                    World world = player.getWorld();

                    if (mountingEntity == null || world == null) return;

                    Entity.TwoHands twoHands = mountingEntity.getBehavior(Entity.TwoHands.class);
                    if (twoHands == null) return;

                    BlockPrototype.Hit hit = world.raycast(camera.getPosition(), camera.getFrontVector(), 5);

                    Item hand = twoHands.getMainHand();
                    if (hand != null) {
                        if (hit != null) {
                            hand.onUseBlockStart(world, mountingEntity, hand, hit);
                        } else {
                            hand.onUseStart(world, mountingEntity, hand);
                        }
                    }
                    if (hit != null) {
                        if (hit.block.shouldActivated(world, mountingEntity, hit.position, hit.block)) {
                            hit.block.onActivated(world, mountingEntity, hit.position, hit.block);
                        }
                    }
                }).build(),
                ActionBuilderImpl.create("player.mouse.left").setStartHandler((c) -> {
                    Player player = getPlayer();

                    if (player == null) return;
                    Entity mountingEntity = player.getMountingEntity();
                    World world = player.getWorld();

                    if (mountingEntity == null || world == null) return;

                    Entity.TwoHands twoHands = mountingEntity.getBehavior(Entity.TwoHands.class);
                    if (twoHands == null) return;

                    BlockPrototype.Hit hit = world.raycast(camera.getPosition(), camera.getFrontVector(), 5);
                    if (twoHands.getMainHand() != null) {
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

    public static class MoveSystem {
        public static final int RUNNING = 0, SNEAKING = 1,
                FORWARD = 2, BACKWARD = 3, LEFT = 4, RIGHT = 5,
                JUMPING = 6;
        float factor = 0.1f;
        private Vector3f movingDirection = new Vector3f();
        private boolean[] phases = new boolean[16];

        public List<Action> getActions() {
            return Lists.newArrayList(
                    ActionBuilderImpl.create("player.move.forward").setStartHandler((c) -> accept(UnknownDomain.getEngine().getController(), FORWARD, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getController(), FORWARD, false)).build(),
                    ActionBuilderImpl.create("player.move.backward").setStartHandler((c) -> accept(UnknownDomain.getEngine().getController(), BACKWARD, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getController(), BACKWARD, false)).build(),
                    ActionBuilderImpl.create("player.move.left").setStartHandler((c) -> accept(UnknownDomain.getEngine().getController(), LEFT, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getController(), LEFT, false)).build(),
                    ActionBuilderImpl.create("player.move.right").setStartHandler((c) -> accept(UnknownDomain.getEngine().getController(), RIGHT, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getController(), RIGHT, false)).build(),
                    ActionBuilderImpl.create("player.move.jump").setStartHandler((c) -> accept(UnknownDomain.getEngine().getController(), JUMPING, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getController(), JUMPING, false)).build(),
                    ActionBuilderImpl.create("player.move.sneak").setStartHandler((c) -> accept(UnknownDomain.getEngine().getController(), SNEAKING, true))
                            .setEndHandler((c, i) -> accept(UnknownDomain.getEngine().getController(), SNEAKING, false)).build()
            );
        }

        private void accept(PlayerController player, int action, boolean phase) {
            if (phases[action] == phase) return;
            this.phases[action] = phase;

            Vector3f movingDirection = this.movingDirection;
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
            Camera camera = player.getCamera();
            Vector3f f = camera.getFrontVector();
            f.y = 0;
            movingDirection.rotate(new Quaternionf().rotateTo(new Vector3f(0, 0, -1), f),
                    player.getPlayer().getMountingEntity().getMotion());
        }
    }
}
