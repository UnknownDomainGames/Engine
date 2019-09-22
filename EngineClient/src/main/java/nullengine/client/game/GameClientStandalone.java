package nullengine.client.game;

import nullengine.client.EngineClient;
import nullengine.client.input.controller.EntityController;
import nullengine.event.game.GameTerminationEvent;
import nullengine.game.GameServerFullAsync;
import nullengine.player.Player;
import nullengine.world.World;
import nullengine.world.WorldCommon;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public class GameClientStandalone extends GameServerFullAsync implements GameClient {

    private final EngineClient engineClient;
    private final Player player;

    private EntityController entityController;

    public GameClientStandalone(EngineClient engineClient, Path storagePath, Player player) {
        super(engineClient, storagePath);
        this.engineClient = engineClient;
        this.player = player;
    }

    @Nonnull
    @Override
    public EngineClient getEngine() {
        return engineClient;
    }

    /**
     * Get player client
     */
    @Nonnull
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the client world
     */
    @Nonnull
    @Override
    public World getWorld() {
        if (player != null && player.getControlledEntity() != null) {
            return player.getControlledEntity().getWorld();
        }
        throw new IllegalStateException("The world hasn't initialize");
    }

    @Override
    public EntityController getEntityController() {
        return entityController;
    }

    @Override
    public void setEntityController(EntityController controller) {
        this.entityController = controller;
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

        ((WorldCommon) getWorld()).tick();
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