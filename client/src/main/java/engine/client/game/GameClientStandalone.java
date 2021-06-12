package engine.client.game;

import engine.client.EngineClient;
import engine.client.player.ClientPlayer;
import engine.client.player.ClientServerPlayer;
import engine.entity.Entity;
import engine.event.game.GameTerminationEvent;
import engine.game.GameData;
import engine.game.GameServerFullAsync;
import engine.player.Player;
import engine.player.Profile;
import engine.server.network.*;
import engine.world.World;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class GameClientStandalone extends GameServerFullAsync implements GameClient {

    private final EngineClient engineClient;

    private final NetworkClient networkClient;
    private ClientServerPlayer clientPlayer;
    protected final Set<Player> players = new HashSet<>();

    public GameClientStandalone(EngineClient engineClient, Path storageBasePath, GameData data, NetworkServer networkServer) {
        super(engineClient, storageBasePath, data, networkServer, () -> null);
        this.engineClient = engineClient;
        networkClient = new NetworkClient();
        networkClient.runLocal(networkServer.runLocal());
    }

    public static GameClientStandalone create(EngineClient engineClient, Path storageBasePath, GameData data) {
        var nettyServer = new NetworkServer();
        nettyServer.prepareNetworkEventBus();
        return new GameClientStandalone(engineClient, storageBasePath, data, nettyServer);
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
        clientPlayer = new ClientServerPlayer(profile, networkClient.getHandler(), controlledEntity);
        players.add(clientPlayer);
        getNetworkServer().getHandlers().forEach(s -> s.setStatus(ConnectionStatus.GAMEPLAY, new ServerGameplayNetworkHandlerContext(clientPlayer)));
        var context = new ClientGameplayNetworkHandlerContext();
        context.setClient(networkClient);
        networkClient.getHandler().setStatus(ConnectionStatus.GAMEPLAY, context);
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
    protected void constructStage() {
        super.constructStage();
    }

    @Override
    protected void resourceStage() {
        super.resourceStage();
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

        getWorlds().forEach(World::tick);
        clientPlayer.tick();
        // TODO upload particle physics here
    }

    @Override
    protected void tryTerminate() {
        logger.info("Game terminating!");
        engine.getEventBus().post(new GameTerminationEvent.Pre(this));
        super.tryTerminate();
        if (networkClient.getHandler().isChannelOpen()) {
            networkClient.getHandler().closeChannel();
        }
        getNetworkServer().close();
        engine.getEventBus().post(new GameTerminationEvent.Post(this));
        logger.info("Game terminated.");
    }
}