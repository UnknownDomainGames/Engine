package nullengine.client.rendering;

import nullengine.client.EngineClient;
import nullengine.client.gui.EngineGuiManager;
import nullengine.client.gui.GuiManager;
import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.camera.FixedCamera;
import nullengine.client.rendering.display.GLFWWindow;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.texture.EngineTextureManager;
import nullengine.client.rendering.texture.TextureManager;
import nullengine.component.Component;
import nullengine.component.ComponentContainer;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class EngineRenderContext implements RenderContext {

    public static final int WINDOW_WIDTH = 854, WINDOW_HEIGHT = 480;

    private final EngineClient engine;
    private final Logger logger;

    private final List<Renderer> renderers = new LinkedList<>();

    private final ComponentContainer components = new ComponentContainer();

    private final EngineRenderScheduler scheduler = new EngineRenderScheduler();

    private Thread renderThread;
    private GLFWWindow window;
    private TextureManager textureManager;
    private GuiManager guiManager;

    private Camera camera;
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();

    public EngineRenderContext(EngineClient engine) {
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

        window.show();

        textureManager = new EngineTextureManager();
        guiManager = new EngineGuiManager(this);

        camera = new FixedCamera(new Vector3f(0, 0, 0), new Vector3f(0, 0, -1));

        initRenderer();
    }

    private void initGL() {
        logger.info("Initializing OpenGL context!");

        GL.createCapabilities();

        logger.info("----- OpenGL Information -----");
        logger.info("\tGL_VENDOR: {}", GL11.glGetString(GL11.GL_VENDOR));
        logger.info("\tGL_RENDERER: {}", GL11.glGetString(GL11.GL_RENDERER));
        logger.info("\tGL_VERSION: {}", GL11.glGetString(GL11.GL_VERSION));
        logger.info("\tGL_EXTENSIONS: {}", GL11.glGetString(GL11.GL_EXTENSIONS));
        logger.info("\tGL_SHADING_LANGUAGE_VERSION: {}", GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
        logger.info("------------------------------");

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
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
