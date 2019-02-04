package unknowndomain.engine.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.Engine;
import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.EngineAssetSource;
import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.client.game.GameClientStandalone;
import unknowndomain.engine.client.rendering.display.GLFWGameWindow;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.engine.EngineEvent;
import unknowndomain.engine.player.Profile;
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

public class EngineClient implements Engine {

    public static final int WIDTH = 854, HEIGHT = 480;

    private Logger logger = LoggerFactory.getLogger("Engine");

    private GLFWGameWindow window;

    private EventBus eventBus;

    private AssetSource engineAssetSource;

    private Profile playerProfile;
    private GameClientStandalone game;

    private boolean developmentEnv;

    @Override
    public void initEngine() {
        initEnvironment();
        paintSystemInfo();

        Logger log = Platform.getLogger();
        eventBus = new AsmEventBus();
        playerProfile = new Profile(UUID.randomUUID(), 12);

        // TODO: Remove it
        getEventBus().register(new DefaultGameMode());

        // =====Client=====
        log.info("Initializing Window!");
        window = new GLFWGameWindow(WIDTH, HEIGHT, UnknownDomain.getName());
        window.init();

        // Resource Stage
        // Later when separating common and client, common will not have this part
        engineAssetSource = EngineAssetSource.create();

        // Finish Stage
        log.info("Finishing Initialization!");
        eventBus.post(new EngineEvent.InitializationComplete(this));
    }

    private void initEnvironment() {
        try {
            developmentEnv = Files.isDirectory(Path.of(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
        } catch (URISyntaxException e) {
            developmentEnv = false;
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
    public boolean isDevelopmentEnv() {
        return developmentEnv;
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

    public GLFWGameWindow getWindow() {
        return window;
    }

    public AssetSource getEngineAssetSource() {
        return engineAssetSource;
    }

    private void paintSystemInfo() {
        Logger logger = Platform.getLogger();
        logger.info("----- System Information -----");
        logger.info("\tOperating System: " + OS_NAME + " (" + OS_ARCH + ") version " + OS_VERSION);
        logger.info("\tJava Version: " + JAVA_VERSION + ", " + JAVA_VENDOR);
        logger.info("\tJVM Info: " + JAVA_VM_NAME + " (" + JAVA_VM_INFO + "), " + JAVA_VM_VENDOR);
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
        logger.info("\tRuntime Environment: " + (developmentEnv ? "Development" : "Deployment"));
        logger.info("------------------------------");
    }
}
