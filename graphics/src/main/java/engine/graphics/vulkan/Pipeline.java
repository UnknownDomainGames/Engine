package engine.graphics.vulkan;

import engine.graphics.vulkan.device.LogicalDevice;
import org.lwjgl.vulkan.VK10;

public class Pipeline {

    private long handle;

    private final LogicalDevice device;

    public Pipeline(LogicalDevice device, long handle) {
        this.device = device;
        this.handle = handle;
    }

    public void bind(CommandBuffer buffer){
        VK10.vkCmdBindPipeline(buffer.getNativeCommandBuffer(), VK10.VK_PIPELINE_BIND_POINT_GRAPHICS, handle);
    }
}
