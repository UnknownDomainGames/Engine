package engine.graphics;

import engine.client.EngineClient;
import engine.client.asset.AssetType;
import engine.client.asset.provider.TextureAssetProvider;
import engine.client.event.rendering.RenderEvent;
import engine.client.hud.HUDManager;
import engine.graphics.display.Window;
import engine.graphics.game.GameRenderer;
import engine.graphics.management.GraphicsBackend;
import engine.graphics.texture.Texture2D;
import engine.graphics.viewport.PerspectiveViewport;
import engine.gui.EngineGUIManager;
import engine.gui.EngineHUDManager;
import engine.gui.GUIManager;
import engine.gui.GameGUIPlatform;

public class EngineRenderManager implements RenderManager {

    private final EngineClient engine;

    private Thread renderThread;
    private Window window;
    private PerspectiveViewport viewport;

    private GameRenderer gameRenderer;
    private EngineGUIManager guiManager;
    private EngineHUDManager hudManager;
    private GameGUIPlatform gameGUIPlatform;

    public EngineRenderManager(EngineClient engine) {
        this.engine = engine;
    }

    @Override
    public EngineClient getEngine() {
        return engine;
    }

    @Override
    public Thread getRenderThread() {
        return renderThread;
    }

    @Override
    public boolean isRenderThread() {
        return Thread.currentThread() == renderThread;
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public PerspectiveViewport getViewport() {
        return viewport;
    }

    @Override
    public GUIManager getGUIManager() {
        return guiManager;
    }

    @Override
    public HUDManager getHUDManager() {
        return hudManager;
    }

    public void init(Thread renderThread) {
        this.renderThread = renderThread;

        GraphicsEngine.start(new GraphicsEngine.Settings());

        GraphicsBackend manager = GraphicsEngine.getGraphicsBackend();
        window = manager.getPrimaryWindow();
        window.setDisplayMode(engine.getSettings().getDisplaySettings().getDisplayMode(),
                engine.getSettings().getDisplaySettings().getResolutionWidth(),
                engine.getSettings().getDisplaySettings().getResolutionHeight(),
                engine.getSettings().getDisplaySettings().getFrameRate());

        viewport = new PerspectiveViewport();
        viewport.setScene(new Scene3D());
        viewport.setSize(window.getWidth(), window.getHeight());

        initTextureAssetProvider();
        gameRenderer = new GameRenderer(this);
        gameGUIPlatform = new GameGUIPlatform();
        guiManager = new EngineGUIManager(window, gameGUIPlatform.getGUIStage());
        hudManager = new EngineHUDManager(gameGUIPlatform.getHUDStage());

        window.show();
    }

    private void initTextureAssetProvider() {
        getEngine().getAssetManager().register(
                AssetType.builder(Texture2D.class)
                        .name("Texture")
                        .provider(new TextureAssetProvider())
                        .parentLocation("texture")
                        .extensionName(".png")
                        .build());
    }

    public void render(float tpf) {
        engine.getEventBus().post(new RenderEvent.Pre());

        GraphicsEngine.doRender(tpf);
        if (window.isResized()) {
            viewport.setSize(window.getWidth(), window.getHeight());
        }
        viewport.getScene().doUpdate(tpf);
        gameRenderer.render(tpf);
        gameGUIPlatform.render(gameGUIPlatform.getGUIStage());
        gameGUIPlatform.render(gameGUIPlatform.getHUDStage());
        window.swapBuffers();
        updateFPS();

        engine.getEventBus().post(new RenderEvent.Post());
    }

    private long lastUpdateFps = System.currentTimeMillis();
    private int frameCount = 0;
    private int fps = 0;

    @Override
    public int getFPS() {
        return fps;
    }

    private void updateFPS() {
        long time = System.currentTimeMillis();
        if (time - lastUpdateFps > 1000) {
            fps = frameCount;
            frameCount = 0; // reset the FPS counter
            lastUpdateFps += 1000; // add one second
        }
        frameCount++;
    }

    public void dispose() {
        gameGUIPlatform.dispose();
        GraphicsEngine.stop();
    }
}
