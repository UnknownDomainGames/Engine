package nullengine.client.rendering.gl;

import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.font.Font;
import nullengine.client.rendering.font.FontHelper;
import nullengine.client.rendering.gl.font.WindowsFontHelper;
import nullengine.client.rendering.gl.pipeline.ForwardPipeline;
import nullengine.client.rendering.gl.util.NVXGPUInfo;
import nullengine.client.rendering.glfw.GLFWContext;
import nullengine.client.rendering.glfw.GLFWWindow;
import nullengine.client.rendering.management.RenderListener;
import nullengine.client.rendering.management.RenderManager;
import nullengine.client.rendering.scene.PerspectiveViewPort;
import nullengine.client.rendering.scene.Scene;
import nullengine.client.rendering.scene.ViewPort;
import nullengine.client.rendering.util.GPUInfo;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static nullengine.client.rendering.gl.util.GLContextUtils.*;

public class GLRenderManager implements RenderManager {

    public static final Logger LOGGER = LoggerFactory.getLogger("Rendering");

    private final RenderListener listener;

    private Thread renderThread;
    private GLFWWindow primaryWindow;

    private ViewPort primaryViewPort;
    private Scene scene;

    private GPUInfo gpuInfo;

    private ForwardPipeline forwardPipeline;

    public GLRenderManager(RenderListener listener) {
        this.listener = listener;
    }

    @Nonnull
    @Override
    public Thread getRenderingThread() {
        return renderThread;
    }

    @Override
    public boolean isRenderingThread() {
        return Thread.currentThread() == renderThread;
    }

    @Nonnull
    @Override
    public Window getPrimaryWindow() {
        return primaryWindow;
    }

    public ViewPort getPrimaryViewPort() {
        return primaryViewPort;
    }

    public void setPrimaryViewPort(ViewPort viewPort) {
        this.primaryViewPort = viewPort;
    }

    @Override
    public GPUInfo getGPUInfo() {
        return gpuInfo;
    }

    @Override
    public void render(float partial) {
        if (listener != null) listener.onPreRender(this);

        if (scene != null) {
            scene.doUpdate(partial);
        }

        forwardPipeline.render(this, new PerspectiveViewPort());

        primaryWindow.swapBufferAndPollEvents();

        if (listener != null) listener.onPostRender(this);
    }

    @Override
    public void init() {
        if (listener != null) listener.onPreInitialize();

        this.renderThread = Thread.currentThread();

        LOGGER.info("Initializing window!");
        GLFWContext.initialize();
        primaryWindow = new GLFWWindow();
        primaryWindow.init();
//        window.setDisplayMode(Platform.getEngineClient().getSettings().getDisplaySettings().getDisplayMode(), Platform.getEngineClient().getSettings().getDisplaySettings().getResolutionWidth(), Platform.getEngineClient().getSettings().getDisplaySettings().getResolutionHeight(), Platform.getEngineClient().getSettings().getDisplaySettings().getFrameRate());

        initGL();
        initFont();
        initRenderPipeline();
//        initTexture();
//        guiManager = new EngineGuiManager(this);

        primaryWindow.show();

        if (listener != null) listener.onInitialized(this);
    }

    private void initGL() {
        LOGGER.info("Initializing OpenGL context!");

        GL.createCapabilities();
        gpuInfo = new NVXGPUInfo();
        printGLInfo();

        ARBDebugOutput.glDebugMessageCallbackARB((source, type, id, severity, length, message, userParam) -> {

        }, 0);
    }

    private void printGLInfo() {
        LOGGER.info("----- OpenGL Information -----");
        LOGGER.info("\tGL_VENDOR: {}", getVendor());
        LOGGER.info("\tGL_RENDERER: {}", getRenderer());
        LOGGER.info("\tGL_VERSION: {}", getVersion());
        LOGGER.info("\tGL_EXTENSIONS: {}", getExtensions());
        LOGGER.info("\tGL_SHADING_LANGUAGE_VERSION: {}", getShadingLanguageVersion());
        LOGGER.info("\tGPU_TOTAL_MEMORY: {} MB", (gpuInfo.getTotalMemory()) >> 10);
        LOGGER.info("------------------------------");
    }

    private void initFont() {
        var fontHelper = new WindowsFontHelper();
        FontHelper.Internal.setInstance(fontHelper);
        Font defaultFont = new Font("Arial", "Regular", 16);
        fontHelper.setDefaultFont(defaultFont);
    }

    private void initRenderPipeline() {
        forwardPipeline = new ForwardPipeline();
    }

    @Override
    public void dispose() {
        if (primaryWindow != null) primaryWindow.dispose();

        if (listener != null) listener.onDisposed(this);

        GLFWContext.terminate();
    }
}
