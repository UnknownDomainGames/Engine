package engine.graphics.vulkan;


import engine.graphics.GraphicsEngine;
import engine.graphics.display.Window;
import engine.graphics.display.WindowHelper;
import engine.graphics.glfw.GLFWContext;
import engine.graphics.glfw.GLFWVulkanWindow;
import engine.graphics.graph.RenderGraph;
import engine.graphics.graph.RenderGraphInfo;
import engine.graphics.management.GraphicsBackend;
import engine.graphics.management.GraphicsBackendFactory;
import engine.graphics.management.ResourceFactory;
import engine.graphics.util.Cleaner;
import engine.graphics.util.GPUInfo;
import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.device.PhysicalDevice;
import engine.graphics.vulkan.pipeline.PipelineStage;
import engine.graphics.vulkan.synchronize.Semaphore;
import engine.graphics.vulkan.util.GPUInfoVk;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.EXTDebugReport;
import org.lwjgl.vulkan.VkDebugReportCallbackCreateInfoEXT;
import org.lwjgl.vulkan.VkDebugReportCallbackEXT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import static engine.graphics.vulkan.util.VulkanUtils.translateVulkanResult;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugReport.*;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class VKGraphicsBackend implements GraphicsBackend {

    public static final String BACKEND_NAME = "vulkan";

    public static final Logger LOGGER = LoggerFactory.getLogger("Graphics");

    private Thread renderingThread;
    private GPUInfo gpuInfo;
    private VKWindowHelper windowHelper;
    private GLFWVulkanWindow primaryWindow;
    private VKResourceFactory resourceFactory;

    private final List<RunnableFuture<?>> pendingTasks = new ArrayList<>();

    public VKGraphicsBackend() {

    }

    @Override
    public String getName() {
        return BACKEND_NAME;
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
    public void removeRenderGraph(RenderGraph renderGraph) {

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
    public Window getPrimaryWindow() {
        return primaryWindow;
    }

    @Override
    public ResourceFactory getResourceFactory() {
        return resourceFactory;
    }

    @Override
    public RenderGraph loadRenderGraph(RenderGraphInfo renderGraph) {
        throw new UnsupportedOperationException(); // TODO: render graph
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
        var imageAcquireSemaphore = Semaphore.createSemaphore(device);
        var renderCompleteSemaphore = Semaphore.createSemaphore(device);
        int nextImage = swapchain.acquireNextImage(imageAcquireSemaphore, null);
        var cmdBuf = commandPool.createCommandBuffer();
        cmdBuf.beginCommandBuffer();
        // TODO: render graph
        cmdBuf.endCommandBuffer();
        queue.submit(cmdBuf, List.of(imageAcquireSemaphore), List.of(PipelineStage.COLOR_ATTACHMENT_OUTPUT), List.of(renderCompleteSemaphore), null);
        queue.present(swapchain, nextImage, List.of(renderCompleteSemaphore));
        queue.waitIdle();
        imageAcquireSemaphore.dispose();
        renderCompleteSemaphore.dispose();
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
        initVulkan();
    }

    private void initGLFW() {
        GLFWContext.initialize();
        windowHelper = new VKWindowHelper();
        primaryWindow = (GLFWVulkanWindow) windowHelper.createWindow();
        primaryWindow.init();
    }

    private VulkanInstance vulkanInstance;
    private PhysicalDevice physicalDevice;
    private LogicalDevice device;
    private VulkanMemoryAllocator allocator;
    private CommandPool commandPool;
    private Queue queue;
    private Swapchain swapchain;

    private void initVulkan() {
        LOGGER.info("Initializing Vulkan instance!");

        var requiredExtensions = primaryWindow.getRequiredExtensions();
        if (requiredExtensions == null)
            throw new IllegalStateException("Unable to get extension required for Vulkan to load");
        var extensions = new ArrayList<String>();
        while (requiredExtensions.hasRemaining()) {
            extensions.add(requiredExtensions.getStringUTF8());
        }
        if (GraphicsEngine.isDebug()) {
            extensions.add(EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
        }
        vulkanInstance = VulkanInstance.createInstance(extensions, "", 1, VulkanVersion.VER_1_0);
        if (GraphicsEngine.isDebug()) {
            initDebugCallback();
        }
        physicalDevice = vulkanInstance.getPhysicalDevices().get(0);
        gpuInfo = new GPUInfoVk(physicalDevice);
        printVKInfo();

        var i = 0;
        for (i = 0; i < physicalDevice.getQueueFamilyCount(); i++) {
            if (physicalDevice.doQueueFamilySupportGraphics(i)) {
                break;
            }
        }
        if (i >= physicalDevice.getQueueFamilyCount()) {
            throw new IllegalStateException(String.format("Unable to retrieve graphics queue family for device %s", physicalDevice.getDeviceName()));
        }
        device = physicalDevice.createLogicalDevice(new int[]{i}, new int[]{1}, null, null);
        resourceFactory = new VKResourceFactory(device);
        allocator = VulkanMemoryAllocator.createAllocator(device);
        commandPool = device.createCommandPool(i);
        queue = device.getQueue(i, 0);
        var surface = primaryWindow.getSurface(vulkanInstance);
        swapchain = Swapchain.createSwapChain(device, surface, null, primaryWindow.getWidth(), primaryWindow.getHeight(), ColorSpace.getColorSpace(physicalDevice, surface));
    }

    private void printVKInfo() {
        LOGGER.info("----- Vulkan Information -----");
        LOGGER.info("\tVK_VENDOR: {}", gpuInfo.getVendorName());
        LOGGER.info("\tVK_DEVICE: {}", gpuInfo.getName());
        LOGGER.info("\tVK_VERSION: {}", "1.0.0");
        LOGGER.info("\tVK_AVAILABLE_EXTENSIONS: {}", physicalDevice.getSupportedExtensionProperties(null).stream().reduce((s, s2) -> s + ", " + s2).orElse(""));
        LOGGER.info("\tGPU_TOTAL_MEMORY: {} MB", gpuInfo.getTotalMemory() / 1024);
        LOGGER.info("------------------------------");
    }

    public VulkanMemoryAllocator getAllocator() {
        return allocator;
    }

    private long debugCallback;

    private void initDebugCallback() {
        try (var stack = MemoryStack.stackPush()) {
            var vkCallback = new VkDebugReportCallbackEXT() {
                public int invoke(int flags, int objectType, long object, long location, int messageCode, long pLayerPrefix, long pMessage, long pUserData) {
                    var msg = VkDebugReportCallbackEXT.getString(pMessage);
                    switch (flags) {
                        case VK_DEBUG_REPORT_DEBUG_BIT_EXT:
                            LOGGER.debug(msg);
                            break;
                        case VK_DEBUG_REPORT_INFORMATION_BIT_EXT:
                            LOGGER.info(msg);
                            break;
                        case VK_DEBUG_REPORT_WARNING_BIT_EXT:
                            LOGGER.warn(msg);
                            break;
                        case VK_DEBUG_REPORT_ERROR_BIT_EXT:
                            LOGGER.error(msg);
                            break;
                    }
                    return 0;
                }
            };
            var dbgCreateInfo = VkDebugReportCallbackCreateInfoEXT.callocStack(stack)
                    .sType(EXTDebugReport.VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT)
                    .pNext(NULL)
                    .pfnCallback(vkCallback)
                    .pUserData(NULL)
                    .flags(VK_DEBUG_REPORT_ERROR_BIT_EXT | VK_DEBUG_REPORT_WARNING_BIT_EXT);
            var pCallback = stack.mallocLong(1);
            int err = vkCreateDebugReportCallbackEXT(vulkanInstance.getNativeInstance(), dbgCreateInfo, null, pCallback);
            long callbackHandle = pCallback.get(0);
            if (err != VK_SUCCESS) {
                throw new IllegalStateException("Failed to create VkInstance: " + translateVulkanResult(err));
            }
            debugCallback = callbackHandle;
        }
    }

    @Override
    public void dispose() {
        vkDestroyDebugReportCallbackEXT(vulkanInstance.getNativeInstance(), debugCallback, null);
        vulkanInstance.free();
    }

    public static final class Factory implements GraphicsBackendFactory {

        @Override
        public String getName() {
            return BACKEND_NAME;
        }

        @Override
        public GraphicsBackend create() {
            return new VKGraphicsBackend();
        }
    }
}
