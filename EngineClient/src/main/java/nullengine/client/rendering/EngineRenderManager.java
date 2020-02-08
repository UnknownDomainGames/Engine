package nullengine.client.rendering;

import nullengine.client.EngineClient;
import nullengine.client.asset.AssetType;
import nullengine.client.event.rendering.RenderEvent;
import nullengine.client.gui.EngineGUIManager;
import nullengine.client.gui.EngineHUDManager;
import nullengine.client.gui.GUIManager;
import nullengine.client.gui.GameGUIPlatform;
import nullengine.client.hud.HUDManager;
import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.game.GameRenderer;
import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.client.rendering.texture.EngineTextureManager;
import nullengine.client.rendering.texture.TextureManager;
import nullengine.client.rendering3d.Scene3D;
import nullengine.client.rendering3d.viewport.PerspectiveViewport;

public class EngineRenderManager implements RenderManager {

    private final EngineClient engine;

    private Thread renderThread;
    private Window window;
    private PerspectiveViewport viewport;

    private EngineTextureManager textureManager;

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
    public TextureManager getTextureManager() {
        return textureManager;
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

        RenderEngine.start(new RenderEngine.Settings());

        nullengine.client.rendering.management.RenderManager manager = RenderEngine.getManager();
        window = manager.getPrimaryWindow();
        window.setDisplayMode(engine.getSettings().getDisplaySettings().getDisplayMode(),
                engine.getSettings().getDisplaySettings().getResolutionWidth(),
                engine.getSettings().getDisplaySettings().getResolutionHeight(),
                engine.getSettings().getDisplaySettings().getFrameRate());

        viewport = new PerspectiveViewport();
        viewport.setScene(new Scene3D());
        viewport.setCamera(new Camera());

        initTexture();
        gameRenderer = new GameRenderer(this);
        gameGUIPlatform = new GameGUIPlatform();
        guiManager = new EngineGUIManager(window, gameGUIPlatform.getGUIStage());
        hudManager = new EngineHUDManager();

        window.show();
    }

    private void initTexture() {
        textureManager = new EngineTextureManager();
        TextureManager.Internal.setInstance(textureManager);
        getEngine().getAssetManager().register(AssetType.builder(GLTexture2D.class).name("Texture").provider(textureManager).parentLocation("texture").extensionName(".png").build());
    }

    public void render(float tpf) {
        engine.getEventBus().post(new RenderEvent.Pre());

        RenderEngine.doRender(tpf);
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
        RenderEngine.stop();
    }
}
