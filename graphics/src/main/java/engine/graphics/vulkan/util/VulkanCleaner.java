package engine.graphics.vulkan.util;

import engine.graphics.util.Cleaner;
import engine.graphics.vulkan.synchronize.Semaphore;
import org.lwjgl.vulkan.VK10;

public class VulkanCleaner {
    public static Cleaner.Disposable registerSemaphore(Semaphore semaphore){
        return Cleaner.register(semaphore, semaphore::dispose);
    }
}
