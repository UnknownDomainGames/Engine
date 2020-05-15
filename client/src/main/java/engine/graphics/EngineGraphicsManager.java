package engine.graphics;

import engine.client.ClientEngine;
import engine.client.asset.AssetType;
import engine.client.asset.provider.TextureAssetProvider;
import engine.client.event.graphics.RenderEvent;
import engine.client.hud.HUDManager;
import engine.graphics.backend.GraphicsBackend;
import engine.graphics.display.Window;
import engine.graphics.graph.*;
import engine.graphics.internal.graph.ShadowOpaqueDrawDispatcher;
import engine.graphics.internal.graph.ViewportOpaqueDrawDispatcher;
import engine.graphics.internal.graph.ViewportSkyDrawDispatcher;
import engine.graphics.light.DirectionalLight;
import engine.graphics.sky.SkyBox;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.BlendMode;
import engine.graphics.util.CullMode;
import engine.graphics.viewport.PerspectiveViewport;
import engine.graphics.voxel.VoxelGraphicsHelper;
import engine.graphics.voxel.shape.SelectedBlock;
import engine.gui.EngineGUIManager;
import engine.gui.EngineGUIPlatform;
import engine.gui.EngineHUDManager;
import engine.gui.GUIManager;
import engine.gui.internal.impl.graphics.StageDrawDispatcher;
import engine.math.BlockPos;
import engine.util.Color;
import engine.util.RuntimeEnvironment;

import static engine.graphics.graph.ColorOutputInfo.colorOutput;
import static engine.graphics.graph.DepthOutputInfo.depthOutput;

public final class EngineGraphicsManager implements GraphicsManager {

    private final ClientEngine engine;

    private Thread renderThread;
    private Window window;
    private RenderGraph renderGraph;
    private Scene3D scene;
    private PerspectiveViewport viewport;

    private EngineGUIPlatform guiPlatform;
    private EngineGUIManager guiManager;
    private EngineHUDManager hudManager;

    public EngineGraphicsManager(ClientEngine engine, Thread renderThread) {
        this.engine = engine;
        this.renderThread = renderThread;
        initialize();
    }

