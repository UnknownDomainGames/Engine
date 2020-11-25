package engine.graphics.gl;

import engine.graphics.backend.GraphicsBackend;
import engine.graphics.backend.GraphicsBackendFactory;
import engine.graphics.backend.ResourceFactory;
import engine.graphics.display.Window;
import engine.graphics.display.WindowHelper;
import engine.graphics.gl.graph.GLRenderGraph;
import engine.graphics.gl.util.DebugMessageCallback;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.gl.util.GPUInfoImpl;
import engine.graphics.glfw.GLFWContext;
import engine.graphics.glfw.GLFWWindow;
import engine.graphics.graph.RenderGraph;
import engine.graphics.graph.RenderGraphInfo;
import engine.graphics.util.Cleaner;
import engine.graphics.util.GPUInfo;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
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

public final class GLGraphicsBackend implements GraphicsBackend {

    public static final String BACKEND_NAME = "opengl";

    public static final String OPENGL_DEBUG_PROPERTY = "opengl.debug";

    public static final Logger LOGGER = LoggerFactory.getLogger("Graphics");

    private Thread renderingThread;
    private GLCapabilities capabilities;
    private GPUInfo gpuInfo;
    private WindowHelper windowHelper;
    private GLFWWindow primaryWindow;
    private GLResourceFactory resourceFactory;

    private final List<GLRenderGraph> renderGraphs = new ArrayList<>();
    private final List<RunnableFuture<?>> pendingTasks = new ArrayList<>();

    @Override
    public String getName() {
        return BACKEND_NAME;
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
    public RenderGraph loadRenderGraph(RenderGraphInfo renderGraph) {
        GLRenderGraph glRenderGraph = new GLRenderGraph(renderGraph);
        renderGraphs.add(glRenderGraph);
        return glRenderGraph;
    }

    @Override
    public void removeRenderGraph(RenderGraph renderGraph) {
        GLRenderGraph glRenderGraph = (GLRenderGraph) renderGraph;
        if (renderGraphs.remove(glRenderGraph)) {
            glRenderGraph.dispose();
        }
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
    public void render(float timeToLastUpdate) {
        Cleaner.clean();
        runPendingTasks();
        renderGraphs.forEach(renderGraph -> renderGraph.draw(timeToLastUpdate));
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
    }

    private void initGLFW() {
        GLFWContext.initialize();
        windowHelper = new GLWindowHelper();
        primaryWindow = new GLFWWindow();
        primaryWindow.init();
    }

    private void initGL() {
        LOGGER.info("Initializing OpenGL context!");

        capabilities = GL.createCapabilities();
        gpuInfo = new GPUInfoImpl();
        GLHelper.setup(capabilities, gpuInfo.getVendor());
        printGLInfo();

        if (Boolean.parseBoolean(System.getProperty(OPENGL_DEBUG_PROPERTY, "false"))) {
            initDebugMessageCallback();
        }

        resourceFactory = new GLResourceFactory(renderingThread);
    }

    private void initDebugMessageCallback() {
        try {
            GLHelper.setDebugMessageCallback(new DebugMessageCallback() {
                @Override
                public void invoke(Source source, Type type, int id, Severity severity, String message, long userParam) {
                    var msg = "GL " + source + (type == Type.ERROR ? "" : " " + type) + " (" + id + ") " + message;
                    switch (severity) {
                        case HIGH:
                            LOGGER.error(msg);
                            break;
                        case MEDIUM:
                            LOGGER.warn(msg);
                            break;
                        case LOW:
                        case NOTIFICATION:
                            LOGGER.info(msg);
                            break;
                    }
                }
            });
        } catch (UnsupportedOperationException e) {
            LOGGER.warn("Unsupported debug message callback.");
        }
    }

    private void printGLInfo() {
        LOGGER.info("----- OpenGL Information -----");
        LOGGER.info("\tGL_VENDOR: {}", GLHelper.getVendor());
        LOGGER.info("\tGL_RENDERER: {}", GLHelper.getRenderer());
        LOGGER.info("\tGL_VERSION: {}", GLHelper.getVersion());
        LOGGER.info("\tGL_EXTENSIONS: {}", GLHelper.getExtensions());
        LOGGER.info("\tGL_SHADING_LANGUAGE_VERSION: {}", GLHelper.getShadingLanguageVersion());
        LOGGER.info("\tGPU_TOTAL_MEMORY: {} MB", (gpuInfo.getTotalMemory()) >> 10);
        LOGGER.info("------------------------------");
    }

    @Override
    public void dispose() {
        GLFWContext.terminate();
    }

    public GLCapabilities getCapabilities() {
        return capabilities;
    }

    public static final class Factory implements GraphicsBackendFactory {

        @Override
        public String getName() {
            return BACKEND_NAME;
        }

        @Override
        public GraphicsBackend create() {
            return new GLGraphicsBackend();
        }
    }
}
