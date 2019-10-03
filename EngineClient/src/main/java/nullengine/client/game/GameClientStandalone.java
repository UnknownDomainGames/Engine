package nullengine.client.game;

import nullengine.client.EngineClient;
import nullengine.client.player.ClientPlayer;
import nullengine.client.player.ClientPlayerImpl;
import nullengine.entity.Entity;
import nullengine.event.game.GameTerminationEvent;
import nullengine.game.GameData;
import nullengine.game.GameServerFullAsync;
import nullengine.player.Profile;
import nullengine.world.World;
import nullengine.world.WorldCommon;
import nullengine.world.hit.HitResult;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public class GameClientStandalone extends GameServerFullAsync implements GameClient {

    private final EngineClient engineClient;

    private ClientPlayer clientPlayer;


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
    public ClientPlayer joinPlayer(Profile profile, Entity controlledEntity) {
        if (clientPlayer != null) {
            throw new IllegalStateException("Cannot join player twice on client game");
        }
        clientPlayer = new ClientPlayerImpl(profile, controlledEntity);
        players.add(clientPlayer);
        return clientPlayer;
    }

    /**
     * Get player client
     */
    @Nonnull
    @Override
    public ClientPlayer getClientPlayer() {
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
    public HitResult getHitResult() {
        return null;
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