package engine.client.player;

import engine.Platform;
import engine.client.input.controller.EntityController;
import engine.entity.Entity;
import engine.graphics.GraphicsManager;
import engine.graphics.display.callback.CursorCallback;
import engine.player.PlayerImpl;
import engine.player.Profile;
import engine.server.network.NetworkHandler;
import engine.server.network.packet.c2s.PacketPlayerMove;
import org.joml.Vector3d;

import javax.annotation.Nonnull;

public class ClientPlayerImpl extends PlayerImpl implements ClientPlayer {

    private EntityController entityController;

    private CursorCallback cursorCallback;

    public ClientPlayerImpl(Profile profile, Entity controlledEntity) {
        super(profile, controlledEntity);
    }

    public ClientPlayerImpl(Profile profile, NetworkHandler handler, Entity controlledEntity) {
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
