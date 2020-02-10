package engine.graphics.vulkan;

import engine.graphics.vulkan.device.LogicalDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;

import static org.lwjgl.vulkan.VK10.*;

public class CommandPool {
    private long pointer;
    private LogicalDevice device;

    public CommandPool(long ptr, LogicalDevice device){
        this.pointer = ptr;
        this.device = device;
    }

    public long getCommandPoolPointer() {
        return pointer;
    }

    public void resetCommandPool(int flag){
        vkResetCommandPool(device.getNativeDevice(), pointer, flag);
    }

    public LogicalDevice getDevice() {
        return device;
    }

    public CommandBuffer createCommandBuffer() {
        try(var stack = MemoryStack.stackPush()) {
            var cmdBufAllocateInfo = VkCommandBufferAllocateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(pointer)
                    .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                    .commandBufferCount(1);
            var pCommandBuffer = stack.mallocPointer(1);
            int err = vkAllocateCommandBuffers(device.getNativeDevice(), cmdBufAllocateInfo, pCommandBuffer);
            if (err != VK_SUCCESS) {
//            throw new AssertionError("Failed to allocate command buffer: " + translateVulkanResult(err));
                return null;
            }
            long commandBuffer = pCommandBuffer.get(0);
            return new CommandBuffer(new VkCommandBuffer(commandBuffer, device.getNativeDevice()), this);
        }
    }
}
