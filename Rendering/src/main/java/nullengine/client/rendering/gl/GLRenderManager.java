package nullengine.client.rendering.gl;

import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.font.Font;
import nullengine.client.rendering.font.FontHelper;
import nullengine.client.rendering.gl.font.WindowsFontHelper;
import nullengine.client.rendering.gl.pipeline.ForwardPipeline;
import nullengine.client.rendering.gl.util.NVXGPUInfo;
import nullengine.client.rendering.glfw.GLFWContext;
import nullengine.client.rendering.glfw.GLFWWindow;
import nullengine.client.rendering.management.RenderManager;
import nullengine.client.rendering.management.SwapBuffersListener;
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

    private final SwapBuffersListener listener;

    private Thread renderingThread;
    private GLFWWindow primaryWindow;

    private ViewPort primaryViewPort;

    private GPUInfo gpuInfo;

    private ForwardPipeline forwardPipeline;

    public GLRenderManager(SwapBuffersListener listener) {
        this.listener = listener;
    }

    @Nonnull
    @Override
    public Thread getRenderingThread() {
        return renderingThread;
    }

    @Override
    public boolean isRenderingThread() {
        return Thread.currentThread() == renderingThread;
    }

    @Nonnull
    @Override
    public Window getPrimaryWindow() {
        return primaryWindow;
    }

    @Override
    public ViewPort getPrimaryViewPort() {
        return primaryViewPort;
    }

    @Override
    public void setPrimaryViewPort(ViewPort viewPort) {
        this.primaryViewPort = viewPort;
    }

    @Override
    public GPUInfo getGPUInfo() {
        return gpuInfo;
    }

    @Override
    public void render(float partial) {
        renderViewPort(primaryViewPort, partial);
        listener.onPreSwapBuffers(this, partial);
        primaryWindow.swapBuffers();
    }

    private void renderViewPort(ViewPort viewPort, float partial) {
        if (viewPort == null) return;
        forwardPipeline.render(this, viewPort, partial);
    }

    @Override
    public void init() {
        this.renderingThread = Thread.currentThread();
        initWindow();
        initGL();
        initRenderPipeline();
        initFont();
    }

    private void initWindow() {
        GLFWContext.initialize();
        primaryWindow = new GLFWWindow();
        primaryWindow.init();
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

    private void initRenderPipeline() {
        forwardPipeline = new ForwardPipeline();
    }

    private void initFont() {
        var fontHelper = new WindowsFontHelper();
        FontHelper.Internal.setInstance(fontHelper);
        Font defaultFont = new Font("Arial", "Regular", 16);
        fontHelper.setDefaultFont(defaultFont);
    }

    @Override
    public void dispose() {
        GLFWContext.terminate();
    }
}
