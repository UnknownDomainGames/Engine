package engine.graphics.vulkan;

import engine.graphics.vulkan.util.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.List;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.VK10.*;

public class CommandBuffer {
    private VkCommandBuffer vkbuffer;
    private final CommandPool pool;
    private boolean released;

    public VkCommandBuffer getNativeCommandBuffer() {
        return vkbuffer;
    }

    public CommandBuffer(VkCommandBuffer vkbuffer, CommandPool pool){
        this.vkbuffer = vkbuffer;
        this.pool = pool;
    }

    public int beginCommandBuffer(){
        checkReleased();
        try(var stack = MemoryStack.stackPush()) {
            var info = VkCommandBufferBeginInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            return vkBeginCommandBuffer(vkbuffer, info);
        }
    }

    private void checkReleased() {
        if(released) throw new IllegalStateException("CommandBuffer already released!");
    }

    public int endCommandBuffer(){
        checkReleased();
        return vkEndCommandBuffer(vkbuffer);
    }

    public void submitCommandBuffer(VkQueue queue){
        checkReleased();
        try(var stack = MemoryStack.stackPush()) {
            VkSubmitInfo submitInfo = VkSubmitInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            var pCommandBuffers = stack.mallocPointer(1)
                    .put(vkbuffer)
                    .flip();
            submitInfo.pCommandBuffers(pCommandBuffers);
            int err = vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to submit command buffer: " + VulkanUtils.translateVulkanResult(err));
            }
        }
    }

    public void resetCommandBuffer(int flag){
        checkReleased();
        vkResetCommandBuffer(vkbuffer, flag);
    }

    public void freeCommandBuffer(){
        checkReleased();
        vkFreeCommandBuffers(pool.getDevice().getNativeDevice(), pool.getCommandPoolPointer(), vkbuffer);
        vkbuffer = null;
        released = true;
    }

//    public void drawArrays(long pipeline, GLBuffer buffer){
//        vkCmdBindPipeline(vkbuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);
//        var lb = memAllocLong(1);
//        lb.put(0,0);
//        vkCmdBindVertexBuffers(vkbuffer, 0, 1, &model.vertices.buffer, lb);
//        vkCmdDraw(vkbuffer, model.indexCount, 1, 0, 0, 0);
//    }

    public void put(VulkanBuffer src, VulkanBuffer dst, List<CmdCopyRegion> regionList){
        checkReleased();
        try(var stack = MemoryStack.stackPush()) {
            var regions = VkBufferCopy.callocStack(regionList.size(), stack);
            for (int i = 0; i < regionList.size(); i++) {
                var region = regions.get(i);
                var warpedRegion = regionList.get(i);
                region.srcOffset(warpedRegion.srcOffset()).dstOffset(warpedRegion.dstOffset()).size(warpedRegion.size());
            }
            vkCmdCopyBuffer(getNativeCommandBuffer(), src.getHandle(), dst.getHandle(), regions);
        }
    }

    public void fill(VulkanBuffer buf, long size, long offset, int data){
        vkCmdFillBuffer(vkbuffer, buf.getHandle(), offset, size, data);
    }

    public static class CmdCopyRegion{
        private long srcOffset;
        private long dstOffset;
        private long size;

        public CmdCopyRegion srcOffset(long value){
            this.srcOffset = value;
            return this;
        }

        public long srcOffset() {
            return srcOffset;
        }

        public CmdCopyRegion dstOffset(long value){
            this.dstOffset = value;
            return this;
        }

        public long dstOffset() {
            return srcOffset;
        }
        public CmdCopyRegion size(long value){
            this.size = value;
            return this;
        }

        public long size() {
            return size;
        }
    }
}
