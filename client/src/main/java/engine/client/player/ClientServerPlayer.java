package engine.client.player;

import engine.Platform;
import engine.client.input.controller.EntityController;
import engine.entity.Entity;
import engine.graphics.GraphicsManager;
import engine.graphics.display.callback.CursorCallback;
import engine.player.Profile;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.NetworkHandler;
import engine.server.network.packet.c2s.PacketPlayerMove;
import engine.server.network.packet.s2c.PacketPlayerPosView;
import engine.server.player.ServerPlayer;
import org.joml.Vector3d;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public class ClientServerPlayer extends ServerPlayer implements ClientPlayer {

    private EntityController entityController;

    private CursorCallback cursorCallback;

    public ClientServerPlayer(Profile profile, Entity controlledEntity) {
        super(profile, controlledEntity);
    }

    public ClientServerPlayer(Profile profile, NetworkHandler handler, Entity controlledEntity) {
        super(profile, handler, controlledEntity);
    }

    private int tickSinceLastPositionSent = 0;
    public double lastX;
    public double lastY;
    public double lastZ;
    public float lastYaw;
    public float lastPitch;


    @Override
    public void tick() {
        super.tick();
        if (getControlledEntity() != null) {
            var dx = getControlledEntity().getPosition().x - lastX;
            var dy = getControlledEntity().getPosition().y - lastY;
            var dz = getControlledEntity().getPosition().z - lastZ;
            var dYaw = getControlledEntity().getRotation().x - lastYaw;
            var dPitch = getControlledEntity().getRotation().y - lastPitch;
            ++tickSinceLastPositionSent;
            boolean shouldUpdatePos = dx * dx + dy * dy + dz * dz >= 0.03 * 0.03 || tickSinceLastPositionSent >= 20;
            boolean shouldUpdateView = dYaw * dYaw > 0 || dPitch * dPitch > 0;
            //TODO: onGround status cannot be determined until physics system is implemented
            if (shouldUpdatePos && shouldUpdateView) {
                getNetworkHandler().sendPacket(PacketPlayerMove.update(getControlledEntity().getPosition(), new Vector3d(lastX, lastY, lastZ), getControlledEntity().getRotation(), false /*TODO*/));
            } else if (shouldUpdatePos) {
                getNetworkHandler().sendPacket(PacketPlayerMove.updatePosition(getControlledEntity().getPosition(), new Vector3d(lastX, lastY, lastZ), false));
            } else if (shouldUpdateView) {
                getNetworkHandler().sendPacket(PacketPlayerMove.updateLookAt(getControlledEntity().getRotation(), false));
            }

            if (shouldUpdatePos) {
                lastX = getControlledEntity().getPosition().x;
                lastY = getControlledEntity().getPosition().y;
                lastZ = getControlledEntity().getPosition().z;
                tickSinceLastPositionSent = 0;
            }

            if (shouldUpdateView) {
                lastYaw = getControlledEntity().getRotation().x;
                lastPitch = getControlledEntity().getRotation().y;
            }
        }
    }

    @Override
    public void handlePosViewSync(PacketReceivedEvent event) {
        if (!(event.getPacket() instanceof PacketPlayerPosView)) {
            return;
        }
        var packet = ((PacketPlayerPosView) event.getPacket());
        var motion = this.getControlledEntity().getMotion().get(new Vector3f());
        var pos = this.getControlledEntity().getPosition().get(new Vector3d());
        if (packet.isPosXRelative()) {
            pos.add(packet.getPosX(), 0, 0);
        } else {
            motion.x = 0;
            pos.x = packet.getPosX();
        }
        if (packet.isPosYRelative()) {
            pos.add(0, packet.getPosY(), 0);
        } else {
            motion.y = 0;
            pos.y = packet.getPosY();
        }
        if (packet.isPosZRelative()) {
            pos.add(0, 0, packet.getPosZ());
        } else {
            motion.z = 0;
            pos.z = packet.getPosZ();
        }

        this.getControlledEntity().getPosition().set(pos);
        this.getControlledEntity().getMotion().set(motion);
        this.lastX = pos.x;
        this.lastY = pos.y;
        this.lastZ = pos.z;
        var rotation = this.getControlledEntity().getRotation().get(new Vector3f());
        if (packet.isYawRelative()) {
            rotation.add(packet.getYaw(), 0, 0);
        } else {
            rotation.x = packet.getYaw();
        }
        if (packet.isPitchRelative()) {
            rotation.add(0, packet.getPitch(), 0);
        } else {
            rotation.y = packet.getPitch();
        }
        this.getControlledEntity().getRotation().set(rotation);
        this.lastYaw = rotation.x;
        this.lastPitch = rotation.y;
        event.getHandler().sendPacket(packet.reply());
        event.getHandler().sendPacket(PacketPlayerMove.update(pos, pos, rotation, false));
    }

    @Override
    public EntityController getEntityController() {
        return entityController;
    }

    @Override
    public void setEntityController(EntityController controller) {
        entityController = controller;
        controller.setPlayer(this, getControlledEntity());
        GraphicsManager graphicsManager = Platform.getEngineClient().getGraphicsManager();
        graphicsManager.getWindow().removeCursorCallback(cursorCallback);
        cursorCallback = (window, xpos, ypos) -> entityController.onCursorMove(xpos, ypos, !graphicsManager.getGUIManager().isShowing());
        graphicsManager.getWindow().addCursorCallback(cursorCallback);
    }

    @Nonnull
    @Override
    public Entity controlEntity(@Nonnull Entity entity) {
        return super.controlEntity(entity);
    }

}
