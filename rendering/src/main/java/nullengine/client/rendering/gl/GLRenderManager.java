package nullengine.client.rendering.gl;

import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.WindowHelper;
import nullengine.client.rendering.font.Font;
import nullengine.client.rendering.font.FontHelper;
import nullengine.client.rendering.font.WindowsFontHelper;
import nullengine.client.rendering.gl.util.DebugMessageCallback;
import nullengine.client.rendering.gl.util.GPUInfoImpl;
import nullengine.client.rendering.glfw.GLFWContext;
import nullengine.client.rendering.glfw.GLFWWindow;
import nullengine.client.rendering.management.RenderHandler;
import nullengine.client.rendering.management.RenderManager;
import nullengine.client.rendering.management.ResourceFactory;
import nullengine.client.rendering.util.Cleaner;
import nullengine.client.rendering.util.GPUInfo;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import static nullengine.client.rendering.gl.util.GLContextUtils.*;

public final class GLRenderManager implements RenderManager {

    public static final Logger LOGGER = LoggerFactory.getLogger("Rendering");

    private Thread renderingThread;
    private GLCapabilities capabilities;
    private GPUInfo gpuInfo;
    private WindowHelper windowHelper;
    private GLFWWindow primaryWindow;
    private GLResourceFactory resourceFactory;

    private final List<RenderHandler> handlers = new ArrayList<>();
    private final List<RunnableFuture<?>> pendingTasks = new ArrayList<>();

    public GLRenderManager() {
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
    public ResourceFactory getResourceFactory() {
        return resourceFactory;
    }

    @Override
    public GPUInfo getGPUInfo() {
        return gpuInfo;
    }

    @Override
    public WindowHelper getWindowHelper() {
        return windowHelper;
    }

    @Override
    public void attachHandler(RenderHandler handler) {
        handler.init(this);
        this.handlers.add(handler);
    }

    @Override
    public Future<Void> submitTask(Runnable runnable) {
        FutureTask<Void> task = new FutureTask<>(runnable, null);
        submitTask(task);
        return task;
    }

    @Override
    public <V> Future<V> submitTask(Callable<V> callable) {
        FutureTask<V> task = new FutureTask<>(callable);
        submitTask(task);
        return task;
    }

    private void submitTask(RunnableFuture<?> task) {
        if (Thread.currentThread() == renderingThread) {
            task.run();
            return;
        }

        synchronized (pendingTasks) {
            pendingTasks.add(task);
        }
    }

    @Override
    public void render(float tpf) {
        Cleaner.clean();
        runPendingTasks();
        for (RenderHandler handler : handlers) {
            handler.render(tpf);
        }
        GLFW.glfwPollEvents();
    }

    private void runPendingTasks() {
        synchronized (pendingTasks) {
            if (pendingTasks.isEmpty()) return;
            for (RunnableFuture<?> task : pendingTasks) {
                if (task.isCancelled()) continue;
                task.run();
            }
            pendingTasks.clear();
        }
    }

    @Override
    public void init() {
        this.renderingThread = Thread.currentThread();
        initGLFW();
        initGL();
        initFont();
    }

    private void initGLFW() {
        GLFWContext.initialize();
        windowHelper = new GLWindowHelper();
        primaryWindow = new GLFWWindow();
        primaryWindow.init();
    }

    private void initGL() {
        LOGGER.info("Initializing OpenGL context!");

        capabilities = GL.createCapabilities(true);
        setCapabilities(capabilities);
        gpuInfo = new GPUInfoImpl();
        printGLInfo();

        initDebugMessageCallback();

        resourceFactory = new GLResourceFactory(renderingThread);
    }

    private void initDebugMessageCallback() {
        DebugMessageCallback callback = new DebugMessageCallback() {
            @Override
            public void invoke(Source source, Type type, int id, Severity severity, String message, long userParam) {
                LOGGER.debug("OpenGL Debug Message:\n" +
                        "\tSource: " + source + "\n" +
                        "\tType: " + type + "\n" +
                        "\tId: " + id + "\n" +
                        "\tSeverity: " + severity + "\n" +
                        "\tMessage: " + message);
            }
        };
        if (capabilities.OpenGL43) {
            GL43.glDebugMessageCallback(callback::invoke, 0);
        } else if (capabilities.GL_ARB_debug_output) {
            ARBDebugOutput.glDebugMessageCallbackARB(callback::invoke, 0);
        } else {
            LOGGER.warn("Unsupported debug message callback.");
        }
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

    @Override
    public void dispose() {
        for (RenderHandler handler : handlers) {
            handler.dispose();
        }
        GLFWContext.terminate();
    }
}
