package engine.graphics.vulkan.buffer;

import engine.graphics.GraphicsEngine;
import engine.graphics.vulkan.VKGraphicsBackend;
import engine.graphics.vulkan.VulkanMemoryAllocator;
import engine.graphics.vulkan.device.DeviceMemory;
import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.texture.ColorFormat;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferViewCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;

import java.nio.ByteBuffer;
import java.util.List;

public class VulkanBuffer {
    private long handle;
    private final LogicalDevice device;
    private DeviceMemory memory;
    private List<Usage> usages;
    private boolean released = false;

    //Vma section
    private long allocationHandle;

    public VulkanBuffer(LogicalDevice device, long handle, List<Usage> usages){
        this.device = device;
        this.handle = handle;
        this.usages = usages;
    }

    public VulkanBuffer(LogicalDevice device, long handle, long allocationHandle, List<Usage> usages){
        this.device = device;
        this.handle = handle;
        this.allocationHandle = allocationHandle;
        this.usages = usages;
    }

    public long getHandle() {
        return handle;
    }

    public void free(){
        if(released) return;
        VK10.vkDestroyBuffer(device.getNativeDevice(), handle, null);
        handle = 0;
        released = true;
    }

    public void uploadData(ByteBuffer buffer) {
        checkReleased();
        try(var stack = MemoryStack.stackPush()) {
            if (allocationHandle != 0) {
                var mappedPtr = stack.mallocPointer(1);
                var allocator = ((VKGraphicsBackend) GraphicsEngine.getGraphicsBackend()).getAllocator();
                Vma.vmaMapMemory(allocator.getHandle(), allocationHandle, mappedPtr);
                MemoryUtil.memCopy(MemoryUtil.memAddress(buffer), mappedPtr.get(0), buffer.remaining());
                Vma.vmaUnmapMemory(allocator.getHandle(), allocationHandle);
            }
            else {
                var requirement = VkMemoryRequirements.callocStack(stack);
                VK10.vkGetBufferMemoryRequirements(device.getNativeDevice(), handle, requirement);
                if(memory == null) {
                    memory = device.allocateMemory(requirement.size(), requirement.memoryTypeBits());
                }
                var ptr = memory.mapMemory(0, VK10.VK_WHOLE_SIZE, 0);
                MemoryUtil.memCopy(MemoryUtil.memAddress(buffer), ptr, buffer.remaining());
                memory.unmapMemory();
            }
        }
    }

    private void checkReleased() {
        if(released) throw new IllegalStateException("Buffer has been released!");
    }

    public BufferView createView(ColorFormat format, long offset, long size){
        if (!usages.contains(Usage.UNIFORM_TEXEL_BUFFER) && !usages.contains(Usage.STORAGE_TEXEL_BUFFER)) {
            return null;
        }
        try(var stack = MemoryStack.stackPush()){
            var info = VkBufferViewCreateInfo.callocStack(stack).sType(VK10.VK_STRUCTURE_TYPE_BUFFER_VIEW_CREATE_INFO);
            info.buffer(handle).format(format.getVk()).offset(offset).range(size);
            var ptr = stack.mallocLong(1);
            var err = VK10.vkCreateBufferView(device.getNativeDevice(), info, null, ptr);
            if(err != VK10.VK_SUCCESS){
                return null;
            }
            return new BufferView(this, ptr.get(0));
        }
    }

    public enum Usage {
        INDEX_BUFFER(VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT),
        VERTEX_BUFFER(VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT),
        SOURCE_BUFFER(VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT),
        DESTINATION_BUFFER(VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT),
        UNIFORM_TEXEL_BUFFER(VK10.VK_BUFFER_USAGE_UNIFORM_TEXEL_BUFFER_BIT),
        STORAGE_TEXEL_BUFFER(VK10.VK_BUFFER_USAGE_STORAGE_TEXEL_BUFFER_BIT),
        UNIFORM_BUFFER(VK10.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT),
        STORAGE_BUFFER(VK10.VK_BUFFER_USAGE_STORAGE_BUFFER_BIT),
        INDIRECT_BUFFER(VK10.VK_BUFFER_USAGE_INDIRECT_BUFFER_BIT);
        private final int vk;

        Usage(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }

    public static class BufferView {
        private VulkanBuffer parentBuffer;
        private long handle;
        private boolean released = false;

        private BufferView(VulkanBuffer buffer, long handle){
            this.parentBuffer = buffer;
            this.handle = handle;
        }

        public long getHandle() {
            return handle;
        }

        public void free(){
            VK10.vkDestroyBufferView(parentBuffer.device.getNativeDevice(), handle, null);
            handle = 0;
            released = true;
        }
    }
}
