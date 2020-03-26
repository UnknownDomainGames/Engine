package engine.graphics.vulkan.util;

import engine.graphics.util.Cleaner;
import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.synchronize.Semaphore;
import org.lwjgl.vulkan.VK10;

public class VulkanCleaner {
    public static Cleaner.Disposable registerSemaphore(Semaphore semaphore, LogicalDevice device, long handle){
        return Cleaner.register(semaphore, ()->Semaphore.dispose(device, handle));
    }
}
