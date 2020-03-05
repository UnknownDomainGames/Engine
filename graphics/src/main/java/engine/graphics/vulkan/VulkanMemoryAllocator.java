package engine.graphics.vulkan;

import engine.graphics.vulkan.buffer.VulkanBuffer;
import engine.graphics.vulkan.device.LogicalDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferCreateInfo;

import java.util.List;
import java.util.stream.Stream;

import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class VulkanMemoryAllocator {
    private final LogicalDevice device;
    private long handle;
    private boolean released = false;

    private VulkanMemoryAllocator(LogicalDevice device, long handle){
        this.device = device;
        this.handle = handle;
    }

    public static VulkanMemoryAllocator createAllocator(LogicalDevice device) {
        try(var stack = MemoryStack.stackPush()){
            var info = VmaAllocatorCreateInfo.callocStack(stack)
                    .physicalDevice(device.getPhysicalDevice().getNativePhysicalDevice())
                    .device(device.getNativeDevice());
            var ptr = stack.mallocPointer(1);
            var err = Vma.vmaCreateAllocator(info, ptr);
            if(err != VK_SUCCESS){
                return null;
            }
            return new VulkanMemoryAllocator(device, ptr.get(0));
        }
    }

    public VulkanBuffer createBuffer(long size, VulkanBuffer.Usage[] usage){
        return createBuffer(size, usage, false, new int[0]);
    }

    public VulkanBuffer createBuffer(long size, VulkanBuffer.Usage[] usage, boolean shared, int[] queueFamilyIndices) {
        try(var stack= MemoryStack.stackPush()) {
            var createInfo = VkBufferCreateInfo.callocStack(stack).sType(VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            createInfo.size(size).usage(Stream.of(usage).mapToInt(VulkanBuffer.Usage::getVk).reduce(0,(flag1, flag2)->flag1|flag2)).sharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE);
            if(shared){
                var queues = stack.mallocInt(queueFamilyIndices.length).put(queueFamilyIndices).flip();
                createInfo.sharingMode(VK10.VK_SHARING_MODE_CONCURRENT).pQueueFamilyIndices(queues);
            }
            var allocateInfo = VmaAllocationCreateInfo.callocStack(stack);
            allocateInfo.usage(Vma.VMA_MEMORY_USAGE_CPU_TO_GPU);
            var bufptr = stack.mallocLong(1);
            var alloptr = stack.mallocPointer(1);
            var err = Vma.vmaCreateBuffer(handle, createInfo, allocateInfo, bufptr, alloptr, null);
            if(err != VK_SUCCESS) {
                return null;
            }
            return new VulkanBuffer(device, bufptr.get(0), alloptr.get(0), List.of(usage));
        }
    }

    public long getHandle() {
        return handle;
    }

    public void dispose() {
        if(released) return;
        Vma.vmaDestroyAllocator(handle);
        released = true;
        handle = 0;
    }
}
