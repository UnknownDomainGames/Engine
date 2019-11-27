package nullengine.client.player;

import nullengine.Platform;
import nullengine.client.input.controller.EntityController;
import nullengine.client.rendering.display.callback.CursorCallback;
import nullengine.entity.Entity;
import nullengine.player.PlayerImpl;
import nullengine.player.Profile;
import nullengine.server.network.NetworkHandler;

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

    @Override
    public EntityController getEntityController() {
        return entityController;
    }

    @Override
    public void setEntityController(EntityController controller) {
        if (entityController == controller) {
            return;
        }
        entityController = controller;
        Platform.getEngineClient().getRenderManager().getWindow().removeCursorCallback(cursorCallback);
        cursorCallback = (window, xpos, ypos) -> entityController.handleCursorMove(xpos, ypos);
        Platform.getEngineClient().getRenderManager().getWindow().addCursorCallback(cursorCallback);
    }

    @Nonnull
    @Override
    public Entity controlEntity(@Nonnull Entity entity) {
        return super.controlEntity(entity);
    }


}
