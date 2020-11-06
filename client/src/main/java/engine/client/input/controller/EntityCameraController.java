package engine.client.input.controller;

import engine.block.component.ActivateBehavior;
import engine.block.component.ClickBehavior;
import engine.client.player.ClientPlayer;
import engine.entity.Entity;
import engine.entity.component.TwoHands;
import engine.event.block.BlockInteractEvent;
import engine.event.block.cause.BlockChangeCause;
import engine.event.block.cause.BlockInteractCause;
import engine.event.entity.EntityInteractEvent;
import engine.event.entity.cause.EntityInteractCause;
import engine.event.item.ItemInteractEvent;
import engine.event.item.cause.ItemInteractCause;
import engine.game.Game;
import engine.graphics.camera.Camera;
import engine.item.ItemStack;
import engine.item.component.ActivateBlockBehavior;
import engine.item.component.ActivateEntityBehavior;
import engine.item.component.ClickBlockBehavior;
import engine.item.component.ClickEntityBehavior;
import engine.math.Math2;
import engine.registry.Registries;
import engine.server.network.packet.c2s.PacketPlayerAction;
import engine.world.hit.BlockHitResult;
import engine.world.hit.EntityHitResult;
import engine.world.hit.HitResult;
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
                getDirectionFromYawPitch(rotation));
    }

    public Vector3f getDirectionFromYawPitch(Vector3fc rotation) {
        return new Vector3f((float) (Math.cos(Math.toRadians(rotation.y())) * Math.cos(Math.toRadians(-rotation.x()))),
                (float) Math.sin(Math.toRadians(rotation.y())),
                (float) (Math.cos(Math.toRadians(rotation.y())) * Math.sin(Math.toRadians(-rotation.x())))).normalize();
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
    public void onCursorMove(double x, double y, boolean cursorLock) {
        double yaw = (lastX - x) * SENSIBILITY;
        double pitch = (lastY - y) * SENSIBILITY;
        lastX = x;
        lastY = y;
        if(cursorLock) {
            if (setupLast) {
                Vector3f rotation = player.getControlledEntity().getRotation();
                rotation.y += pitch;
                rotation.y = Math.min(89.0f, Math.max(-89.0f, rotation.y));
                rotation.x = Math2.loop(rotation.x + (float) yaw, 360);
                updateMotion();
            } else {
                setupLast = true;
            }
        } else {
            setupLast = false;
        }
    }

    @Override
    public void onAttack() {
        Game game = player.getWorld().getGame();
        HitResult hitResult = player.getWorld().raycast(new Vector3f().set(entity.getPosition()), getDirectionFromYawPitch(entity.getRotation()), 10);
        if (hitResult.isFailure()) {
            var cause = new ItemInteractCause.PlayerCause(player);
            entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                    twoHands.getMainHand().ifNonEmpty(itemStack -> {
                        game.getEngine().getEventBus().post(new ItemInteractEvent.Click(itemStack, cause));
                        itemStack.getItem().getComponent(engine.item.component.ClickBehavior.class).ifPresent(clickBehavior ->
                                clickBehavior.onClicked(itemStack, cause));
                    }));
            return;
        }
        if (hitResult instanceof BlockHitResult) {
            var blockHitResult = (BlockHitResult) hitResult;
            var cause = new BlockInteractCause.PlayerCause(player);
            game.getEngine().getEventBus().post(new BlockInteractEvent.Click(blockHitResult, cause));
            blockHitResult.getBlock().getPrototype().getComponent(ClickBehavior.class).ifPresent(clickBehavior ->
                    clickBehavior.onClicked(blockHitResult, cause));
            entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                    twoHands.getMainHand().ifNonEmpty(itemStack ->
                            itemStack.getItem().getComponent(ClickBlockBehavior.class).ifPresent(clickBlockBehavior ->
                                    clickBlockBehavior.onClicked(itemStack, blockHitResult, cause))));
            player.getNetworkHandler().sendPacket(new PacketPlayerAction(PacketPlayerAction.Action.START_BREAK_BLOCK, blockHitResult));
            // TODO: Remove it
            player.getWorld().destroyBlock(blockHitResult.getPos(), new BlockChangeCause.PlayerCause(player));
        } else if (hitResult instanceof EntityHitResult) {
            var entityHitResult = (EntityHitResult) hitResult;
            var cause = new EntityInteractCause.PlayerCause(player);
            game.getEngine().getEventBus().post(new EntityInteractEvent.Click(entityHitResult, cause));
            entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                    twoHands.getMainHand().ifNonEmpty(itemStack ->
                            itemStack.getItem().getComponent(ClickEntityBehavior.class).ifPresent(clickBlockBehavior ->
                                    clickBlockBehavior.onClicked(itemStack, entityHitResult, cause))));
        }
    }

    @Override
    public void onInteract() {
        Game game = player.getWorld().getGame();
        HitResult hitResult = player.getWorld().raycast(new Vector3f().set(entity.getPosition()), getDirectionFromYawPitch(entity.getRotation()), 10);
        if (hitResult.isFailure()) {
            var cause = new ItemInteractCause.PlayerCause(player);
            entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                    twoHands.getMainHand().ifNonEmpty(itemStack -> {
                        game.getEngine().getEventBus().post(new ItemInteractEvent.Activate(itemStack, cause));
                        itemStack.getItem().getComponent(engine.item.component.ActivateBehavior.class).ifPresent(activateBehavior ->
                                activateBehavior.onActivate(itemStack, cause));
                    }));
            return;
        }
        if (hitResult instanceof BlockHitResult) {
            // TODO: sync holding item to server before doing anything
            var blockHitResult = (BlockHitResult) hitResult;
            var cause = new BlockInteractCause.PlayerCause(player);
            game.getEngine().getEventBus().post(new BlockInteractEvent.Activate(blockHitResult, cause));
            blockHitResult.getBlock().getPrototype().getComponent(ActivateBehavior.class).ifPresent(activateBehavior ->
                    activateBehavior.onActivated(blockHitResult, cause));
            entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                    twoHands.getMainHand().ifNonEmpty(itemStack ->
                            itemStack.getItem().getComponent(ActivateBlockBehavior.class).ifPresent(activateBlockBehavior ->
                                    activateBlockBehavior.onActivate(itemStack, blockHitResult, cause))));
            player.getNetworkHandler().sendPacket(new PacketPlayerAction(PacketPlayerAction.Action.INTERACT_BLOCK, blockHitResult));
        } else if (hitResult instanceof EntityHitResult) {
            var entityHitResult = (EntityHitResult) hitResult;
            var cause = new EntityInteractCause.PlayerCause(player);
            game.getEngine().getEventBus().post(new EntityInteractEvent.Activate(entityHitResult, cause));
            entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                    twoHands.getMainHand().ifNonEmpty(itemStack ->
                            itemStack.getItem().getComponent(ActivateEntityBehavior.class).ifPresent(activateEntityBehavior ->
                                    activateEntityBehavior.onActivate(itemStack, entityHitResult, cause))));
        }
    }

    @Override
    public void onPickBlock() {
        player.getWorld().raycastBlock(new Vector3f().set(entity.getPosition()), getDirectionFromYawPitch(entity.getRotation()), 10).ifSuccess(hit ->
                entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                        Registries.getItemRegistry().getBlockItem(hit.getBlock()).ifPresent(item ->
                                twoHands.setMainHand(new ItemStack(item)))));
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
