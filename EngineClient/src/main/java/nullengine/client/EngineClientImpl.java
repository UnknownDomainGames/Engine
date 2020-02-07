package nullengine.client;

import configuration.io.ConfigLoadException;
import configuration.parser.ConfigParseException;
import nullengine.EngineBase;
import nullengine.Platform;
import nullengine.client.asset.AssetManager;
import nullengine.client.asset.AssetType;
import nullengine.client.asset.EngineAssetManager;
import nullengine.client.asset.reloading.AssetReloadListener;
import nullengine.client.asset.source.AssetSource;
import nullengine.client.asset.source.CompositeAssetSource;
import nullengine.client.asset.source.FileSystemAssetSource;
import nullengine.client.game.GameClient;
import nullengine.client.i18n.LocaleManager;
import nullengine.client.input.keybinding.KeyBinding;
import nullengine.client.input.keybinding.KeyBindingManager;
import nullengine.client.rendering.EngineRenderManager;
import nullengine.client.rendering.RenderEngine;
import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.gl.util.GLContextUtils;
import nullengine.client.rendering.model.BakedModel;
import nullengine.client.rendering.model.voxel.ModelManager;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.settings.EngineSettings;
import nullengine.client.sound.ALSoundManager;
import nullengine.client.sound.EngineSoundManager;
import nullengine.enginemod.EngineModClientListeners;
import nullengine.enginemod.EngineModListeners;
import nullengine.event.engine.EngineEvent;
import nullengine.game.Game;
import nullengine.logic.Ticker;
import nullengine.mod.ModContainer;
import nullengine.util.ClassPathUtils;
import nullengine.util.RuntimeEnvironment;
import nullengine.util.Side;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;

public class EngineClientImpl extends EngineBase implements EngineClient {

    private Thread clientThread;

    private AssetSource engineAssetSource;
    private EngineAssetManager assetManager;
    private EngineSoundManager soundManager;
    private EngineRenderManager renderManager;
    private LocaleManager localeManager;

    private Ticker ticker;

    private GameClient game;

    private KeyBindingManager keyBindingManager;

    private EngineSettings settings;

    public EngineClientImpl(Path runPath) {
        super(runPath);
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
        try {
            Path engineJarPath = Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            if (getRuntimeEnvironment() == RuntimeEnvironment.ENGINE_DEVELOPMENT) {
                engineAssetSource = new CompositeAssetSource(
                        ClassPathUtils.getDirectoriesInClassPath(),
                        "assets",
                        getClass().getClassLoader());
            } else {
                FileSystem fileSystem = FileSystems.newFileSystem(engineJarPath, getClass().getClassLoader());
                engineAssetSource = new FileSystemAssetSource(fileSystem, "assets");
            }
        } catch (URISyntaxException | IOException e) {
            // TODO: Crash report
            logger.error("Cannot init engine.", e);
        }

        assetManager = new EngineAssetManager();
        assetManager.getSourceManager().getSources().add(engineAssetSource);
        shutdownListeners.add(() -> assetManager.dispose());

        logger.info("Initializing render context!");
        renderManager = new EngineRenderManager(this);
        RenderManager.Internal.setInstance(renderManager);
        renderManager.init(clientThread);
        initRenderCrashReportDetails();
        renderManager.getWindow().addWindowCloseCallback(window -> Platform.getEngine().terminate());
        addShutdownListener(renderManager::dispose);
        assetManager.getReloadManager().addListener(AssetReloadListener.builder().name("Shader").runnable(ShaderManager.instance()::reload).build());

        assetManager.register(AssetType.builder(BakedModel.class).name("VoxelModel").provider(new ModelManager()).parentLocation("model").extensionName(".json").build());

        logger.info("Initializing audio context!");
        soundManager = new EngineSoundManager();
        soundManager.init();
        addShutdownListener(soundManager::dispose);
        assetManager.getReloadManager().addListener(AssetReloadListener.builder().name("Sound").runnable(soundManager::reload).build());

        logger.info("Initializing internalization!");
        localeManager = LocaleManager.INSTANCE;
        assetManager.getReloadManager().addListener(AssetReloadListener.builder().name("I18n").runnable(() -> {
            localeManager.reset();
            for (ModContainer mod : EngineClientImpl.this.getModManager().getLoadedMods()) {
                localeManager.register(mod);
            }
        }).build());

        assetManager.reload();
    }

    private void initRenderCrashReportDetails() {
        crashHandler.addReportDetail("GL Vendor", builder -> builder.append(GLContextUtils.getVendor()));
        crashHandler.addReportDetail("GL Renderer", builder -> builder.append(GLContextUtils.getRenderer()));
        crashHandler.addReportDetail("GL Version", builder -> builder.append(GLContextUtils.getVersion()));
        crashHandler.addReportDetail("GL Extensions", builder -> builder.append(GLContextUtils.getExtensions()));
        crashHandler.addReportDetail("GL Shading Language Version", builder -> builder.append(GLContextUtils.getShadingLanguageVersion()));
        crashHandler.addReportDetail("GPU Memory Usage", builder -> {
            var gpuMemoryInfo = RenderEngine.getManager().getGPUInfo();
            var usedMemory = (gpuMemoryInfo.getTotalMemory() - gpuMemoryInfo.getFreeMemory()) / 1024;
            var totalMemory = gpuMemoryInfo.getTotalMemory() / 1024;
            builder.append(usedMemory).append(" MB / ").append(totalMemory).append(" MB");
        });
    }

    @Override
    protected void finishStage() {
        super.finishStage();

        logger.info("Initializing key binding!");
        var window = renderManager.getWindow();
        keyBindingManager = new KeyBindingManager(this, registryManager.getRegistry(KeyBinding.class).orElseThrow());
        keyBindingManager.reload();
        window.addKeyCallback(keyBindingManager::handleKey);
        window.addMouseCallback(keyBindingManager::handleMouse);

        ticker = new Ticker(this::clientTick, partial -> renderManager.render(partial), Ticker.CLIENT_TICK);
    }

    @Override
    public void runEngine() {
        super.runEngine();

        addShutdownListener(ticker::stop);

        logger.info("Finishing initialization!");
        eventBus.post(new EngineEvent.Ready(this));

        ticker.run();
    }

    private void clientTick() {
        if (isPlaying()) {
            game.clientTick();
            keyBindingManager.tick();
        }

        // TODO: Remove it.
        soundManager.updateListener(renderManager.getCamera());

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
    public void startGame(Game game) {
        if (isPlaying()) {
            throw new IllegalStateException("Game is running");
        }

        if (!(game instanceof GameClient)) {
            throw new IllegalArgumentException("Game must be GameClient");
        }

        this.game = (GameClient) Objects.requireNonNull(game);
        game.init();
    }

    @Override
    public GameClient getCurrentGame() {
        return game;
    }

    @Override
    public boolean isPlaying() {
        return game != null && !game.isTerminated();
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
    public RenderManager getRenderManager() {
        return renderManager;
    }

    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public ALSoundManager getSoundManager() {
        return soundManager;
    }

    @Override
    public EngineSettings getSettings() {
        return settings;
    }
}
