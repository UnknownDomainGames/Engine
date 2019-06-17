package unknowndomain.engine.client;

import unknowndomain.engine.EngineBase;
import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.AssetManager;
import unknowndomain.engine.client.asset.EngineAssetManager;
import unknowndomain.engine.client.asset.model.voxel.VoxelModel;
import unknowndomain.engine.client.asset.model.voxel.VoxelModelManager;
import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.client.asset.source.DirectoriesAssetSource;
import unknowndomain.engine.client.asset.source.FileSystemAssetSource;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.rendering.EngineRenderContext;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.game3d.Game3DRenderer;
import unknowndomain.engine.client.rendering.gui.GuiRenderer;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.sound.ALSoundManager;
import unknowndomain.engine.client.sound.EngineSoundManager;
import unknowndomain.engine.event.engine.EngineEvent;
import unknowndomain.engine.event.game.GameStartEvent;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.i18n.LocaleManager;
import unknowndomain.engine.math.Ticker;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.util.RuntimeEnvironment;
import unknowndomain.engine.util.Side;
import unknowndomain.engine.util.disposer.Disposer;
import unknowndomain.engine.util.disposer.DisposerImpl;
import unknowndomain.game.DefaultGameMode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static unknowndomain.engine.util.ClassPathUtils.getDirectoriesInClassPath;

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

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void initEngine() {
        super.initEngine();

        // TODO: Remove it
        getEventBus().register(DefaultGameMode.class);

        initEngineClient();
    }

    private void initEngineClient() {
        logger.info("Initializing client engine!");
        clientThread = Thread.currentThread();
        disposer = new DisposerImpl();

        logger.info("Initializing asset!");
        try {
            Path engineJarPath = Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            if (getRuntimeEnvironment() == RuntimeEnvironment.ENGINE_DEVELOPMENT) {
                engineAssetSource = new DirectoriesAssetSource(
                        getDirectoriesInClassPath().stream()
                                .map($ -> $.resolve("assets"))
                                .collect(Collectors.toList()));
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

        logger.info("Initializing render context!");
        renderContext = new EngineRenderContext(this);
        renderContext.getRenderers().add(new Game3DRenderer());
        renderContext.getRenderers().add(new GuiRenderer());
        renderContext.init(clientThread);
        renderContext.getWindow().addWindowCloseCallback(window -> Platform.getEngine().terminate());
        // TODO: Remove it.
        renderContext.getWindow().addKeyCallback((window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_F12 && action == GLFW_PRESS) {
                Platform.getEngine().terminate();
            }
        });
        addShutdownListener(renderContext::dispose);
        assetManager.getReloadDispatcher().addLast("Shader", ShaderManager.INSTANCE::reload);
        assetManager.getReloadDispatcher().addLast("Texture", () -> renderContext.getTextureManager().reload());

        assetManager.register(VoxelModel.class, "VoxelModel", new VoxelModelManager(this));

        logger.info("Initializing audio context!");
        soundManager = new EngineSoundManager();
        soundManager.init();
        addShutdownListener(soundManager::dispose);
        assetManager.getReloadDispatcher().addLast("Sound", soundManager::reload);

        localeManager = LocaleManager.INSTANCE;
        assetManager.getReloadDispatcher().addLast("I18n", () -> {
            localeManager.reset();
            for (ModContainer mod : EngineClientImpl.this.getModManager().getLoadedMods()) {
                localeManager.register(mod);
            }
        });

        ticker = new Ticker(this::clientTick, partial -> renderContext.render(partial), Ticker.CLIENT_TICK);
    }

    @Override
    public void runEngine() {
        super.runEngine();

        assetManager.reload();

        addShutdownListener(ticker::stop);

        logger.info("Finishing initialization!");
        eventBus.post(new EngineEvent.Ready(this));

        ticker.run();
    }

    private void clientTick() {
        if (isTerminated()) {
            if (isPlaying()) {
                game.terminate();
                game.clientTick();
            }
            tryTerminate();
            return;
        }

        if (isPlaying()) { // TODO: Remove it.
            game.clientTick();
        }

        soundManager.updateListener(renderContext.getCamera()); // TODO: Remove it.
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
