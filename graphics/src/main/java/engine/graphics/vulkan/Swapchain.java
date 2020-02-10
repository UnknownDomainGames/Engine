package engine.graphics.vulkan;

import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.texture.VKTexture;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

import javax.annotation.Nullable;
import java.nio.LongBuffer;

import static engine.graphics.vulkan.util.VulkanUtils.translateVulkanResult;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class Swapchain {
    private final LogicalDevice device;
    long swapchainHandle;
    VKTexture[] images;
    long[] imageViews;
    private boolean released = false;

    public Swapchain(LogicalDevice device, long handle){
        this.device = device;
        this.swapchainHandle = handle;
    }

    public long getSwapchainHandle() {
        return swapchainHandle;
    }

    private static final int VK_FLAGS_NONE = 0;
    public static Swapchain createSwapChain(LogicalDevice device, long surface, @Nullable Swapchain oldSwapChain, CommandBuffer commandBuffer, int newWidth,
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
                    .imageFormat(colorSpace.getColorFormat())
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
                swapchainCI.oldSwapchain(oldSwapChain.getSwapchainHandle());
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
                vkDestroySwapchainKHR(device.getNativeDevice(), oldSwapChain.getSwapchainHandle(), null);
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
            long[] imageViews = new long[imageCount];
            LongBuffer pBufferView = stack.mallocLong(1);
            VkImageViewCreateInfo colorAttachmentView = VkImageViewCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                    .pNext(NULL)
                    .format(colorSpace.getColorFormat())
                    .viewType(VK_IMAGE_VIEW_TYPE_2D)
                    .flags(VK_FLAGS_NONE);
            colorAttachmentView.components()
                    .r(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .g(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .b(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .a(VK_COMPONENT_SWIZZLE_IDENTITY);
            colorAttachmentView.subresourceRange()
                    .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    .baseMipLevel(0)
                    .levelCount(1)
                    .baseArrayLayer(0)
                    .layerCount(1);
            for (int i = 0; i < imageCount; i++) {
                images[i] = new VKTexture(device, pSwapchainImages.get(i));
                colorAttachmentView.image(images[i].getHandle());
                err = vkCreateImageView(device.getNativeDevice(), colorAttachmentView, null, pBufferView);
                imageViews[i] = pBufferView.get(0);
                if (err != VK_SUCCESS) {
                    throw new AssertionError("Failed to create image view: " + translateVulkanResult(err));
                }
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

//    public VKTexture acquireNextImage(int timeout, Semaphore semaphore){
//
//    }
}
