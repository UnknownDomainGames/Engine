package engine.client;

import configuration.io.ConfigLoadException;
import configuration.parser.ConfigParseException;
import engine.EngineBase;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;

public class EngineClientImpl extends EngineBase implements EngineClient {

    private Thread clientThread;

    private EngineAssetManager assetManager;
    private ALSoundManager soundManager;
    private EngineGraphicsManager graphicsManager;
    private LocaleManager localeManager;

    private Ticker ticker;

    private ClientGame game;

    private KeyBindingManager keyBindingManager;

    private EngineSettings settings;

    private Profile playerProfile;

    public EngineClientImpl(Path runPath, Profile profile) {
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
            for (ModContainer mod : EngineClientImpl.this.getModManager().getLoadedMods()) {
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
        ticker = new Ticker(this::clientTick, partial -> graphicsManager.doRender(partial), Ticker.CLIENT_TICK);
    }

    @Override
    public void runEngine() {
        super.runEngine();

        addShutdownListener(ticker::stop);

        logger.info("Finishing initialization!");
        eventBus.post(new EngineEvent.Ready(this));

        ticker.run();
    }

    @Override
    public Game getGame() {
        return null;
    }

    private void clientTick() {
        if (isPlaying()) {
            game.clientTick();
            keyBindingManager.tick();
        }

        // TODO: Remove it.
        soundManager.getListener().camera(graphicsManager.getViewport().getCamera());

        if (isMarkedTermination()) {
            if (isPlaying()) {
                game.terminate();
            } else {
                tryTerminate();
            }
        }
    }

    private void tryTerminate() {
        logger.info("Engine terminating!");
        if (isPlaying()) {
            game.terminate();
            game.clientTick();
        }

        eventBus.post(new EngineEvent.PreTermination(this));

        ticker.stop();
        shutdownListeners.forEach(Runnable::run);
        logger.info("Engine terminated!");
    }

    @Override
    public void runGame(Game game) {
        if (isPlaying()) {
            throw new IllegalStateException("Game is running");
        }

        if (!(game instanceof ClientGame)) {
            throw new IllegalArgumentException("Game must be GameClient");
        }

        this.game = (ClientGame) Objects.requireNonNull(game);
        game.init();
    }

    @Override
    public ClientGame getClientGame() {
        return game;
    }

    @Override
    public void runClientGame(ClientGame game) {

    }

    @Override
    public boolean isPlaying() {
        return game != null && game.isReadyToPlay() && !game.isTerminated();
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
