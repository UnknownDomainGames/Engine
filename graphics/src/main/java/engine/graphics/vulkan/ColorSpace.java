package engine.graphics.vulkan;


import engine.graphics.vulkan.device.PhysicalDevice;
import engine.graphics.vulkan.texture.VKColorFormat;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import static engine.graphics.vulkan.util.VulkanUtils.translateVulkanResult;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.VK10.*;

public class ColorSpace {
    private VKColorFormat colorFormat;
    private int colorSpace;

    public VKColorFormat getColorFormat() {
        return colorFormat;
    }

    public int getColorSpace() {
        return colorSpace;
    }

    public static ColorSpace getColorSpace(PhysicalDevice physicalDevice, long surface) {
        try(var stack = MemoryStack.stackPush()) {
            // Iterate over each queue to learn whether it supports presenting:
            int queueCount = physicalDevice.getQueueFamilyCount();
            var supportsPresent = stack.mallocInt(queueCount);
            for (int i = 0; i < queueCount; i++) {
                supportsPresent.position(i);
                int err = vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice.getNativePhysicalDevice(), i, surface, supportsPresent);
                if (err != VK_SUCCESS) {
                    throw new AssertionError("Failed to physical device surface support: " + translateVulkanResult(err));
                }
            }

            // Search for a graphics and a present queue in the array of queue families, try to find one that supports both
            int graphicsQueueNodeIndex = Integer.MAX_VALUE;
            int presentQueueNodeIndex = Integer.MAX_VALUE;
            for (int i = 0; i < queueCount; i++) {
                if (physicalDevice.doQueueFamilySupportGraphics(i)) {
                    if (graphicsQueueNodeIndex == Integer.MAX_VALUE) {
                        graphicsQueueNodeIndex = i;
                    }
                    if (supportsPresent.get(i) == VK_TRUE) {
                        graphicsQueueNodeIndex = i;
                        presentQueueNodeIndex = i;
                        break;
                    }
                }
            }
            if (presentQueueNodeIndex == Integer.MAX_VALUE) {
                // If there's no queue that supports both present and graphics try to find a separate present queue
                for (int i = 0; i < queueCount; ++i) {
                    if (supportsPresent.get(i) == VK_TRUE) {
                        presentQueueNodeIndex = i;
                        break;
                    }
                }
            }

            // Generate error if could not find both a graphics and a present queue
            if (graphicsQueueNodeIndex == Integer.MAX_VALUE) {
                throw new AssertionError("No graphics queue found");
            }
            if (presentQueueNodeIndex == Integer.MAX_VALUE) {
                throw new AssertionError("No presentation queue found");
            }
            if (graphicsQueueNodeIndex != presentQueueNodeIndex) {
                throw new AssertionError("Presentation queue != graphics queue");
            }

            // Get list of supported formats
            var pFormatCount = stack.mallocInt(1);
            int err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice.getNativePhysicalDevice(), surface, pFormatCount, null);
            int formatCount = pFormatCount.get(0);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to query number of physical device surface formats: " + translateVulkanResult(err));
            }

            var surfFormats = VkSurfaceFormatKHR.callocStack(formatCount, stack);
            err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice.getNativePhysicalDevice(), surface, pFormatCount, surfFormats);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to query physical device surface formats: " + translateVulkanResult(err));
            }

            VKColorFormat colorFormat;
            if (formatCount == 1 && surfFormats.get(0).format() == VK_FORMAT_UNDEFINED) {
                colorFormat = VKColorFormat.BGR_UNSIGNED_NORMALIZED;
            } else {
                colorFormat = VKColorFormat.fromVkFormat(surfFormats.get(0).format());
            }
            int colorSpace = surfFormats.get(0).colorSpace();

            ColorSpace ret = new ColorSpace();
            ret.colorFormat = colorFormat;
            ret.colorSpace = colorSpace;
            return ret;
        }
    }
}
