package unknowndomain.engine.client.rendering;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.gui.EngineGuiManager;
import unknowndomain.engine.client.gui.GuiManager;
import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.client.rendering.camera.SimpleCamera;
import unknowndomain.engine.client.rendering.display.GLFWGameWindow;
import unknowndomain.engine.client.rendering.display.GameWindow;
import unknowndomain.engine.client.rendering.texture.EngineTextureManager;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.util.Disposable;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class EngineRenderContext implements RenderContext, Disposable {

    public static final int WINDOW_WIDTH = 854, WINDOW_HEIGHT = 480;

    private final EngineClient engine;
    private final Logger logger;

    private final List<Renderer> renderers = new LinkedList<>();

    private Thread renderThread;
    private GLFWGameWindow window;
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
    public GameWindow getWindow() {
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
        camera.update(partial);
        frustumIntersection.set(window.projection().mul(getCamera().getViewMatrix(), new Matrix4f()));

        window.beginRender();
        for (Renderer renderer : renderers) {
            renderer.render(partial);
        }
        window.endRender();
    }

    public void init(Thread renderThread) {
        this.renderThread = renderThread;

        logger.info("Initializing window!");
        window = new GLFWGameWindow(WINDOW_WIDTH, WINDOW_HEIGHT, UnknownDomain.getName());
        window.init();

        textureManager = new EngineTextureManager();
        guiManager = new EngineGuiManager(this);

        camera = new SimpleCamera(new Vector3f(0, 0, 0), new Vector3f(0, 0, -1));

        initRenderer();
    }

    public void initRenderer() {
        for (Renderer renderer : renderers) {
            renderer.init(this);
        }
    }

    public List<Renderer> getRenderers() {
        return renderers;
    }

    @Override
    public void dispose() {
        renderers.forEach(Disposable::dispose);
    }
}
