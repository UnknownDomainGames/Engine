package engine.graphics.vulkan.texture;

import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture;
import engine.graphics.texture.TextureFormat;
import engine.graphics.vulkan.device.LogicalDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import javax.annotation.Nullable;

import static org.lwjgl.vulkan.VK10.*;

public class VKTexture implements Texture {
    private final LogicalDevice device;
    private long handle;
    private boolean released = false;

    private ColorFormat format;
    private Usage usage;

    public VKTexture(LogicalDevice device, long handle, ColorFormat format, Usage usage) {
        this.device = device;
        this.handle = handle;
        this.format = format;
        this.usage = usage;
    }

    public LogicalDevice getDevice() {
        return device;
    }

    public long getHandle() {
        return handle;
    }

    public void checkReleased(){
        if(released) throw new IllegalStateException("Texture already released!");
    }

    public ImageView createView(ImageAspect aspect) {
        return createView(aspect, 0, 1, 0, 1);
    }

    public ImageView createView(ImageAspect aspect, int baseMipmapLevel, int levelCount, int baseLayer, int layerCount){
        try(var stack = MemoryStack.stackPush()){
            var view = VkImageViewCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                    .format(format.getVk())
                    .viewType(VK_IMAGE_VIEW_TYPE_2D)
                    .image(handle);
            view.components()
                    .r(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .g(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .b(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .a(VK_COMPONENT_SWIZZLE_IDENTITY);
            view.subresourceRange()
                    .aspectMask(aspect.getVk())
                    .baseMipLevel(baseMipmapLevel)
                    .levelCount(levelCount)
                    .baseArrayLayer(baseLayer)
                    .layerCount(layerCount);
            var ptr = stack.mallocLong(1);
            var err = vkCreateImageView(device.getNativeDevice(), view, null, ptr);
            if (err != VK_SUCCESS) {
                return null;
            }
            return new ImageView(this, ptr.get(0));
        }
    }

    @Override
    public int getId() {
        return Math.toIntExact(handle);
    }

    @Override
    public TextureFormat getFormat() {
        return format.getPeer();
    }

    @Override
    public boolean isMultiSample() {
        return false;
    }

    @Nullable
    @Override
    public Sampler getSampler() {
        return null;
    }

    @Override
    public void bind() {

    }

    @Override
    public void dispose() {
        if (!isDisposed()) {
            VK10.vkDestroyImage(device.getNativeDevice(), handle, null);
            released = true;
            handle = 0;
        }
    }

    @Override
    public boolean isDisposed() {
        return released;
    }

    public static class ImageView {
        private VKTexture texture;
        private long handle;

        public ImageView(VKTexture texture, long handle){
            this.texture = texture;
            this.handle = handle;
        }

        public long getHandle() {
            return handle;
        }

        public VKTexture getTexture() {
            return texture;
        }
    }

    public enum Swizzle {
        IDENTITY(VK10.VK_COMPONENT_SWIZZLE_IDENTITY),
        ZERO(VK10.VK_COMPONENT_SWIZZLE_ZERO),
        ONE(VK10.VK_COMPONENT_SWIZZLE_ONE),
        R(VK10.VK_COMPONENT_SWIZZLE_R),
        G(VK10.VK_COMPONENT_SWIZZLE_G),
        B(VK10.VK_COMPONENT_SWIZZLE_B),
        A(VK10.VK_COMPONENT_SWIZZLE_A);

        private final int vk;

        Swizzle(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }

    public enum Usage {
        SAMPLE(VK10.VK_IMAGE_USAGE_SAMPLED_BIT),
        SOURCE(VK10.VK_IMAGE_USAGE_TRANSFER_SRC_BIT),
        DESTINATION(VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT),
        STORAGE(VK10.VK_IMAGE_USAGE_STORAGE_BIT),
        COLOR_ATTACHMENT(VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT),
        DEPTH_STENCIL_ATTACHMENT(VK10.VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT),
        TRANSIENT_ATTACHMENT(VK10.VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT),
        INPUT_ATTACHMENT(VK10.VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT);
        private final int vk;

        Usage(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }

    public enum Layout {
        UNDEFINED(VK10.VK_IMAGE_LAYOUT_UNDEFINED),
        GENERAL(VK10.VK_IMAGE_LAYOUT_GENERAL),
        TRANSFER_SOURCE(VK10.VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL),
        TRANSFER_DESTINATION(VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL),
        READONLY(VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL),
        COLOR_ATTACHMENT(VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL),
        DEPTH_STENCIL_ATTACHMENT(VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL),
        DEPTH_STENCIL_READONLY(VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_READ_ONLY_OPTIMAL),
        PREINITIALIZED(VK10.VK_IMAGE_LAYOUT_PREINITIALIZED),
        PRESENTATION_SURFACE(KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);
        private final int vk;

        Layout(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }

    public enum ImageAspect {
        COLOR(VK10.VK_IMAGE_ASPECT_COLOR_BIT),
        DEPTH(VK10.VK_IMAGE_ASPECT_DEPTH_BIT),
        STENCIL(VK10.VK_IMAGE_ASPECT_STENCIL_BIT),
        METADATA(VK10.VK_IMAGE_ASPECT_METADATA_BIT);

        private final int vk;

        ImageAspect(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }
}
