package engine.graphics.vulkan;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

public class QueueFamily {
    private VkDevice device;
    private int queueFamilyIndex;
    private VkPhysicalDeviceMemoryProperties memoryProperties;

    QueueFamily(VkDevice device, int queueFamilyIndex, VkPhysicalDeviceMemoryProperties memoryProperties){
        this.device = device;
        this.queueFamilyIndex = queueFamilyIndex;
        this.memoryProperties = memoryProperties;
    }

    public VkDevice getLogicalDevice() {
        return device;
    }

    public int getQueueFamilyIndex() {
        return queueFamilyIndex;
    }

    public VkPhysicalDeviceMemoryProperties getMemoryProperties() {
        return memoryProperties;
    }
}
