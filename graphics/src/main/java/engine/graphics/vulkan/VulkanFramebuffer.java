package engine.graphics.vulkan;

import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.render.Attachment;
import engine.graphics.vulkan.render.RenderPass;
import engine.graphics.vulkan.texture.VKTexture;
import engine.graphics.vulkan.util.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanFramebuffer {

    private long handle;
    private LogicalDevice device;
    private List<VKTexture.ImageView> attachmentViews;
    private boolean released = false;

    VulkanFramebuffer(LogicalDevice device, long handle){
        this.device = device;
        this.handle = handle;
    }

    public long getHandle() {
        return handle;
    }

    public void dispose(){
        if(released) return;
        vkDestroyFramebuffer(device.getNativeDevice(), handle, null);
        handle = 0;
        released = true;
    }

    public static VulkanFramebuffer createFramebuffer(LogicalDevice device, RenderPass renderPass, int width, int height) {
        try(var stack = MemoryStack.stackPush()) {
            var attachments = stack.mallocLong(renderPass.getAttachments().size());
            VkFramebufferCreateInfo fci = VkFramebufferCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                    .pAttachments(attachments)
                    .flags(0)
                    .height(height)
                    .width(width)
                    .layers(1)
                    .pNext(NULL)
                    .renderPass(renderPass.getHandle());
            var attachedView = new ArrayList<VKTexture.ImageView>();
            for (Attachment renderPassAttachment : renderPass.getAttachments()) {
                var usages = renderPass.getAttachmentUsages().get(renderPassAttachment);
                var tex = device.createTexture(width, height, renderPassAttachment.getFormat(), usages, renderPassAttachment.getFinalLayout());
                if(usages.contains(VKTexture.Usage.COLOR_ATTACHMENT)) {
                    attachedView.add(tex.createView(VKTexture.ImageAspect.COLOR));
                }
                else if(usages.contains(VKTexture.Usage.DEPTH_STENCIL_ATTACHMENT)){
                    attachedView.add(tex.createView(List.of(VKTexture.ImageAspect.DEPTH, VKTexture.ImageAspect.STENCIL)));
                }
            }
            for (VKTexture.ImageView imageView : attachedView) {
                attachments.put(imageView.getHandle());
            }
            attachments.flip();
            var pFramebuffer = stack.mallocLong(1);
            int err = vkCreateFramebuffer(device.getNativeDevice(), fci, null, pFramebuffer);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create framebuffer: " + VulkanUtils.translateVulkanResult(err));
            }
            var fbo = new VulkanFramebuffer(device, pFramebuffer.get(0));
            fbo.attachmentViews = attachedView;
            return fbo;
        }
    }
}
