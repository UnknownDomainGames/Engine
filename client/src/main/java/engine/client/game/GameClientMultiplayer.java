package engine.client.game;

import com.google.common.base.Strings;
import engine.client.EngineClient;
import engine.client.player.ClientPlayer;
import engine.client.player.ClientPlayerImpl;
import engine.client.world.WorldClient;
import engine.entity.Entity;
import engine.event.game.GameTerminationEvent;
import engine.event.world.WorldLoadEvent;
import engine.game.GameBase;
import engine.game.GameData;
import engine.player.Player;
import engine.player.Profile;
import engine.registry.Registries;
import engine.server.network.NetworkClient;
import engine.world.World;
import engine.world.WorldCreationSetting;
import engine.world.exception.WorldAlreadyLoadedException;
import engine.world.exception.WorldLoadException;
import engine.world.exception.WorldNotExistsException;
import engine.world.exception.WorldProviderNotFoundException;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.*;

public class GameClientMultiplayer extends GameBase implements GameClient {
    private ClientPlayer clientPlayer;
    protected final Set<Player> players = new HashSet<>();
    protected final Map<String, World> worlds = new HashMap<>();
    private final EngineClient engineClient;
    private final NetworkClient networkClient;

    public GameClientMultiplayer(EngineClient engine, NetworkClient networkClient, GameData data) {
        super(engine, Path.of(""), data);
        this.engineClient = engine;
        this.networkClient = networkClient;
    }

    @Nonnull
    @Override
    public EngineClient getEngine() {
        return engineClient;
    }

    @Override
    protected void finishStage() {
        logger.info("Finishing Game Initialization!");
        super.finishStage();
        data.getWorlds().forEach((name, name2) -> loadWorld(name));
        markReady();
        logger.info("Game Ready!");
    }

    @Nonnull
    @Override
    public Player joinPlayer(Profile profile, Entity controlledEntity) {
        if (clientPlayer != null) {
            throw new IllegalStateException("Cannot join player twice on client game");
        }
        clientPlayer = new ClientPlayerImpl(profile, networkClient.getHandler(), controlledEntity);
        return clientPlayer;
    }

    @Nonnull
    @Override
    public Collection<Player> getPlayers() {
        return players;
    }

    @Nonnull
    @Override
    public World createWorld(@Nonnull String providerName, @Nonnull String name, @Nonnull WorldCreationSetting config) {
        return null;
    }

    @Nonnull
    @Override
    public World loadWorld(@Nonnull String name) throws WorldLoadException, WorldNotExistsException {
        Validate.notEmpty(name);
        if (worlds.containsKey(name)) {
            throw new WorldAlreadyLoadedException(name);
        }

        String providerName = data.getWorlds().get(name);
        if (Strings.isNullOrEmpty(providerName)) {
            throw new WorldNotExistsException(name);
        }

        var provider = Registries.getWorldProviderRegistry().getValue(providerName);
        if (provider == null) {
            throw new WorldProviderNotFoundException(providerName);
        }

        var world = new WorldClient(this, provider, name);
        this.worlds.put(name, world);
        getEventBus().post(new WorldLoadEvent(world));
        return world;
    }

    @Override
    public void unloadWorld(@Nonnull String name) {
        getWorld(name).ifPresent(World::unload);
    }

    @Override
    public Optional<World> getWorld(@Nonnull String name) {
        return Optional.ofNullable(worlds.get(name));
    }

    @Override
    public void doUnloadWorld(World world) {

    }

    @Nonnull
    @Override
    public ClientPlayer getClientPlayer() {
        return clientPlayer;
    }

    @Nonnull
    @Override
    public World getClientWorld() {
        if (clientPlayer != null) {
            return clientPlayer.getWorld();
        }
        throw new IllegalStateException("The world hasn't initialize");
    }

    @Override
    public void clientTick() {
        if (isMarkedTermination()) {
            tryTerminate();
        }
        networkClient.tick();
        worlds.values().forEach(world -> ((WorldClient) world).tick());
        if (clientPlayer != null) {
            clientPlayer.tick();
        }
    }

    @Override
    protected void tryTerminate() {
        logger.info("Game terminating!");
        engine.getEventBus().post(new GameTerminationEvent.Pre(this));
        super.tryTerminate();
        if (networkClient.getHandler().isChannelOpen()) {
            networkClient.getHandler().closeChannel();
            networkClient.close();
        }
        engine.getEventBus().post(new GameTerminationEvent.Post(this));
        logger.info("Game terminated.");
    }
}