    @Override
    public ClientEngine getEngine() {
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
    public RenderGraph getRenderGraph() {
        return renderGraph;
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

    private void initialize() {
        Internal.setInstance(this);

        System.setProperty(GraphicsEngine.DEBUG_PROPERTY,
                "false".equals(System.getProperty(GraphicsEngine.DEBUG_PROPERTY, "false")) ||
                        getEngine().getRuntimeEnvironment() != RuntimeEnvironment.DEPLOYMENT ? "true" : "false");
        GraphicsEngine.start(new GraphicsEngine.Settings());

        GraphicsBackend backend = GraphicsEngine.getGraphicsBackend();
        window = backend.getPrimaryWindow();

        scene = new Scene3D();
        viewport = new PerspectiveViewport();
        viewport.setScene(scene);

        initTextureAssetProvider();
        guiPlatform = new EngineGUIPlatform();
        hudManager = new EngineHUDManager(guiPlatform.getHUDStage());
        guiManager = new EngineGUIManager(window, guiPlatform.getGUIStage(), hudManager);

        renderGraph = GraphicsEngine.getGraphicsBackend().loadRenderGraph(createRenderGraph());
        renderGraph.bindWindow(window);

        engine.getSettings().getDisplaySettings().apply();
        window.centerOnScreen();
        window.show();
    }

    public void initScene() {
        VoxelGraphicsHelper.initialize(this);
        scene.addNode(new SkyBox());
        Geometry selectedBlock = new SelectedBlock();
        selectedBlock.setController((node, tpf) -> {
            if (!getEngine().isPlaying()) return;
            var player = getEngine().getClientGame().getClientPlayer();
            if (player == null) return;
            var camera = getViewport().getCamera();
            var hit = player.getWorld().raycastBlock(camera.getPosition(), camera.getFront(), 10);
            selectedBlock.setVisible(hit.isSuccess());
            if (hit.isSuccess()) {
                BlockPos pos = hit.getPos();
                node.setTranslation(pos.x(), pos.y(), pos.z());
            }
        });
        scene.addNode(selectedBlock);
        scene.getLightManager().add(new DirectionalLight().setColor(Color.WHITE).setDirection(1, -1, 1).setIntensity(1f));
        scene.getLightManager().setAmbientLight(0.5f);
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

    public void doRender(float timeToLastUpdate) {
        engine.getEventBus().post(new RenderEvent.Pre());

        if (window.isResized()) {
            viewport.setSize(window.getWidth(), window.getHeight());
        }

        if (engine.isPlaying()) {
            var player = engine.getClientGame().getClientPlayer();
            if (player != null && player.getEntityController() != null) {
                player.getEntityController().updateCamera(viewport.getCamera(), timeToLastUpdate);
            }
        }

        GraphicsEngine.doRender(timeToLastUpdate);
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
            mainTask.addSetup((frameContext, renderTask) -> {
                Frame frame = frameContext.getFrame();
                if (frame.isResized()) viewport.setSize(frame.getOutputWidth(), frame.getOutputHeight());
                viewport.getScene().doUpdate(frame.getTimeToLastUpdate());
            });
            {
                RenderBufferInfo colorBuffer = RenderBufferInfo.renderBuffer();
                colorBuffer.setName("color");
                colorBuffer.setFormat(ColorFormat.RGB8);
                colorBuffer.setRelativeSize(1, 1);
                mainTask.addRenderBuffers(colorBuffer);

                RenderBufferInfo depthBuffer = RenderBufferInfo.renderBuffer();
                depthBuffer.setName("depth");
                depthBuffer.setFormat(ColorFormat.DEPTH24);
                depthBuffer.setRelativeSize(1, 1);
                mainTask.addRenderBuffers(depthBuffer);

                RenderBufferInfo shadowBuffer = RenderBufferInfo.renderBuffer();
                shadowBuffer.setName("shadow");
                shadowBuffer.setFormat(ColorFormat.DEPTH24);
                shadowBuffer.setFixedSize(1024, 1024);
                mainTask.addRenderBuffers(shadowBuffer);
            }
            {
                RenderPassInfo skyPass = RenderPassInfo.renderPass();
                skyPass.setName("sky");
                skyPass.setCullMode(CullMode.CULL_BACK);
                skyPass.addColorOutputs(colorOutput().setClear(true).setColorBuffer("color"));
                skyPass.setDepthOutput(depthOutput().setClear(true).setWritable(false).setDepthBuffer("depth"));
                {
                    DrawerInfo skyDrawer = DrawerInfo.drawer();
                    skyDrawer.setShader("sky");
                    skyDrawer.setDrawDispatcher(new ViewportSkyDrawDispatcher(viewport));
                    skyPass.addDrawers(skyDrawer);
                }

                RenderPassInfo shadowPass = RenderPassInfo.renderPass();
                shadowPass.setName("shadow");
                shadowPass.dependsOn("sky");
                shadowPass.setCullMode(CullMode.CULL_FRONT);
                shadowPass.setDepthOutput(depthOutput().setClear(true).setDepthBuffer("shadow"));
                {
                    DrawerInfo sceneDrawer = DrawerInfo.drawer();
                    sceneDrawer.setShader("shadow");
                    sceneDrawer.setDrawDispatcher(new ShadowOpaqueDrawDispatcher(viewport));
                    shadowPass.addDrawers(sceneDrawer);
                }

                RenderPassInfo opaquePass = RenderPassInfo.renderPass();
                opaquePass.setName("opaque");
                opaquePass.dependsOn("sky");
                opaquePass.setCullMode(CullMode.CULL_BACK);
                opaquePass.addColorOutputs(colorOutput().setColorBuffer("color"));
                opaquePass.setDepthOutput(depthOutput().setDepthBuffer("depth"));
                {
                    DrawerInfo sceneDrawer = DrawerInfo.drawer();
                    sceneDrawer.setShader("opaque");
                    sceneDrawer.setDrawDispatcher(new ViewportOpaqueDrawDispatcher(viewport));
                    opaquePass.addDrawers(sceneDrawer);
                }

                RenderPassInfo guiPass = RenderPassInfo.renderPass();
                guiPass.setName("gui");
                guiPass.dependsOn("opaque");
                guiPass.addColorOutputs(colorOutput()
                        .setColorBuffer("color")
                        .setBlendMode(BlendMode.MIX));
                {
                    DrawerInfo hudDrawer = DrawerInfo.drawer();
                    hudDrawer.setShader("gui");
                    hudDrawer.setDrawDispatcher(new StageDrawDispatcher(guiPlatform.getHUDStage()));

                    DrawerInfo guiDrawer = DrawerInfo.drawer();
                    guiDrawer.setShader("gui");
                    guiDrawer.setDrawDispatcher(new StageDrawDispatcher(guiPlatform.getGUIStage()));

                    guiPass.addDrawers(hudDrawer, guiDrawer);
                }

                mainTask.addPasses(skyPass, opaquePass, guiPass);
            }
            renderGraph.addTasks(mainTask);
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
        guiPlatform.dispose();
        GraphicsEngine.stop();
    }
}
