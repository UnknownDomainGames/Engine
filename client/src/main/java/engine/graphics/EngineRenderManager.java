package engine.graphics;

import engine.client.EngineClient;
import engine.client.asset.AssetType;
import engine.client.asset.provider.TextureAssetProvider;
import engine.client.event.rendering.RenderEvent;
import engine.client.hud.HUDManager;
import engine.graphics.backend.GraphicsBackend;
import engine.graphics.display.Window;
import engine.graphics.graph.*;
import engine.graphics.internal.graph.ViewportOpaqueDrawDispatcher;
import engine.graphics.shape.SkyBox;
import engine.graphics.texture.Texture2D;
import engine.graphics.texture.TextureFormat;
import engine.graphics.util.BlendMode;
import engine.graphics.util.CullMode;
import engine.graphics.viewport.PerspectiveViewport;
import engine.graphics.voxel.VoxelRenderHelper;
import engine.graphics.voxel.shape.SelectedBlock;
import engine.gui.*;
import engine.math.BlockPos;

import static engine.graphics.graph.ColorOutputInfo.colorOutput;
import static engine.graphics.graph.DepthOutputInfo.depthOutput;

public final class EngineRenderManager implements RenderManager {

    private final EngineClient engine;

    private Thread renderThread;
    private Window window;
    private Scene3D scene;
    private PerspectiveViewport viewport;

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

        scene = new Scene3D();
        viewport = new PerspectiveViewport();
        viewport.setScene(scene);
        viewport.setSize(window.getWidth(), window.getHeight());

        initTextureAssetProvider();
        VoxelRenderHelper.initialize(this);
        initScene();
        gameGUIPlatform = new GameGUIPlatform();
        hudManager = new EngineHUDManager(gameGUIPlatform.getHUDStage());
        guiManager = new EngineGUIManager(window, gameGUIPlatform.getGUIStage(), hudManager);

        RenderGraph renderGraph = GraphicsEngine.getGraphicsBackend().loadRenderGraph(createRenderGraph());
        renderGraph.bindWindow(window);
        window.show();
    }

    private void initScene() {
        scene.addNode(new SkyBox());
        Geometry selectedBlock = new SelectedBlock();
        selectedBlock.setController((node, tpf) -> {
            if (!getEngine().isPlaying()) return;
            var player = getEngine().getCurrentGame().getClientPlayer();
            var camera = getViewport().getCamera();
            var hit = player.getWorld().raycastBlock(camera.getPosition(), camera.getFront(), 10);
            selectedBlock.setVisible(hit.isSuccess());
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
        updateFPS();

        engine.getEventBus().post(new RenderEvent.Post());
    }

    private RenderGraphInfo createRenderGraph() {
        RenderGraphInfo renderGraph = RenderGraphInfo.renderGraph();
        renderGraph.setMainTask("main");
        {
            RenderTaskInfo mainTask = RenderTaskInfo.renderTask();
            mainTask.setName("main");
            mainTask.setFinalPass("gui");
            {
                RenderBufferInfo colorBuffer = RenderBufferInfo.renderBuffer();
                colorBuffer.setName("color");
                colorBuffer.setFormat(TextureFormat.RGB8);
                colorBuffer.setRelativeSize(1, 1);

                RenderBufferInfo depthBuffer = RenderBufferInfo.renderBuffer();
                depthBuffer.setName("depth");
                depthBuffer.setFormat(TextureFormat.DEPTH24);
                depthBuffer.setRelativeSize(1, 1);

                mainTask.setRenderBuffers(colorBuffer, depthBuffer);
            }
            {
                RenderPassInfo opaquePass = RenderPassInfo.renderPass();
                opaquePass.setName("opaque");
                opaquePass.setCullMode(CullMode.CULL_BACK);
                opaquePass.setColorOutputs(colorOutput()
                        .setClear(true)
                        .setColorBuffer("color"));
                opaquePass.setDepthOutput(depthOutput()
                        .setClear(true)
                        .setDepthBuffer("depth"));
                {
                    DrawerInfo sceneDrawer = DrawerInfo.drawer();
                    sceneDrawer.setShader("opaque");
                    sceneDrawer.setDrawDispatcher(new ViewportOpaqueDrawDispatcher(viewport));
                    opaquePass.setDrawers(sceneDrawer);
                }

                RenderPassInfo guiPass = RenderPassInfo.renderPass();
                guiPass.setName("gui");
                guiPass.dependsOn("opaque");
                guiPass.setCullMode(CullMode.CULL_BACK);
                guiPass.setColorOutputs(colorOutput()
                        .setColorBuffer("color")
                        .setBlendMode(BlendMode.MIX));
                {
                    DrawerInfo sceneDrawer = DrawerInfo.drawer();
                    sceneDrawer.setShader("gui");
                    sceneDrawer.setDrawDispatcher(new GameGUIDrawDispatcher(gameGUIPlatform.getGUIStage(),
                            gameGUIPlatform.getHUDStage()));
                    guiPass.setDrawers(sceneDrawer);
                }

                mainTask.setPasses(opaquePass, guiPass);
            }
            renderGraph.setTasks(mainTask);
        }
        return renderGraph;
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
