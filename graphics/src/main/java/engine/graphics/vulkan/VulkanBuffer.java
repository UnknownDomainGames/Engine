package engine.graphics.vulkan;

import engine.graphics.vulkan.device.DeviceMemory;
import engine.graphics.vulkan.device.LogicalDevice;
import org.lwjgl.vulkan.VK10;

public class VulkanBuffer {
    private long handle;
    private final LogicalDevice device;
    private DeviceMemory memory;
    private boolean released = false;

    public VulkanBuffer(LogicalDevice device,long handle){
        this.device = device;
        this.handle = handle;
    }

    public long getHandle() {
        return handle;
    }

    public void free(){
        VK10.vkDestroyBuffer(device.getNativeDevice(), handle, null);
        handle = 0;
        released = true;
    }

    public enum Usage {
        INDEX_BUFFER(VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT),
        VERTEX_BUFFER(VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT),
        SOURCE_BUFFER(VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT),
        DESTINATION_BUFFER(VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT),
        UNIFORM_TEXEL_BUFFER(VK10.VK_BUFFER_USAGE_UNIFORM_TEXEL_BUFFER_BIT),
        STORAGE_TEXEL_BUFFER(VK10.VK_BUFFER_USAGE_STORAGE_TEXEL_BUFFER_BIT),
        UNIFORM_BUFFER(VK10.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT),
        STORAGE_BUFFER(VK10.VK_BUFFER_USAGE_STORAGE_BUFFER_BIT),
        INDIRECT_BUFFER(VK10.VK_BUFFER_USAGE_INDIRECT_BUFFER_BIT);
        private final int vk;

        Usage(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }
}
