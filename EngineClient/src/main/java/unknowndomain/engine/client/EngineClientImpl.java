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
import unknowndomain.engine.client.game.GameClientStandalone;
import unknowndomain.engine.client.gui.EngineGuiManager;
import unknowndomain.engine.client.gui.GuiManager;
import unknowndomain.engine.client.rendering.EngineRenderContext;
import unknowndomain.engine.client.rendering.display.GLFWGameWindow;
import unknowndomain.engine.client.rendering.display.GameWindow;
import unknowndomain.engine.client.rendering.gui.GuiRenderer;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.rendering.texture.EngineTextureManager;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.sound.ALSoundManager;
import unknowndomain.engine.client.sound.EngineSoundManager;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.engine.EngineEvent;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.math.Ticker;
import unknowndomain.engine.player.Profile;
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
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.SystemUtils.*;

public class EngineClientImpl implements EngineClient {

    public static final int WINDOW_WIDTH = 854, WINDOW_HEIGHT = 480;

    private final Logger logger = LoggerFactory.getLogger("Engine");

    private RuntimeEnvironment runtimeEnvironment;

    private Thread clientThread;
    private GLFWGameWindow window;
    private Profile playerProfile;

    private EventBus eventBus;

    private AssetSource engineAssetSource;
    private EngineAssetLoadManager assetLoadManager;
    private EngineAssetManager assetManager;
    private EngineTextureManager textureManager;
    private EngineSoundManager soundManager;
    private EngineGuiManager guiManager;

    private EngineRenderContext renderContext;
    private Ticker ticker;

    private Disposer disposer;

    private GameClientStandalone game;

    private boolean initialized = false;
    private boolean running = false;
    private boolean terminated = false;

    @Override
    public void initEngine() {
        if (initialized) {
            throw new IllegalStateException("Engine has been initialized.");
        }
        initialized = true;

        initEnvironment();
        printSystemInfo();

        eventBus = new AsmEventBus();
        playerProfile = new Profile(UUID.randomUUID(), 12);

        // TODO: Remove it
        getEventBus().register(new DefaultGameMode());

        initEngineClient();

        // Finish Stage
        logger.info("Finishing Initialization!");
        eventBus.post(new EngineEvent.InitializationComplete(this));
    }

    private void initEngineClient() {
        logger.info("Initializing client engine!");
        disposer = new DisposerImpl();

        logger.info("Initializing window!");
        window = new GLFWGameWindow(WINDOW_WIDTH, WINDOW_HEIGHT, UnknownDomain.getName());
        window.init();

        logger.info("Initializing asset!");
        engineAssetSource = EngineAssetSource.create();
        assetManager = new EngineAssetManager();
        assetManager.getSources().add(engineAssetSource);
        assetLoadManager = new EngineAssetLoadManager(assetManager);

        textureManager = new EngineTextureManager();
        soundManager = new EngineSoundManager();
        soundManager.init();
        guiManager = new EngineGuiManager(this);
        assetManager.getReloadListeners().add(() -> {
            ShaderManager.INSTANCE.reload();
            textureManager.reload();
            soundManager.reload();
        });

        renderContext = new EngineRenderContext(this);
        renderContext.getRenderers().add(new GuiRenderer());

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

        assetManager.reload();

        ticker.run();
    }

    private void clientTick() {

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

        logger.info("Terminating Engine!");
        terminated = true;

        if (game != null) {
            game.terminate();
        }

        ticker.stop();

        soundManager.dispose();

        logger.info("Engine terminated!");
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public void startGame() {
        // prepare
        game = new GameClientStandalone(this);
        game.run();
    }

    @Override
    public void startGame(Game game) {

    }

    @Override
    public GameClient getCurrentGame() {
        return game;
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
    public GameWindow getWindow() {
        return window;
    }

    @Override
    public AssetSource getEngineAssetSource() {
        return engineAssetSource;
    }

    @Override
    public GuiManager getGuiManager() {
        return guiManager;
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
    public TextureManager getTextureManager() {
        return textureManager;
    }

    @Override
    public ALSoundManager getSoundManager() {
        return soundManager;
    }

    private void printSystemInfo() {
        Logger logger = Platform.getLogger();
        logger.info("----- System Information -----");
        logger.info("\tOperating System: " + OS_NAME + " (" + OS_ARCH + ") version " + OS_VERSION);
        logger.info("\tJava Version: " + JAVA_VERSION + ", " + JAVA_VENDOR);
        logger.info("\tJVM Information: " + JAVA_VM_NAME + " (" + JAVA_VM_INFO + "), " + JAVA_VM_VENDOR);
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        logger.info("\tMax Memory: " + maxMemory + " bytes (" + maxMemory / 1024L / 1024L + " MB)");
        logger.info("\tTotal Memory: " + totalMemory + " bytes (" + totalMemory / 1024L / 1024L + " MB)");
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmFlags = runtimeMXBean.getInputArguments();
        StringBuilder formattedFlags = new StringBuilder();
        for (String flag : jvmFlags) {
            if (formattedFlags.length() > 0) {
                formattedFlags.append(' ');
            }
            formattedFlags.append(flag);
        }
        logger.info("\tJVM Flags (" + jvmFlags.size() + " totals): " + formattedFlags.toString());
        logger.info("\tEngine Version: " + Platform.getVersion());
        logger.info("\tRuntime Environment: " + runtimeEnvironment.name());
        logger.info("------------------------------");
    }
}
