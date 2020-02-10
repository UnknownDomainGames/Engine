package engine.graphics.vulkan.texture;

import engine.graphics.vulkan.device.LogicalDevice;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK10;

public class VKTexture {
    private final LogicalDevice device;
    private long handle;
    private boolean released = false;

    public VKTexture(LogicalDevice device, long handle) {
        this.device = device;
        this.handle = handle;
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

    public void free(){
        checkReleased();
        VK10.vkDestroyImage(device.getNativeDevice(), handle, null);
        released = true;
        handle = 0;
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
        PRESENTATION_SUFRACE(KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);
        private final int vk;

        Layout(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }

    public enum Format{
        BGRA_UNSIGNED_NORMALIZED(VK10.VK_FORMAT_B8G8R8A8_UNORM),
        RGBA_UNSIGNED_NORMALIZED(VK10.VK_FORMAT_R8G8B8A8_UNORM),
        BGRA_NORMALIZED(VK10.VK_FORMAT_B8G8R8A8_SNORM),
        RGBA_NORMALIZED(VK10.VK_FORMAT_R8G8B8A8_SNORM),
        BGRA_UINT(VK10.VK_FORMAT_B8G8R8A8_UINT),
        RGBA_UINT(VK10.VK_FORMAT_R8G8B8A8_UINT),
        BGRA_INT(VK10.VK_FORMAT_B8G8R8A8_SINT),
        RGBA_INT(VK10.VK_FORMAT_R8G8B8A8_SINT),
        BGRA_UNSIGNED_SCALED_FLOAT(VK10.VK_FORMAT_B8G8R8A8_USCALED),
        RGBA_UNSIGNED_SCALED_FLOAT(VK10.VK_FORMAT_R8G8B8A8_USCALED),
        BGRA_SCALED_FLOAT(VK10.VK_FORMAT_B8G8R8A8_SSCALED),
        RGBA_SCALED_FLOAT(VK10.VK_FORMAT_R8G8B8A8_SSCALED),
        R8_UNSIGNED_NORMALIZED(VK10.VK_FORMAT_R8_UNORM),
        R8_NORMALIZED(VK10.VK_FORMAT_R8_SNORM),
        R8_UINT(VK10.VK_FORMAT_R8_UINT),
        R8_INT(VK10.VK_FORMAT_R8_SINT),
        R8_UNSIGNED_SCALED_FLOAT(VK10.VK_FORMAT_R8_USCALED),
        R8_SCALED_FLOAT(VK10.VK_FORMAT_R8_SSCALED),
        R8G8_UNSIGNED_NORMALIZED(VK10.VK_FORMAT_R8G8_UNORM),
        R8G8_NORMALIZED(VK10.VK_FORMAT_R8G8_SNORM),
        R8G8_UINT(VK10.VK_FORMAT_R8G8_UINT),
        R8G8_INT(VK10.VK_FORMAT_R8G8_SINT),
        R8G8_UNSIGNED_SCALED_FLOAT(VK10.VK_FORMAT_R8G8_USCALED),
        R8G8_SCALED_FLOAT(VK10.VK_FORMAT_R8G8_SSCALED),
        RGB_UNSIGNED_NORMALIZED(VK10.VK_FORMAT_R8G8B8_UNORM),
        RGB_NORMALIZED(VK10.VK_FORMAT_R8G8B8_SNORM),
        RGB_UINT(VK10.VK_FORMAT_R8G8B8_UINT),
        RGB_INT(VK10.VK_FORMAT_R8G8B8_SINT),
        RGB_UNSIGNED_SCALED_FLOAT(VK10.VK_FORMAT_R8G8B8_USCALED),
        RGB_SCALED_FLOAT(VK10.VK_FORMAT_R8G8B8_SSCALED),
        BGR_UNSIGNED_NORMALIZED(VK10.VK_FORMAT_B8G8R8_UNORM),
        BGR_NORMALIZED(VK10.VK_FORMAT_B8G8R8_SNORM),
        BGR_UINT(VK10.VK_FORMAT_B8G8R8_UINT),
        BGR_INT(VK10.VK_FORMAT_B8G8R8_SINT),
        BGR_UNSIGNED_SCALED_FLOAT(VK10.VK_FORMAT_B8G8R8_USCALED),
        BGR_SCALED_FLOAT(VK10.VK_FORMAT_B8G8R8_SSCALED);
        private final int vk;

        Format(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }
}
