package nullengine.client.rendering;

import nullengine.client.EngineClient;
import nullengine.client.asset.AssetType;
import nullengine.client.gui.EngineGuiManager;
import nullengine.client.gui.GuiManager;
import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.camera.FixedCamera;
import nullengine.client.rendering.display.GLFWWindow;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.font.Font;
import nullengine.client.rendering.font.FontHelper;
import nullengine.client.rendering.font.TTFontHelper;
import nullengine.client.rendering.texture.EngineTextureManager;
import nullengine.client.rendering.texture.GLTexture;
import nullengine.client.rendering.texture.TextureManager;
import nullengine.client.rendering.util.GLInfo;
import nullengine.client.rendering.util.GLInfoImpl;
import nullengine.client.rendering.util.GPUMemoryInfo;
import nullengine.client.rendering.util.NVXGPUMemoryInfo;
import nullengine.component.Component;
import nullengine.component.ComponentContainer;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class EngineRenderManager implements RenderManager {

    public static final int WINDOW_WIDTH = 854, WINDOW_HEIGHT = 480;

    private final EngineClient engine;
    private final Logger logger;

    private final List<Renderer> renderers = new LinkedList<>();

    private final ComponentContainer components = new ComponentContainer();

    private final EngineRenderScheduler scheduler = new EngineRenderScheduler();

    private Thread renderThread;
    private GLFWWindow window;
    private EngineTextureManager textureManager;
    private GuiManager guiManager;
    private GLInfo glInfo;
    private GPUMemoryInfo gpuMemoryInfo;

    private Camera camera;
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();

    public EngineRenderManager(EngineClient engine) {
        this.engine = engine;
        this.logger = engine.getLogger();
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
    public TextureManager getTextureManager() {
        return textureManager;
    }

    @Override
    public GuiManager getGuiManager() {
        return guiManager;
    }

    @Override
    public RenderScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public GLInfo getGLInfo() {
        return glInfo;
    }

    @Override
    public GPUMemoryInfo getGPUMemoryInfo() {
        return gpuMemoryInfo;
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

    @Override
    @Nonnull
    public Camera getCamera() {
        return camera;
    }

    @Override
    public void setCamera(@Nonnull Camera camera) {
        this.camera = Objects.requireNonNull(camera);
    }

    @Override
    public FrustumIntersection getFrustumIntersection() {
        return frustumIntersection;
    }

    public void render(float partial) {
        scheduler.run();

        camera.update(partial);
        frustumIntersection.set(window.projection().mul(camera.getViewMatrix(), new Matrix4f()));

        window.beginRender();
        for (Renderer renderer : renderers) {
            renderer.render(partial);
        }
        window.endRender();
        updateFPS();
    }

    public void init(Thread renderThread) {
        this.renderThread = renderThread;

        logger.info("Initializing window!");
        window = new GLFWWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "");
        window.init();
        engine.addShutdownListener(window::dispose);

        initGL();

        initFont();
        initTexture();
        guiManager = new EngineGuiManager(this);

        camera = new FixedCamera(new Vector3f(0, 0, 0), new Vector3f(0, 0, -1));

        initRenderer();

        window.show();
    }

    private void initGL() {
        logger.info("Initializing OpenGL context!");

        GL.createCapabilities();
        glInfo = new GLInfoImpl();
        logger.info("----- OpenGL Information -----");
        logger.info("\tGL_VENDOR: {}", glInfo.getVendor());
        logger.info("\tGL_RENDERER: {}", glInfo.getRenderer());
        logger.info("\tGL_VERSION: {}", glInfo.getVersion());
        logger.info("\tGL_EXTENSIONS: {}", glInfo.getExtensions());
        logger.info("\tGL_SHADING_LANGUAGE_VERSION: {}", glInfo.getShadingLanguageVersion());
        logger.info("------------------------------");

        NVXGPUMemoryInfo nvxgpuMemoryInfo = new NVXGPUMemoryInfo();
        nvxgpuMemoryInfo.init();
        scheduler.runTaskEveryFrame(nvxgpuMemoryInfo::update);
        gpuMemoryInfo = nvxgpuMemoryInfo;

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    private void initTexture() {
        textureManager = new EngineTextureManager();
        TextureManager.Internal.setInstance(textureManager);
        getEngine().getAssetManager().register(AssetType.builder(GLTexture.class).name("Texture").provider(textureManager).parentLocation("texture").extensionName(".png").build());
    }

    private void initFont() {
        var fontHelper = new TTFontHelper();
        FontHelper.Internal.setInstance(fontHelper);
        Font defaultFont = new Font("Arial", "Regular", 16);
        fontHelper.setDefaultFont(defaultFont);
    }

    public void initRenderer() {
        for (Renderer renderer : renderers) {
            renderer.init(this);
        }
    }

    public List<Renderer> getRenderers() {
        return renderers;
    }

    public void dispose() {
        renderers.forEach(Renderer::dispose);
    }

    @Override
    public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
        return components.getComponent(type);
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return components.hasComponent(type);
    }

    @Override
    public <T extends Component> void setComponent(@Nonnull Class<T> type, @Nullable T value) {
        components.setComponent(type, value);
    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {
        components.removeComponent(type);
    }

    @Override
    @Nonnull
    public Set<Class<?>> getComponents() {
        return components.getComponents();
    }
}
