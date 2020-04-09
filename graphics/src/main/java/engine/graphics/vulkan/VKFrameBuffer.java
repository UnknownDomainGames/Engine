package engine.graphics.vulkan;

import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.render.Attachment;
import engine.graphics.vulkan.render.RenderPass;
import engine.graphics.vulkan.texture.VKTexture;
import engine.graphics.vulkan.util.VulkanUtils;
import org.joml.Vector4ic;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.VK10.*;

public class VKFrameBuffer implements FrameBuffer {

    private long handle;
    private int width;
    private int height;
    private LogicalDevice device;
    private List<VKTexture.ImageView> attachmentViews;
    private boolean released = false;

    VKFrameBuffer(LogicalDevice device, long handle, int width, int height) {
        this.device = device;
        this.handle = handle;
    }

    @Override
    public long getId() {
        return handle;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void bind() {

    }

    @Override
    public void bindReadOnly() {

    }

    @Override
    public void bindDrawOnly() {

    }

    @Override
    public void copyFrom(FrameBuffer src, boolean copyColor, boolean copyDepth, boolean copyStencil, FilterMode filterMode) {

    }

    @Override
    public void copyFrom(FrameBuffer src, Vector4ic srcRect, Vector4ic destRect, boolean copyColor, boolean copyDepth, boolean copyStencil, FilterMode filterMode) {

    }

    @Override
    public void readPixels(ColorFormat format, ByteBuffer pixels) {

    }

    @Override
    public void readPixels(int x, int y, int width, int height, ColorFormat format, ByteBuffer pixels) {

    }

    public void dispose() {
        if (released) return;
        vkDestroyFramebuffer(device.getNativeDevice(), handle, null);
        handle = 0;
        released = true;
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    public static VKFrameBuffer createFramebuffer(LogicalDevice device, RenderPass renderPass, int width, int height) {
        try (var stack = MemoryStack.stackPush()) {
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
            var fbo = new VKFrameBuffer(device, pFramebuffer.get(0), width, height);
            fbo.attachmentViews = attachedView;
            return fbo;
        }
    }
}
