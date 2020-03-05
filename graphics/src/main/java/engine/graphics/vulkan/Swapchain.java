package engine.graphics.vulkan;

import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.synchronize.Semaphore;
import engine.graphics.vulkan.synchronize.VulkanFence;
import engine.graphics.vulkan.texture.VKTexture;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

import javax.annotation.Nullable;
import java.nio.LongBuffer;
import java.util.List;

import static engine.graphics.vulkan.util.VulkanUtils.translateVulkanResult;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class Swapchain {
    private final LogicalDevice device;
    long swapchainHandle;
    VKTexture[] images;
    VKTexture.ImageView[] imageViews;
    private boolean released = false;

    public Swapchain(LogicalDevice device, long handle){
        this.device = device;
        this.swapchainHandle = handle;
    }

    public long getHandle() {
        return swapchainHandle;
    }

    private static final int VK_FLAGS_NONE = 0;
    public static Swapchain createSwapChain(LogicalDevice device, long surface, @Nullable Swapchain oldSwapChain, int newWidth,
                                            int newHeight, ColorSpace colorSpace) {
        try(var stack = MemoryStack.stackPush()) {
            int err;
            // Get physical device surface properties and formats
            var surfCaps = VkSurfaceCapabilitiesKHR.callocStack(stack);
            err = vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device.getPhysicalDevice().getNativePhysicalDevice(), surface, surfCaps);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to get physical device surface capabilities: " + translateVulkanResult(err));
            }

            var pPresentModeCount = stack.mallocInt(1);
            err = vkGetPhysicalDeviceSurfacePresentModesKHR(device.getPhysicalDevice().getNativePhysicalDevice(), surface, pPresentModeCount, null);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to get number of physical device surface presentation modes: " + translateVulkanResult(err));
            }
            int presentModeCount = pPresentModeCount.get(0);

            var pPresentModes = stack.mallocInt(presentModeCount);
            err = vkGetPhysicalDeviceSurfacePresentModesKHR(device.getPhysicalDevice().getNativePhysicalDevice(), surface, pPresentModeCount, pPresentModes);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to get physical device surface presentation modes: " + translateVulkanResult(err));
            }

            // Try to use mailbox mode. Low latency and non-tearing
            int swapchainPresentMode = VK_PRESENT_MODE_FIFO_KHR;
//            for (int i = 0; i < presentModeCount; i++) {
//                if (pPresentModes.get(i) == VK_PRESENT_MODE_MAILBOX_KHR) {
//                    swapchainPresentMode = VK_PRESENT_MODE_MAILBOX_KHR;
//                    break;
//                }
//                if (pPresentModes.get(i) == VK_PRESENT_MODE_IMMEDIATE_KHR) {
//                    swapchainPresentMode = VK_PRESENT_MODE_IMMEDIATE_KHR;
//                }
//            }

            // Determine the number of images
            int desiredNumberOfSwapchainImages = surfCaps.minImageCount() + 1;
            if ((surfCaps.maxImageCount() > 0) && (desiredNumberOfSwapchainImages > surfCaps.maxImageCount())) {
                desiredNumberOfSwapchainImages = surfCaps.maxImageCount();
            }

            VkExtent2D currentExtent = surfCaps.currentExtent();
            int currentWidth = currentExtent.width();
            int currentHeight = currentExtent.height();
            int width;
            int height;
            if (currentWidth != -1 && currentHeight != -1) {
                width = currentWidth;
                height = currentHeight;
            } else {
                width = newWidth;
                height = newHeight;
            }

            int preTransform;
            if ((surfCaps.supportedTransforms() & VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR) != 0) {
                preTransform = VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
            } else {
                preTransform = surfCaps.currentTransform();
            }

            VkSwapchainCreateInfoKHR swapchainCI = VkSwapchainCreateInfoKHR.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .surface(surface)
                    .minImageCount(desiredNumberOfSwapchainImages)
                    .imageFormat(colorSpace.getColorFormat().getVk())
                    .imageColorSpace(colorSpace.getColorSpace())
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                    .preTransform(preTransform)
                    .imageArrayLayers(1)
                    .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .pQueueFamilyIndices(null)
                    .presentMode(swapchainPresentMode)
                    .clipped(true)
                    .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
            swapchainCI.imageExtent()
                    .width(width)
                    .height(height);
            if(oldSwapChain != null){
                swapchainCI.oldSwapchain(oldSwapChain.getHandle());
            }
            LongBuffer pSwapChain = stack.mallocLong(1);
            err = vkCreateSwapchainKHR(device.getNativeDevice(), swapchainCI, null, pSwapChain);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create swap chain: " + translateVulkanResult(err));
            }
            long swapChain = pSwapChain.get(0);

            // If we just re-created an existing swapchain, we should destroy the old swapchain at this point.
            // Note: destroying the swapchain also cleans up all its associated presentable images once the platform is done with them.
            if (oldSwapChain != null) {
                vkDestroySwapchainKHR(device.getNativeDevice(), oldSwapChain.getHandle(), null);
            }

            var pImageCount = memAllocInt(1);
            err = vkGetSwapchainImagesKHR(device.getNativeDevice(), swapChain, pImageCount, null);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to get number of swapchain images: " + translateVulkanResult(err));
            }
            int imageCount = pImageCount.get(0);

            LongBuffer pSwapchainImages = stack.mallocLong(imageCount);
            err = vkGetSwapchainImagesKHR(device.getNativeDevice(), swapChain, pImageCount, pSwapchainImages);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to get swapchain images: " + translateVulkanResult(err));
            }

            var images = new VKTexture[imageCount];
            var imageViews = new VKTexture.ImageView[imageCount];
            for (int i = 0; i < imageCount; i++) {
                images[i] = new VKTexture(device, pSwapchainImages.get(i), colorSpace.getColorFormat(), List.of(VKTexture.Usage.COLOR_ATTACHMENT));
                imageViews[i] = images[i].createView(VKTexture.ImageAspect.COLOR);
            }
            Swapchain ret = new Swapchain(device, swapChain);
            ret.images = images;
            ret.imageViews = imageViews;
            return ret;
        }
    }

    public void free(){
        vkDestroySwapchainKHR(device.getNativeDevice(), swapchainHandle, null);
        released = true;
    }

    /**
        acquire the next image to be rendered, and signal to passing semaphore and fence when completed
 * @param semaphore
 * @param fence
     */
    public int acquireNextImage(@Nullable Semaphore semaphore, @Nullable VulkanFence fence) {
        return acquireNextImage(-1L, semaphore, fence);
    }

    /**
        acquire the next image to be rendered, and signal to passing semaphore and fence when completed
     @param timeout
     @param semaphore
     @param fence
     */
    public int acquireNextImage(long timeout, @Nullable Semaphore semaphore, @Nullable VulkanFence fence){
        try(var stack = MemoryStack.stackPush()){
            var index = stack.mallocInt(1);
            vkAcquireNextImageKHR(device.getNativeDevice(), swapchainHandle, timeout, semaphore != null ? semaphore.getHandle() : VK_NULL_HANDLE, fence != null ? fence.getHandle() : VK_NULL_HANDLE, index);
            return index.get(0);
        }
    }
}
