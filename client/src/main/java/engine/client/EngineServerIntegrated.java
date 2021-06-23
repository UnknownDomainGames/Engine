package engine.client;

import engine.Platform;
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
import engine.util.CrashHandlerImpl;
import engine.util.RuntimeEnvironment;
import engine.util.Side;
import engine.world.chunk.ChunkStatusListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.apache.commons.lang3.SystemUtils.*;

public class EngineServerIntegrated implements EngineServer {

    private final Logger logger = LoggerFactory.getLogger("Integrated Server");
    protected final List<Runnable> shutdownListeners = new LinkedList<>();
    private final EngineClient parent;
    private Thread serverThread;
    private Ticker ticker;
    private NetworkServer nettyServer;
    private ServerConfig serverConfig;
    private final Supplier<ChunkStatusListener> chunkStatusListenerSupplier;
    private Game game;
    private GameData passThroughGameData;
    private boolean initialized;
    private boolean running;
    private boolean markedTermination;
    private boolean paused;
    private boolean terminated;
    private CrashHandlerImpl crashHandler;

    public EngineServerIntegrated(EngineClient parent, ServerConfig serverConfig, Supplier<ChunkStatusListener> chunkStatusListenerSupplier) {
        this.parent = parent;
        this.serverConfig = serverConfig;
        this.chunkStatusListenerSupplier = chunkStatusListenerSupplier;
    }

    public EngineServerIntegrated(EngineClient parent, ServerConfig serverConfig, GameData gameData, Supplier<ChunkStatusListener> chunkStatusListenerSupplier) {
        this(parent, serverConfig, chunkStatusListenerSupplier);
        this.passThroughGameData = gameData;
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
        return crashHandler;
    }

    protected void initExceptionHandler() {
        crashHandler = new CrashHandlerImpl(this, getRunPath().resolve("crashreport"));
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Caught unhandled exception!!! Engine will terminate!");
            crashHandler.crash(t, e);
        });
        crashHandler.addReportDetail("Engine Version",
                builder -> builder.append(Platform.getVersion()));
        crashHandler.addReportDetail("Engine Side",
                builder -> builder.append(getSide()));
        crashHandler.addReportDetail("Runtime Environment",
                builder -> builder.append(getRuntimeEnvironment()));
        crashHandler.addReportDetail("Loaded Mods",
                builder -> {
                    if (getModManager() == null)
                        builder.append("[]");
                    else
                        builder.append("[").append(StringUtils.join(getModManager().getLoadedMods().stream().map(modContainer -> modContainer.getId() + "(" + modContainer.getVersion() + ")").iterator(), ", ")).append("]");
                });
        crashHandler.addReportDetail("Operating System",
                builder -> builder.append(format("%s (%s) version %s", OS_NAME, OS_ARCH, OS_VERSION)));
        crashHandler.addReportDetail("Java Version",
                builder -> builder.append(format("%s (%s), %s", JAVA_VERSION, JAVA_VM_VERSION, JAVA_VERSION)));
        crashHandler.addReportDetail("JVM Information",
                builder -> builder.append(format("%s (%s), %s", JAVA_VM_NAME, JAVA_VM_INFO, JAVA_VM_VENDOR)));
        crashHandler.addReportDetail("Heap Memory Usage",
                builder -> {
                    MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
                    long usedMemory = heapMemoryUsage.getUsed() >> 20;
                    long totalMemory = heapMemoryUsage.getCommitted() >> 20;
                    long maxMemory = heapMemoryUsage.getMax() >> 20;
                    builder.append(format("%d MB / %d MB (Max: %d MB)", usedMemory, totalMemory, maxMemory));
                });
        crashHandler.addReportDetail("Non Heap Memory Usage",
                builder -> {
                    MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
                    long usedMemory = nonHeapMemoryUsage.getUsed() >> 20;
                    long maxMemory = nonHeapMemoryUsage.getMax() >> 20;
                    builder.append(format("%d MB / %d MB", usedMemory, maxMemory));
                });
        crashHandler.addReportDetail("JVM Flags",
                builder -> {
                    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
                    List<String> jvmFlags = runtimeMXBean.getInputArguments();
                    builder.append(format("(%s totals) %s", jvmFlags.size(), String.join(" ", jvmFlags)));
                });
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

        initExceptionHandler();
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
        startGame(new GameServerFullAsync(this, gameBasePath, passThroughGameData != null ? passThroughGameData : GameData.createFromExistingGame(gameBasePath, serverConfig.getGame()), nettyServer, chunkStatusListenerSupplier));
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
            if (isPlaying() && !game.isMarkedTermination()) {
                game.terminate();
            } else if (game.isTerminated()) {
                tryTerminate();
            }
        }
        boolean oldPaused = this.paused;
        this.paused = parent.isGamePaused();
        if (!oldPaused && this.paused) {

        }
//        nettyServer.tick();
        // need to tick if marked termination even if game is paused
        if (game.isMarkedTermination() || !this.paused) {
            ((GameServerFullAsync) game).tick();
        }
    }

    private void tryTerminate() {
        logger.info("Engine terminating!");
        if (isPlaying()) {
            game.terminate();
        }

        nettyServer.close();
        getEventBus().post(new EngineEvent.PreTermination(this));

        ticker.stop();
        shutdownListeners.forEach(Runnable::run);
        logger.info("Engine terminated!");
        this.terminated = true;
    }

    public boolean isTerminated() {
        return terminated;
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
