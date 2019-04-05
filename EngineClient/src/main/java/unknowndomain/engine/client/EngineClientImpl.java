package unknowndomain.engine.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.AssetManager;
import unknowndomain.engine.client.asset.EngineAssetLoadManager;
import unknowndomain.engine.client.asset.EngineAssetManager;
import unknowndomain.engine.client.asset.EngineAssetSource;
import unknowndomain.engine.client.asset.loader.AssetLoadManager;
import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.rendering.EngineRenderContext;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.game3d.Game3DRenderer;
import unknowndomain.engine.client.rendering.gui.GuiRenderer;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.sound.ALSoundManager;
import unknowndomain.engine.client.sound.EngineSoundManager;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.SimpleEventBus;
import unknowndomain.engine.event.asm.AsmEventListenerFactory;
import unknowndomain.engine.event.engine.EngineEvent;
import unknowndomain.engine.event.engine.GameStartEvent;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.math.Ticker;
import unknowndomain.engine.util.Disposer;
import unknowndomain.engine.util.DisposerImpl;
import unknowndomain.engine.util.RuntimeEnvironment;
import unknowndomain.engine.util.Side;
import unknowndomain.game.DefaultGameMode;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.SystemUtils.*;

public class EngineClientImpl implements EngineClient {

    private final Logger logger = LoggerFactory.getLogger("Engine");

    private RuntimeEnvironment runtimeEnvironment;

    private Thread clientThread;

    private EventBus eventBus;

    private AssetSource engineAssetSource;
    private EngineAssetLoadManager assetLoadManager;
    private EngineAssetManager assetManager;
    private EngineSoundManager soundManager;
    private EngineRenderContext renderContext;

    private Ticker ticker;
    private Disposer disposer;

    private GameClient game;

    private boolean initialized = false;
    private boolean running = false;
    private boolean terminated = false;

    private final List<Runnable> shutdownListeners = new LinkedList<>();

    @Override
    public void initEngine() {
        if (initialized) {
            throw new IllegalStateException("Engine has been initialized.");
        }
        initialized = true;

        initEnvironment();
        printSystemInfo();

        eventBus = SimpleEventBus.builder().eventListenerFactory(AsmEventListenerFactory.create()).build();

        // TODO: Remove it
        getEventBus().register(new DefaultGameMode());

        initEngineClient();

        // Finish Stage
        logger.info("Finishing initialization!");
        eventBus.post(new EngineEvent.InitializationComplete(this));
    }

    private void initEngineClient() {
        logger.info("Initializing client engine!");
        disposer = new DisposerImpl();

        logger.info("Initializing asset!");
        engineAssetSource = EngineAssetSource.create();
        assetManager = new EngineAssetManager();
        assetManager.getSources().add(engineAssetSource);
        assetLoadManager = new EngineAssetLoadManager(assetManager);

        logger.info("Initializing render context!");
        renderContext = new EngineRenderContext(this);
        renderContext.getRenderers().add(new Game3DRenderer());
        renderContext.getRenderers().add(new GuiRenderer());

        logger.info("Initializing audio context!");
        soundManager = new EngineSoundManager();
        soundManager.init();
        addShutdownListener(soundManager::dispose);

        assetManager.getReloadListeners().add(() -> {
            ShaderManager.INSTANCE.reload();
            renderContext.getTextureManager().reload();
            soundManager.reload();
        });

        ticker = new Ticker(this::clientTick, partial -> renderContext.render(partial), Ticker.CLIENT_TICK);
    }

    private void initEnvironment() {
        try {
            // TODO:
            runtimeEnvironment = Files.isDirectory(Path.of(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI())) ? RuntimeEnvironment.ENGINE_DEVELOPMENT : RuntimeEnvironment.DEPLOYMENT;
        } catch (URISyntaxException e) {
            runtimeEnvironment = RuntimeEnvironment.DEPLOYMENT;
        }
    }

    @Override
    public void runEngine() {
        if (running) {
            throw new IllegalStateException("Engine is running.");
        }
        running = true;

        clientThread = Thread.currentThread();
        renderContext.init(clientThread);
        addShutdownListener(renderContext::dispose);

        assetManager.reload();

        addShutdownListener(ticker::stop);
        ticker.run();
    }

    private void clientTick() {
        if (isTerminated()) {
            tryTerminate();
            return;
        }

        if (isPlaying()) { // TODO: Remove it.
            game.clientTick();
        }

        soundManager.updateListener(renderContext.getCamera()); // TODO: Fix it.
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public RuntimeEnvironment getRuntimeEnvironment() {
        return runtimeEnvironment;
    }

    @Override
    public synchronized void terminate() {
        if (terminated) {
            return;
        }

        terminated = true;
        logger.info("Marked engine terminated!");
    }

    private void tryTerminate() {
        logger.info("Engine terminating!");

        if (isPlaying()) {
            game.terminateNow();
        }

        shutdownListeners.forEach(Runnable::run);
        logger.info("Engine terminated!");
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public void addShutdownListener(Runnable runnable) {
        shutdownListeners.add(runnable);
    }

    @Override
    public void startGame(Game game) {
        if (isPlaying()) {
            throw new IllegalStateException("Game is running");
        }

        if (!(game instanceof GameClient)) {
            throw new IllegalArgumentException("Game must be GameClient");
        }

        eventBus.post(new GameStartEvent.Pre(game));
        this.game = (GameClient) Objects.requireNonNull(game);
        game.init();
        eventBus.post(new GameStartEvent.Post(game));
    }

    @Override
    public GameClient getCurrentGame() {
        return game;
    }

    @Override
    public boolean isPlaying() {
        return game != null && !game.isStopped();
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Thread getClientThread() {
        return clientThread;
    }

    @Override
    public boolean isClientThread() {
        return Thread.currentThread() == clientThread;
    }

    @Override
    public AssetSource getEngineAssetSource() {
        return engineAssetSource;
    }

    @Override
    public RenderContext getRenderContext() {
        return renderContext;
    }

    @Override
    public Disposer getDisposer() {
        return disposer;
    }

    @Override
    public AssetLoadManager getAssetLoadManager() {
        return assetLoadManager;
    }

    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public ALSoundManager getSoundManager() {
        return soundManager;
    }

    private void printSystemInfo() {
        Logger logger = Platform.getLogger();
        logger.info("----- System Information -----");
        logger.info("\tOperating System: {} ({}) version {}", OS_NAME, OS_ARCH, OS_VERSION);
        logger.info("\tJava Version: {}, {}", JAVA_VERSION, JAVA_VENDOR);
        logger.info("\tJVM Information: {} ({}), {}", JAVA_VM_NAME, JAVA_VM_INFO, JAVA_VM_VENDOR);
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        logger.info("\tMax Memory: {} bytes ({} MB)", maxMemory, maxMemory / 1024L / 1024L);
        logger.info("\tTotal Memory: {} bytes ({} MB)", totalMemory, totalMemory / 1024L / 1024L);
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmFlags = runtimeMXBean.getInputArguments();
        StringBuilder formattedFlags = new StringBuilder();
        for (String flag : jvmFlags) {
            if (formattedFlags.length() > 0) {
                formattedFlags.append(' ');
            }
            formattedFlags.append(flag);
        }
        logger.info("\tJVM Flags ({} totals): {}", jvmFlags.size(), formattedFlags.toString());
        logger.info("\tEngine Version: {}", Platform.getVersion());
        logger.info("\tRuntime Environment: {}", runtimeEnvironment.name());
        logger.info("------------------------------");
    }
}
