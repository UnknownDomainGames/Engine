package engine.client.player;

import engine.Platform;
import engine.client.input.controller.EntityController;
import engine.entity.Entity;
import engine.graphics.RenderManager;
import engine.graphics.display.callback.CursorCallback;
import engine.player.PlayerImpl;
import engine.player.Profile;
import engine.server.network.NetworkHandler;

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
        entityController = controller;
        controller.setPlayer(this, getControlledEntity());
        RenderManager renderManager = Platform.getEngineClient().getRenderManager();
        renderManager.getWindow().removeCursorCallback(cursorCallback);
        cursorCallback = (window, xpos, ypos) -> {
            if (!renderManager.getGUIManager().isShowing()) {
                entityController.onCursorMove(xpos, ypos);
            }
        };
        renderManager.getWindow().addCursorCallback(cursorCallback);
    }

    @Nonnull
    @Override
    public Entity controlEntity(@Nonnull Entity entity) {
        return super.controlEntity(entity);
    }


}
