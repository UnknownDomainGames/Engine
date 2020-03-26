package engine.graphics.vulkan.synchronize;

import engine.graphics.util.Cleaner;
import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.util.VulkanCleaner;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import java.nio.LongBuffer;

import static engine.graphics.vulkan.util.VulkanUtils.translateVulkanResult;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

public class Semaphore {
    private long handle;
    private LogicalDevice device;
    private boolean released = false;
    private Cleaner.Disposable disposable;

    public Semaphore(LogicalDevice device, long handle){
        this.device = device;
        this.handle = handle;
        disposable = VulkanCleaner.registerSemaphore(this, device, handle);
    }

    public LogicalDevice getDevice() {
        return device;
    }

    public long getHandle() {
        return handle;
    }

    private void checkReleased(){
        if(released) throw new IllegalStateException("Semaphore already released!");
    }

    public static Semaphore createSemaphore(LogicalDevice device) {
        try(var stack = MemoryStack.stackPush()){
            var info = VkSemaphoreCreateInfo.callocStack(stack).sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
            var ptr = stack.mallocLong(1);
            var err = vkCreateSemaphore(device.getNativeDevice(), info, null, ptr);
            if(err != VK_SUCCESS){
                return null;
            }
            return new Semaphore(device, ptr.get(0));
        }
    }

    public void dispose(){
        checkReleased();
        dispose(device, handle);
        handle = 0;
        released = true;
    }

    public static void dispose(LogicalDevice device, long handle){
        vkDestroySemaphore(device.getNativeDevice(), handle, null);
    }
}
