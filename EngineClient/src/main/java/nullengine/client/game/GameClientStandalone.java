package nullengine.client.game;

import nullengine.client.EngineClient;
import nullengine.client.input.controller.EntityController;
import nullengine.client.rendering.display.Window;
import nullengine.entity.Entity;
import nullengine.event.game.GameTerminationEvent;
import nullengine.game.GameData;
import nullengine.game.GameServerFullAsync;
import nullengine.player.Player;
import nullengine.player.Profile;
import nullengine.world.World;
import nullengine.world.WorldCommon;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public class GameClientStandalone extends GameServerFullAsync implements GameClient {

    private final EngineClient engineClient;

    private Player clientPlayer;
    private EntityController entityController;
    private Window.CursorCallback cursorCallback;

    public GameClientStandalone(EngineClient engineClient, Path storagePath, GameData data) {
        super(engineClient, storagePath, data);
        this.engineClient = engineClient;
    }

    @Nonnull
    @Override
    public EngineClient getEngine() {
        return engineClient;
    }

    @Nonnull
    @Override
    public Player joinPlayer(Profile profile, Entity controlledEntity) {
        if (clientPlayer != null) {
            throw new IllegalStateException("Cannot join player twice on client game");
        }
        clientPlayer = super.joinPlayer(profile, controlledEntity);
        return clientPlayer;
    }

    /**
     * Get player client
     */
    @Nonnull
    @Override
    public Player getClientPlayer() {
        if (clientPlayer != null) {
            return clientPlayer;
        }
        throw new IllegalStateException("The player hasn't initialize");
    }

    /**
     * @return the client world
     */
    @Nonnull
    @Override
    public World getClientWorld() {
        if (clientPlayer != null) {
            return clientPlayer.getWorld();
        }
        throw new IllegalStateException("The world hasn't initialize");
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
        getEngine().getRenderManager().getWindow().removeCursorCallback(cursorCallback);
        cursorCallback = (window, xpos, ypos) -> entityController.handleCursorMove(xpos, ypos);
        getEngine().getRenderManager().getWindow().addCursorCallback(cursorCallback);
    }

    @Override
    protected void constructStage() {
        super.constructStage();
    }

    @Override
    protected void resourceStage() {
        super.resourceStage();

        engineClient.getAssetManager().reload();
    }

    @Override
    protected void finishStage() {
        logger.info("Finishing Game Initialization!");

        super.finishStage();
        logger.info("Game Ready!");
    }

    public void clientTick() {
        if (isMarkedTermination()) {
            tryTerminate();
        }

        getWorlds().forEach(world -> ((WorldCommon) world).tick());
        // TODO upload particle physics here
    }

    @Override
    protected void tryTerminate() {
        logger.info("Game terminating!");
        engine.getEventBus().post(new GameTerminationEvent.Pre(this));
        super.tryTerminate();
        engine.getEventBus().post(new GameTerminationEvent.Post(this));
        logger.info("Game terminated.");
    }
}