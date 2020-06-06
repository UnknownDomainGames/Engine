package engine.client;

import configuration.io.ConfigLoadException;
import configuration.parser.ConfigParseException;
import engine.BaseEngine;
import engine.Platform;
import engine.client.asset.AssetManager;
import engine.client.asset.AssetType;
import engine.client.asset.EngineAssetManager;
import engine.client.asset.reloading.AssetReloadHandler;
import engine.client.asset.source.CompositeAssetSource;
import engine.client.asset.source.FileSystemAssetSource;
import engine.client.game.ClientGame;
import engine.client.i18n.LocaleManager;
import engine.client.input.keybinding.KeyBindingManager;
import engine.client.settings.EngineSettings;
import engine.client.sound.ALSoundManager;
import engine.client.sound.Sound;
import engine.client.sound.SoundManager;
import engine.enginemod.EngineModClientListeners;
import engine.enginemod.EngineModListeners;
import engine.event.engine.EngineEvent;
import engine.game.Game;
import engine.graphics.EngineGraphicsManager;
import engine.graphics.GraphicsEngine;
import engine.graphics.GraphicsManager;
import engine.graphics.gl.util.GLHelper;
import engine.gui.EngineGUIPlatform;
import engine.logic.Ticker;
import engine.mod.ModContainer;
import engine.player.Profile;
import engine.util.ClassPathUtils;
import engine.util.RuntimeEnvironment;
import engine.util.Side;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public final class ClientEngineImpl extends BaseEngine implements ClientEngine {

    private EngineAssetManager assetManager;
    private ALSoundManager soundManager;
    private EngineGraphicsManager graphicsManager;
    private LocaleManager localeManager;

    private Thread serverThread;
    private Ticker serverTicker;
    private Game serverGame;

    private Thread clientThread;
    private Ticker clientTicker;
    private ClientGame clientGame;

    private KeyBindingManager keyBindingManager;

    private EngineSettings settings;

    private Profile playerProfile;

    public ClientEngineImpl(Path runPath, Profile profile) {
        super(runPath);
        this.playerProfile = profile;
    }

    public Profile getPlayerProfile() {
        return playerProfile;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    protected void constructionStage() {
        super.constructionStage();

        logger.info("Initializing client engine!");
        clientThread = Thread.currentThread();

        settings = new EngineSettings();
        try {
            settings.load(getRunPath().resolve("config/engine.json"));
        } catch (ConfigParseException | ConfigLoadException e) {
            logger.debug("Cannot load or not found engine configuration file! Try to create new one!", e);
            settings = new EngineSettings();
            settings.save(getRunPath().resolve("config/engine.json"));
        }

        // TODO: Remove it
        modManager.getMod("engine").ifPresent(modContainer -> {
            modContainer.getEventBus().register(EngineModListeners.class);
            modContainer.getEventBus().register(EngineModClientListeners.class);
        });
    }

    @Override
    protected void modStage() {
        super.modStage();
    }

    @Override
    protected void resourceStage() {
        super.resourceStage();

        logger.info("Initializing asset!");
        assetManager = new EngineAssetManager();
        try {
            Path engineJarPath = Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            if (getRuntimeEnvironment() == RuntimeEnvironment.ENGINE_DEVELOPMENT) {
                assetManager.getSourceManager().getSources().add(new CompositeAssetSource(
                        ClassPathUtils.getDirectoriesInClassPath(),
                        "asset",
                        getClass().getClassLoader()));
            } else {
                FileSystem fileSystem = FileSystems.newFileSystem(engineJarPath, getClass().getClassLoader());
                assetManager.getSourceManager().getSources().add(new FileSystemAssetSource(fileSystem, "asset"));
            }
        } catch (URISyntaxException | IOException e) {
            getCrashHandler().crash(e);
        }
        shutdownListeners.add(assetManager::dispose);

        logger.info("Initializing graphics!");
        graphicsManager = new EngineGraphicsManager(this, clientThread);
        initGraphicsCrashReportDetails();
        graphicsManager.getWindow().setDoCloseImmediately(false);
        graphicsManager.getWindow().addWindowCloseCallback(window -> Platform.getEngine().terminate());
        addShutdownListener(graphicsManager::dispose);

        soundManager = new ALSoundManager();
        assetManager.register(AssetType.builder(Sound.class)
                .name("Sound")
                .provider(soundManager)
                .parentLocation("sound")
                .extensionName(".ogg")
                .build());
        addShutdownListener(soundManager::dispose);

        logger.info("Initializing input!");
        keyBindingManager = new KeyBindingManager(this);
        EngineGUIPlatform guiPlatform = EngineGUIPlatform.getInstance();
        guiPlatform.getStageHelper().enableInput(guiPlatform.getGUIStage());

        logger.info("Initializing I18n!");
        localeManager = LocaleManager.INSTANCE;
        assetManager.getReloadManager().addHandler(AssetReloadHandler.builder().name("I18n").runnable(() -> {
            localeManager.reset();
            for (ModContainer mod : ClientEngineImpl.this.getModManager().getLoadedMods()) {
                localeManager.register(mod);
            }
        }).build());

        graphicsManager.initScene();
        assetManager.reload();
    }

    private void initGraphicsCrashReportDetails() {
        crashHandler.addReportDetail("GL Vendor", builder -> builder.append(GLHelper.getVendor()));
        crashHandler.addReportDetail("GL Renderer", builder -> builder.append(GLHelper.getRenderer()));
        crashHandler.addReportDetail("GL Version", builder -> builder.append(GLHelper.getVersion()));
        crashHandler.addReportDetail("GL Extensions", builder -> builder.append(GLHelper.getExtensions()));
        crashHandler.addReportDetail("GL Shading Language Version", builder -> builder.append(GLHelper.getShadingLanguageVersion()));
        crashHandler.addReportDetail("GPU Memory Usage", builder -> {
            var gpuMemoryInfo = GraphicsEngine.getGraphicsBackend().getGPUInfo();
            var usedMemory = (gpuMemoryInfo.getTotalMemory() - gpuMemoryInfo.getFreeMemory()) / 1024;
            var totalMemory = gpuMemoryInfo.getTotalMemory() / 1024;
            builder.append(usedMemory).append(" MB / ").append(totalMemory).append(" MB");
        });
    }

    @Override
    protected void finishStage() {
        super.finishStage();

        eventBus.post(new EngineEvent.Ready(this));
        logger.info("Engine initialized!");
    }

    @Override
    public void runStage() {
        super.runStage();

        clientTicker = new Ticker(this::clientTick, partial -> graphicsManager.doRender(partial), Ticker.CLIENT_TICK);
        addShutdownListener(clientTicker::stop);
        clientTicker.run();
    }

    private void clientTick() {
        if (isPlaying()) {
            clientGame.update();
            keyBindingManager.tick();
        }

        // TODO: Remove it.
        soundManager.getListener().camera(graphicsManager.getViewport().getCamera());

        if (isMarkedTermination()) {
            tryTerminate();
        }
    }

    @Override
    public synchronized void terminate() {
        super.terminate();
        if (isPlaying()) clientGame.terminate();
        if (serverGame != null && !serverGame.isTerminated()) serverGame.terminate();
    }

    private void tryTerminate() {
        logger.info("Engine terminating!");
        if (isPlaying()) {
            clientGame.terminate();
            clientGame.update();
        }
        eventBus.post(new EngineEvent.PreTermination(this));
        shutdownListeners.forEach(Runnable::run);
        logger.info("Engine terminated!");
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
    public Game getServerGame() {
        return serverGame;
    }

    @Override
    public void runServerGame(@Nonnull Game game) {
        if (serverGame != null && !serverGame.isTerminated()) {
            throw new IllegalStateException("Server game is running");
        }

        serverGame = Validate.notNull(game);
        game.init();
        serverTicker = new Ticker(game::update, Ticker.LOGIC_TICK);
        serverThread = new Thread(serverTicker, "Server Thread");
        serverThread.start();
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
    public ClientGame getClientGame() {
        return clientGame;
    }

    @Override
    public void runClientGame(@Nonnull ClientGame game) {
        if (isPlaying()) {
            throw new IllegalStateException("Client game is running");
        }

        clientGame = Validate.notNull(game);
        game.init();
    }

    @Override
    public boolean isPlaying() {
        return clientGame != null && !clientGame.isTerminated();
    }

    @Override
    public GraphicsManager getGraphicsManager() {
        return graphicsManager;
    }

    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public SoundManager getSoundManager() {
        return soundManager;
    }

    @Override
    public EngineSettings getSettings() {
        return settings;
    }
}
