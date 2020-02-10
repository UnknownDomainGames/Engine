package engine.graphics.vulkan;


import engine.graphics.display.Window;
import engine.graphics.display.WindowHelper;
import engine.graphics.glfw.GLFWContext;
import engine.graphics.glfw.GLFWVulkanWindow;
import engine.graphics.management.GraphicsBackend;
import engine.graphics.management.RenderHandler;
import engine.graphics.management.ResourceFactory;
import engine.graphics.util.GPUInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class VKGraphicsBackend implements GraphicsBackend {

    public static final Logger LOGGER = LoggerFactory.getLogger("Graphics");


    private Thread renderingThread;
    private GLFWVulkanWindow primaryWindow;
    private VKWindowHelper windowHelper;

    public VKGraphicsBackend() {

    }

    @Override
    public Thread getRenderingThread() {
        return renderingThread;
    }

    @Override
    public boolean isRenderingThread() {
        return Thread.currentThread() == renderingThread;
    }

    @Override
    public GPUInfo getGPUInfo() {
        return null;
    }

    @Override
    public WindowHelper getWindowHelper() {
        return windowHelper;
    }

    @Override
    public Window getPrimaryWindow() {
        return primaryWindow;
    }

    @Override
    public ResourceFactory getResourceFactory() {
        return null;
    }

    @Override
    public void attachHandler(RenderHandler handler) {

    }

    @Override
    public Future<Void> submitTask(Runnable runnable) {
        return null;
    }

    @Override
    public <V> Future<V> submitTask(Callable<V> callable) {
        return null;
    }

    @Override
    public void render(float partial) {

    }

    @Override
    public void init() {
        this.renderingThread = Thread.currentThread();
        initGLFW();
        initVulkan();
    }

    private void initGLFW() {
        GLFWContext.initialize();
        windowHelper = new VKWindowHelper();
        primaryWindow = (GLFWVulkanWindow) windowHelper.createWindow();
        primaryWindow.init();
    }

    private VulkanInstance vulkanInstance;

    private void initVulkan(){
        LOGGER.info("Initializing Vulkan instance!");

        var requiredExtensions = primaryWindow.getRequiredExtensions();
        if(requiredExtensions == null)
            throw new IllegalStateException("Unable to get extension required for Vulkan to load");
        vulkanInstance = VulkanInstance.createInstance(requiredExtensions, "",1,VulkanVersion.VER_1_0);
    }

    @Override
    public void dispose() {

    }
}
