package engine.graphics.vulkan;

import engine.graphics.vulkan.util.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.util.List;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class Queue {
    private VkQueue vk;

    public Queue(VkQueue vkQueue){
        this.vk = vkQueue;
    }

    public VkQueue getNativeQueue() {
        return vk;
    }

    public void submit(List<CommandBuffer> buffers){
        try(var stack = MemoryStack.stackPush()) {
            VkSubmitInfo submitInfo = VkSubmitInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            var pCommandBuffers = stack.mallocPointer(buffers.size());
            for (CommandBuffer buffer : buffers) {
                pCommandBuffers.put(buffer.getNativeCommandBuffer());
            }
            pCommandBuffers.flip();
            submitInfo.pCommandBuffers(pCommandBuffers);
            int err = vkQueueSubmit(vk, submitInfo, VK_NULL_HANDLE);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to submit command buffer: " + VulkanUtils.translateVulkanResult(err));
            }
        }
    }

    public void waitIdle(){
        vkQueueWaitIdle(vk);
    }
}
