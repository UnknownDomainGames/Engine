package engine.graphics.vulkan.util;

import engine.graphics.util.Cleaner;
import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.synchronize.Semaphore;

public class VulkanCleaner {
    public static Cleaner.Cleanable registerSemaphore(Semaphore semaphore, LogicalDevice device, long handle) {
        return Cleaner.register(semaphore, () -> Semaphore.dispose(device, handle));
    }
}
