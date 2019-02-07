package unknowndomain.engine.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.AssetManager;
import unknowndomain.engine.client.asset.DefaultAssetManager;
import unknowndomain.engine.client.asset.DefaultAssetSourceManager;
import unknowndomain.engine.client.asset.EngineAssetSource;
import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.client.asset.source.AssetSourceManager;
import unknowndomain.engine.client.game.GameClientStandalone;
import unknowndomain.engine.client.rendering.display.GLFWGameWindow;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.rendering.texture.TextureManagerImpl;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.engine.EngineEvent;
import unknowndomain.engine.player.Profile;
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

    private GLFWGameWindow window;
    private Profile playerProfile;

    private EventBus eventBus;

    private AssetSource engineAssetSource;
    private AssetManager assetManager;
    private DefaultAssetSourceManager assetSourceManager;
    private TextureManager textureManager;

    private GameClientStandalone game;

    @Override
    public void initEngine() {
        initEnvironment();
        paintSystemInfo();

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
        logger.info("Initializing Window!");
        window = new GLFWGameWindow(WINDOW_WIDTH, WINDOW_HEIGHT, UnknownDomain.getName());
        window.init();

        engineAssetSource = EngineAssetSource.create();
        assetSourceManager = new DefaultAssetSourceManager();
        assetSourceManager.getSources().add(engineAssetSource);
        assetManager = new DefaultAssetManager(assetSourceManager);

        textureManager = new TextureManagerImpl();
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
    public void startGame() {
        // prepare
        game = new GameClientStandalone(this);
        game.run();
    }

    @Override
    public GameClientStandalone getCurrentGame() {
        return game;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public GLFWGameWindow getWindow() {
        return window;
    }

    @Override
    public AssetSource getEngineAssetSource() {
        return engineAssetSource;
    }

    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public AssetSourceManager getAssetSourceManager() {
        return assetSourceManager;
    }

    @Override
    public TextureManager getTextureManager() {
        return textureManager;
    }

    private void paintSystemInfo() {
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
