package engine.graphics;

import engine.client.EngineClient;
import engine.client.asset.AssetType;
import engine.client.asset.provider.TextureAssetProvider;
import engine.client.event.rendering.RenderEvent;
import engine.client.hud.HUDManager;
import engine.graphics.display.Window;
import engine.graphics.gl.texture.GLFrameBuffer;
import engine.graphics.internal.impl.gl.GLPlatform3D;
import engine.graphics.management.GraphicsBackend;
import engine.graphics.shape.SkyBox;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.texture.Texture2D;
import engine.graphics.viewport.PerspectiveViewport;
import engine.graphics.voxel.VoxelRenderHelper;
import engine.graphics.voxel.shape.SelectedBlock;
import engine.gui.EngineGUIManager;
import engine.gui.EngineHUDManager;
import engine.gui.GUIManager;
import engine.gui.GameGUIPlatform;
import engine.math.BlockPos;

public final class EngineRenderManager implements RenderManager {

    private final EngineClient engine;

    private Thread renderThread;
    private Window window;
    private Scene3D scene;
    private PerspectiveViewport viewport;
    private FrameBuffer frameBuffer;

    //    private GameRenderer gameRenderer;
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
    public Scene3D getScene() {
        return scene;
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

        GLPlatform3D.launchEmbedded();

        scene = new Scene3D();
        viewport = new PerspectiveViewport();
        viewport.setScene(scene);
        viewport.setClearMask(true, true, true);
        viewport.setSize(window.getWidth(), window.getHeight());
        frameBuffer = GLFrameBuffer.getDefaultFrameBuffer();
        viewport.show(frameBuffer);

        initTextureAssetProvider();
        VoxelRenderHelper.initialize(this);
//        gameRenderer = new GameRenderer(this);
        initScene();
        gameGUIPlatform = new GameGUIPlatform();
        guiManager = new EngineGUIManager(window, gameGUIPlatform.getGUIStage());
        hudManager = new EngineHUDManager(gameGUIPlatform.getHUDStage());

        window.show();
    }

    private void initScene() {
        scene.addNode(new Geometry(new SkyBox()));
        Geometry selectedBlock = new Geometry(new SelectedBlock());
        selectedBlock.setController((node, tpf) -> {
            if (!getEngine().isPlaying()) return;
            var player = getEngine().getCurrentGame().getClientPlayer();
            var camera = getViewport().getCamera();
            var hit = player.getWorld().raycastBlock(camera.getPosition(), camera.getFront(), 10);
            if (hit.isSuccess()) {
                BlockPos pos = hit.getPos();
                node.setTranslation(pos.x(), pos.y(), pos.z());
            }
        });
        scene.addNode(selectedBlock);
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

        if (window.isResized()) {
            viewport.setSize(window.getWidth(), window.getHeight());
        }
        if (engine.isPlaying()) {
            engine.getCurrentGame().getClientPlayer().getEntityController().updateCamera(viewport.getCamera(), tpf);
        }
        GraphicsEngine.doRender(tpf);
        gameGUIPlatform.render(gameGUIPlatform.getGUIStage(), frameBuffer);
        gameGUIPlatform.render(gameGUIPlatform.getHUDStage(), frameBuffer);
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
