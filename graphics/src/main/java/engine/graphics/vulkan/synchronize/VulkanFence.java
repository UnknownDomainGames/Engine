package engine.graphics.vulkan.synchronize;

import engine.graphics.vulkan.device.LogicalDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkFenceCreateInfo;

public class VulkanFence {

    private final LogicalDevice device;
    private long handle;
    private boolean released = false;

    public VulkanFence(LogicalDevice device, long handle) {
        this.device = device;
        this.handle = handle;
    }

    public long getHandle() {
        return handle;
    }

    private void checkReleased() {
        if(released) throw new IllegalStateException("Fence already disposed!");
    }

    public void dispose() {
        checkReleased();
        VK10.vkDestroyFence(device.getNativeDevice(), handle, null);
        handle = 0;
        released = true;
    }

    public boolean isReady() {
        checkReleased();
        var code = VK10.vkGetFenceStatus(device.getNativeDevice(), handle);
        switch (code) {
            case VK10.VK_SUCCESS:
                return true;
            case VK10.VK_NOT_READY:
                return false;
            case VK10.VK_ERROR_DEVICE_LOST:
                throw new IllegalStateException("Logical device has lost");
            default:
                return false;
        }
    }

    public void reset() {
        checkReleased();
        VK10.vkResetFences(device.getNativeDevice(), handle);
    }

    public static VulkanFence createFence(LogicalDevice device, boolean signaledOnCreation) {
        try(var stack = MemoryStack.stackPush()) {
            var info = VkFenceCreateInfo.callocStack(stack).sType(VK10.VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                    .flags(signaledOnCreation ? VK10.VK_FENCE_CREATE_SIGNALED_BIT : 0);
            var ptr = stack.mallocLong(1);
            var err = VK10.vkCreateFence(device.getNativeDevice(), info, null, ptr);
            if(err != VK10.VK_SUCCESS) {
                return null;
            }
            return new VulkanFence(device, ptr.get(0));
        }
    }
}
