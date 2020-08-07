package engine.client;

import engine.EngineBase;
import engine.client.game.GameClient;
import engine.event.engine.EngineEvent;
import engine.game.Game;
import engine.game.GameData;
import engine.game.GameServerFullAsync;
import engine.logic.Ticker;
import engine.mod.impl.EngineModManager;
import engine.server.EngineServer;
import engine.server.ServerConfig;
import engine.server.network.NetworkServer;
import engine.util.CrashHandlerImpl;
import engine.util.Side;

import java.nio.file.Path;
import java.util.Objects;

public class EngineServerIntegrated extends EngineBase implements EngineServer {
    private final EngineClient parent;
    private Thread serverThread;
    private Ticker ticker;
    private NetworkServer nettyServer;
    private ServerConfig serverConfig;
    private Game game;

    public EngineServerIntegrated(EngineClient parent, ServerConfig serverConfig) {
        super(parent.getRunPath());
        this.parent = parent;
        this.serverConfig = serverConfig;
    }

    @Override
    public Thread getServerThread() {
        return serverThread;
    }

    @Override
    public boolean isServerThread() {
        return Thread.currentThread() == serverThread;
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    protected void constructionStage() {
        logger.info("Initializing engine!");

        crashHandler = (CrashHandlerImpl) parent.getCrashHandler();

        eventBus = parent.getEventBus();
        registryManager = parent.getRegistryManager();
        modManager = (EngineModManager) parent.getModManager();

        serverThread = Thread.currentThread();
    }

    @Override
    protected void modStage() {

    }

    @Override
    protected void finishStage() {
        ticker = new Ticker(this::serverTick, partial -> {
        }, 20);
    }

    @Override
    public void runEngine() {
        super.runEngine();

        addShutdownListener(ticker::stop);

        logger.info("Finishing initialization!");
        eventBus.post(new EngineEvent.Ready(this));
        nettyServer = new NetworkServer();
        nettyServer.prepareNetworkEventBus();
        // NOTE: delay server binding to client connection
        logger.info("Starting game for world");
        Path gameBasePath = this.getRunPath().resolve("game");
        startGame(new GameServerFullAsync(this, gameBasePath, GameData.createFromCurrentEnvironment(gameBasePath, serverConfig.getGame()), nettyServer));
        ticker.run();
    }

    @Override
    public Game getCurrentLogicGame() {
        return game;
    }

    @Override
    public GameClient getCurrentClientGame() {
        return parent != null ? parent.getCurrentClientGame() : null;
    }

    @Override
    public void startGame(Game game) {
        if (isPlaying()) {
            throw new IllegalStateException("Game is running");
        }
        this.game = Objects.requireNonNull(game);
        game.init();
    }

    public void serverTick() {
        if (isMarkedTermination()) {
            if (isPlaying()) {
                game.terminate();
            } else {
                tryTerminate();
            }
        }
        nettyServer.tick();
        ((GameServerFullAsync) game).tick();
    }

    private void tryTerminate() {
        logger.info("Engine terminating!");
        if (isPlaying()) {
            game.terminate();
        }

        eventBus.post(new EngineEvent.PreTermination(this));

        ticker.stop();
        shutdownListeners.forEach(Runnable::run);
        logger.info("Engine terminated!");
    }

    public NetworkServer getNettyServer() {
        return nettyServer;
    }

    @Override
    public boolean isPlaying() {
        return game != null && game.isReadyToPlay() && !game.isTerminated();
    }
}
