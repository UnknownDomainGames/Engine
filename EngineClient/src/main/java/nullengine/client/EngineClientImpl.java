package nullengine.client;

import nullengine.EngineBase;
import nullengine.Platform;
import nullengine.client.asset.AssetManager;
import nullengine.client.asset.AssetType;
import nullengine.client.asset.EngineAssetManager;
import nullengine.client.asset.model.block.BlockModelManager;
import nullengine.client.asset.source.AssetSource;
import nullengine.client.asset.source.CompositeAssetSource;
import nullengine.client.asset.source.FileSystemAssetSource;
import nullengine.client.game.GameClient;
import nullengine.client.i18n.LocaleManager;
import nullengine.client.input.keybinding.KeyBinding;
import nullengine.client.input.keybinding.KeyBindingManager;
import nullengine.client.rendering.EngineRenderContext;
import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.game3d.Game3DRenderer;
import nullengine.client.rendering.gui.GuiRenderer;
import nullengine.client.rendering.model.BakedModel;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.sound.ALSoundManager;
import nullengine.client.sound.EngineSoundManager;
import nullengine.enginemod.EngineModClientListeners;
import nullengine.event.engine.EngineEvent;
import nullengine.game.Game;
import nullengine.logic.Ticker;
import nullengine.mod.ModContainer;
import nullengine.util.ClassPathUtils;
import nullengine.util.RuntimeEnvironment;
import nullengine.util.Side;
import nullengine.util.disposer.Disposer;
import nullengine.util.disposer.DisposerImpl;

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
    private EngineRenderContext renderContext;
    private LocaleManager localeManager;

    private Ticker ticker;
    private Disposer disposer;

    private GameClient game;

    private KeyBindingManager keyBindingManager;

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
        disposer = new DisposerImpl();

        // TODO: Remove it
        modManager.getMod("engine").ifPresent(modContainer -> modContainer.getEventBus().register(EngineModClientListeners.class));
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
        renderContext = new EngineRenderContext(this);
        renderContext.getRenderers().add(new Game3DRenderer());
        renderContext.getRenderers().add(new GuiRenderer());
        renderContext.init(clientThread);
        initRenderCrashReportDetails();
        renderContext.getWindow().addWindowCloseCallback(window -> Platform.getEngine().terminate());
        addShutdownListener(renderContext::dispose);
        assetManager.getReloadManager().addFirst("Shader", ShaderManager.instance()::reload);

        assetManager.register(AssetType.builder(BakedModel.class).name("VoxelModel").provider(new BlockModelManager(this)).parentLocation("model").extensionName(".json").build());

        logger.info("Initializing audio context!");
        soundManager = new EngineSoundManager();
        soundManager.init();
        addShutdownListener(soundManager::dispose);
        assetManager.getReloadManager().addLast("Sound", soundManager::reload);

        logger.info("Initializing internalization!");
        localeManager = LocaleManager.INSTANCE;
        assetManager.getReloadManager().addLast("I18n", () -> {
            localeManager.reset();
            for (ModContainer mod : EngineClientImpl.this.getModManager().getLoadedMods()) {
                localeManager.register(mod);
            }
        });

        assetManager.reload();
    }

    private void initRenderCrashReportDetails() {
        crashHandler.addReportDetail("GL Vendor", builder -> builder.append(renderContext.getGLInfo().getVendor()));
        crashHandler.addReportDetail("GL Renderer", builder -> builder.append(renderContext.getGLInfo().getRenderer()));
        crashHandler.addReportDetail("GL Version", builder -> builder.append(renderContext.getGLInfo().getVersion()));
        crashHandler.addReportDetail("GL Extensions", builder -> builder.append(renderContext.getGLInfo().getExtensions()));
        crashHandler.addReportDetail("GL Shading Language Version", builder -> builder.append(renderContext.getGLInfo().getShadingLanguageVersion()));
        crashHandler.addReportDetail("GPU Memory Usage", builder -> {
            var gpuMemoryInfo = renderContext.getGPUMemoryInfo();
            var usedMemory = (gpuMemoryInfo.getTotalMemory() - gpuMemoryInfo.getFreeMemory()) / 1024;
            var totalMemory = gpuMemoryInfo.getTotalMemory() / 1024;
            builder.append(usedMemory).append(" MB / ").append(totalMemory).append(" MB");
        });
    }

    @Override
    protected void finishStage() {
        super.finishStage();

        logger.info("Initializing key binding!");
        var window = renderContext.getWindow();
        keyBindingManager = new KeyBindingManager(this, registryManager.getRegistry(KeyBinding.class).orElseThrow());
        keyBindingManager.reload();
        window.addKeyCallback(keyBindingManager::handleKey);
        window.addMouseCallback(keyBindingManager::handleMouse);

        ticker = new Ticker(this::clientTick, partial -> renderContext.render(partial), Ticker.CLIENT_TICK);
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
        if (isPlaying()) { // TODO: Remove it.
            game.clientTick();
        }

        // TODO: Remove it.
        keyBindingManager.tick();
        soundManager.updateListener(renderContext.getCamera());

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
    public RenderContext getRenderContext() {
        return renderContext;
    }

    @Override
    public Disposer getDisposer() {
        return disposer;
    }

    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public ALSoundManager getSoundManager() {
        return soundManager;
    }
}
