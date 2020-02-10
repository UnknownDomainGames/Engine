package engine.graphics.vulkan;

import engine.graphics.vulkan.util.VulkanUtils;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanFramebuffer {

    private long[] framebuffers;
    private VkDevice device;

    public long[] getFramebuffers() {
        return framebuffers;
    }

    public void dispose(){
        for (int i = 0; i < framebuffers.length; i++)
            vkDestroyFramebuffer(device, framebuffers[i], null);
    }

    public static VulkanFramebuffer createFramebuffers(VkDevice device, Swapchain swapchain, long renderPass, int width, int height) {
        var attachments = memAllocLong(1);
        VkFramebufferCreateInfo fci = VkFramebufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                .pAttachments(attachments)
                .flags(0)
                .height(height)
                .width(width)
                .layers(1)
                .pNext(NULL)
                .renderPass(renderPass);
        // Create a framebuffer for each swapchain image
        long[] framebuffers = new long[swapchain.images.length];
        var pFramebuffer = memAllocLong(1);
        for (int i = 0; i < swapchain.images.length; i++) {
            attachments.put(0, swapchain.imageViews[i]);
            int err = vkCreateFramebuffer(device, fci, null, pFramebuffer);
            long framebuffer = pFramebuffer.get(0);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create framebuffer: " + VulkanUtils.translateVulkanResult(err));
            }
            framebuffers[i] = framebuffer;
        }
        memFree(attachments);
        memFree(pFramebuffer);
        fci.free();
        var fbo = new VulkanFramebuffer();
        fbo.framebuffers = framebuffers;
        fbo.device = device;
        return fbo;
    }
}
