package engine.client;

import engine.client.game.GameClient;
import engine.event.EventBus;
import engine.event.engine.EngineEvent;
import engine.game.Game;
import engine.game.GameData;
import engine.game.GameServerFullAsync;
import engine.logic.Ticker;
import engine.mod.ModManager;
import engine.registry.RegistryManager;
import engine.server.EngineServer;
import engine.server.ServerConfig;
import engine.server.network.NetworkServer;
import engine.util.CrashHandler;
import engine.util.RuntimeEnvironment;
import engine.util.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class EngineServerIntegrated implements EngineServer {

    private final Logger logger = LoggerFactory.getLogger("Integrated Server");
    protected final List<Runnable> shutdownListeners = new LinkedList<>();
    private final EngineClient parent;
    private Thread serverThread;
    private Ticker ticker;
    private NetworkServer nettyServer;
    private ServerConfig serverConfig;
    private Game game;
    private boolean initialized;
    private boolean running;
    private boolean markedTermination;

    public EngineServerIntegrated(EngineClient parent, ServerConfig serverConfig) {
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
    public Path getRunPath() {
        return parent.getRunPath();
    }

    @Override
    public Logger getLogger() {
        return logger; //TODO: redirect to EngineClient's?
    }

    @Override
    public RuntimeEnvironment getRuntimeEnvironment() {
        return parent.getRuntimeEnvironment();
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public EventBus getEventBus() {
        return parent.getEventBus();
    }

    @Override
    public RegistryManager getRegistryManager() {
        return parent.getRegistryManager();
    }

    @Override
    public ModManager getModManager() {
        return parent.getModManager();
    }

    @Override
    public CrashHandler getCrashHandler() {
        return parent.getCrashHandler();
    }

    @Override
    public void initEngine() {
        if (initialized) {
            throw new IllegalStateException("Engine has been initialized.");
        }
        initialized = true;
        constructionStage();
        modStage();
        finishStage();
    }


    protected void constructionStage() {
        logger.info("Initializing engine!");

        serverThread = Thread.currentThread();
    }


    protected void modStage() {

    }


    protected void finishStage() {
        ticker = new Ticker(this::serverTick, partial -> {
        }, 20);
    }

    @Override
    public void runEngine() {
        if (running) {
            throw new IllegalStateException("Engine is running.");
        }
        running = true;

        addShutdownListener(ticker::stop);

        logger.info("Finishing initialization!");
        getEventBus().post(new EngineEvent.Ready(this));
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

        getEventBus().post(new EngineEvent.PreTermination(this));

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

    @Override
    public void terminate() {
        if (markedTermination) {
            return;
        }

        markedTermination = true;
        getEventBus().post(new EngineEvent.MarkedTermination(this));
        logger.info("Marked engine terminated!");
    }

    @Override
    public boolean isMarkedTermination() {
        return markedTermination;
    }

    @Override
    public void addShutdownListener(Runnable runnable) {
        shutdownListeners.add(runnable);
    }
}
