package engine.graphics.vulkan;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import java.nio.LongBuffer;

import static engine.graphics.vulkan.util.VulkanUtils.translateVulkanResult;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.*;

public class Semaphore {
    private LongBuffer pointerBuf;
    private VkDevice device;

    public LongBuffer getSemaphorePointer() {
        return pointerBuf;
    }

    private boolean semaphoreEstablished = false;

    public void createSemaphore(){
        var info = VkSemaphoreCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO)
                .pNext(NULL)
                .flags(0);
        int err = vkCreateSemaphore(device, info, null, pointerBuf);
        info.free();
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create semaphore: " + translateVulkanResult(err));
        }
        semaphoreEstablished = true;
    }

    public void destroySemaphore(){
        vkDestroySemaphore(device, pointerBuf.get(0), null);
        pointerBuf.clear();
        semaphoreEstablished = false;
    }

    public void dispose(){
        if(semaphoreEstablished) {
            destroySemaphore();
        }
        memFree(pointerBuf);
    }

    public static Semaphore createPendingSemaphore(VkDevice device){

        var pSemaphore = MemoryUtil.memAllocLong(1);

        var semaphore = new Semaphore();
        semaphore.pointerBuf = pSemaphore;
        semaphore.device = device;

        return semaphore;
    }
}
