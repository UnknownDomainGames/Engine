package engine.graphics.vulkan.pipeline;

import engine.graphics.math.ViewSpace;
import engine.graphics.vulkan.CommandBuffer;
import engine.graphics.vulkan.device.LogicalDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkViewport;

public class Pipeline {

    private long handle;

    private final LogicalDevice device;

    private PipelineState state;
    private PipelineLayout layout;

    public Pipeline(LogicalDevice device, long handle, PipelineState state, PipelineLayout layout) {
        this.device = device;
        this.handle = handle;
        state.parent = this;
        this.state = state;
    }

    public void bind(CommandBuffer buffer){
        VK10.vkCmdBindPipeline(buffer.getNativeCommandBuffer(), VK10.VK_PIPELINE_BIND_POINT_GRAPHICS, handle);
    }

    public PipelineState getState() {
        return state;
    }

    public void setViewSpace(CommandBuffer buffer, ViewSpace viewSpace, int index){
        try(var stack = MemoryStack.stackPush()) {
            state.setViewSpace(viewSpace, index);
            var viewport = VkViewport.callocStack(1, stack);
            viewport.get(0).set(viewSpace.getOrigin().x, viewSpace.getOrigin().y, viewSpace.getSize().x, viewSpace.getSize().y, viewSpace.getDepthRange().x, viewSpace.getDepthRange().y);
            VK10.vkCmdSetViewport(buffer.getNativeCommandBuffer(), index, viewport);
        }
    }
}
