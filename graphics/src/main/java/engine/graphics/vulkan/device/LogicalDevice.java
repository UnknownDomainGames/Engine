package engine.graphics.vulkan.device;

import engine.graphics.vulkan.CommandPool;
import engine.graphics.vulkan.Pipeline;
import engine.graphics.vulkan.Queue;
import engine.graphics.vulkan.VulkanBuffer;
import engine.graphics.vulkan.shader.VulkanShader;
import engine.graphics.vulkan.texture.VKTexture;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.stream.Stream;

import static org.lwjgl.vulkan.VK10.*;

public class LogicalDevice {
    private VkDevice vk;
    private final PhysicalDevice physicalDevice;
    private boolean released = false;

    public LogicalDevice(PhysicalDevice physicalDevice, VkDevice device){
        this.vk = device;
        this.physicalDevice = physicalDevice;
    }

    public VulkanBuffer createBuffer(long size, VulkanBuffer.Usage[] usage){
        return createBufferInternal(size,usage,false, new int[0]);
    }

    public VulkanBuffer createSharedBuffer(long size, VulkanBuffer.Usage[] usage, int[] index){
        return createBufferInternal(size, usage, true, index);
    }

    private VulkanBuffer createBufferInternal(long size, VulkanBuffer.Usage[] usage, boolean shared, int[] queueFamilyIndices){
        try(var stack = MemoryStack.stackPush()){
            var createInfo = VkBufferCreateInfo.callocStack(stack);
            createInfo.sType(VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
            .size(size).usage(Stream.of(usage).mapToInt(VulkanBuffer.Usage::getVk).reduce(0,(flag1,flag2)->flag1|flag2)).sharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE);
            if(shared){
                var queues = stack.mallocInt(queueFamilyIndices.length).put(queueFamilyIndices).flip();
                createInfo.sharingMode(VK10.VK_SHARING_MODE_CONCURRENT).pQueueFamilyIndices(queues);
            }
            var ptr = stack.mallocLong(1);
            var err = VK10.vkCreateBuffer(vk,createInfo,null, ptr);
            if(err != VK_SUCCESS){
                return null;
            }
            var handle = ptr.get(0);
            return new VulkanBuffer(this, handle);
        }
    }

    public VKTexture createTexture(int width, int height, VKTexture.Format format, VKTexture.Usage usage, VKTexture.Layout layout){
        return createTextureInternal(width, height, 0, format, usage, layout, false, new int[0]);
    }
    public VKTexture createSharedTexture(int width, int height, VKTexture.Format format, VKTexture.Usage usage, VKTexture.Layout layout, int[] queueFamilyIndex){
        return createTextureInternal(width, height, 0, format, usage, layout, true, queueFamilyIndex);
    }

    private VKTexture createTextureInternal(int width, int height, int flag, VKTexture.Format format, VKTexture.Usage usage, VKTexture.Layout layout, boolean shared, int[] queueFamilyIndices){
        try(var stack = MemoryStack.stackPush()) {
            var createInfo = VkImageCreateInfo.callocStack(stack).sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO);
            createInfo.imageType(VK_IMAGE_TYPE_2D).samples(VK_SAMPLE_COUNT_1_BIT).mipLevels(0)
                    .extent(vkExtent3D -> vkExtent3D.set(width, height,1))
            .format(format.getVk()).usage(usage.getVk()).initialLayout(layout.getVk()).arrayLayers(1).flags(flag).sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            if(shared){
                var queues = stack.mallocInt(queueFamilyIndices.length).put(queueFamilyIndices).flip();
                createInfo.sharingMode(VK10.VK_SHARING_MODE_CONCURRENT).pQueueFamilyIndices(queues);
            }
            var ptr = stack.mallocLong(1);
            var err = vkCreateImage(vk, createInfo, null, ptr);
            if(err != VK_SUCCESS){
                return null;
            }
            return new VKTexture(this, ptr.get(0));
        }
    }

    public Queue getQueue(int queueFamilyIndex, int queueIndex){
        if (queueFamilyIndex >= physicalDevice.getQueueFamilyCount()) throw new IllegalArgumentException(String.format("QueueFamily out of bound. size: %d index: %d", physicalDevice.getQueueFamilyCount(), queueFamilyIndex));
        if (queueIndex >= physicalDevice.getQueueFamilyPropertiesList().get(queueFamilyIndex).queueCount())
            throw new IllegalArgumentException(String.format("Queue from QueueFamily#%d out of bound. size: %d index: %d", queueFamilyIndex, physicalDevice.getQueueFamilyPropertiesList().get(queueFamilyIndex).queueCount(), queueIndex));
        try(var stack = MemoryStack.stackPush()) {
            var ptr = stack.mallocPointer(1);
            VK10.vkGetDeviceQueue(vk, queueFamilyIndex, queueIndex, ptr);
            return new Queue(new VkQueue(ptr.get(0), vk));
        }
    }


    public CommandPool createCommandPool(int queueFamilyIndex) {
        if (queueFamilyIndex >= physicalDevice.getQueueFamilyCount()) throw new IllegalArgumentException(String.format("QueueFamily out of bound. size: %d index: %d", physicalDevice.getQueueFamilyCount(), queueFamilyIndex));
        try(var stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo cmdPoolInfo = VkCommandPoolCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                    .queueFamilyIndex(queueFamilyIndex)
                    .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT | VK_COMMAND_POOL_CREATE_TRANSIENT_BIT);
            var pCmdPool = stack.mallocLong(1);
            int err = vkCreateCommandPool(vk, cmdPoolInfo, null, pCmdPool);
            if (err != VK_SUCCESS) {
//                throw new AssertionError("Failed to create command pool: " + translateVulkanResult(err));
                return null;
            }
            return new CommandPool(pCmdPool.get(0), this);
        }
    }

    public Pipeline createPipeline(VulkanShader... shaders){
        try(var stack = MemoryStack.stackPush()){
            VkGraphicsPipelineCreateInfo.Buffer infos = VkGraphicsPipelineCreateInfo.callocStack(1, stack);

            LongBuffer ptrs = stack.mallocLong(1);
            vkCreateGraphicsPipelines(vk, 0, infos, null, ptrs);
            return new Pipeline(this, ptrs.get(0));
        }
    }

    public DeviceMemory allocateMemory(long size, int typeBits){
        try(var stack = MemoryStack.stackPush()){
            var allocateInfo = VkMemoryAllocateInfo.callocStack(stack);
            var memoryType = getMemoryType(typeBits);
            if(memoryType == -1) return null;
            allocateInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO).allocationSize(size).memoryTypeIndex(memoryType);
            var ptr = stack.mallocLong(1);
            var err = vkAllocateMemory(vk, allocateInfo, null, ptr);
            if(err != VK_SUCCESS){
                return null;
            }
            return new DeviceMemory(ptr.get(0), this);
        }
    }

    private int getMemoryType(int typeBits) {
        int bits = typeBits;
        for (int i = 0; i < 32; i++) {
            if ((bits & 1) == 1) {
                if ((physicalDevice.getMemoryProperties().getMemoryTypes().get(i).getProperty() & VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT) != 0) {
                    return i;
                }
            }
            bits >>= 1;
        }
        return -1;
    }

    public void waitIdle(){
        vkDeviceWaitIdle(vk);
    }

    public void free(){
        VK10.vkDestroyDevice(vk, null);
        vk = null;
        released = true;
    }

    public VkDevice getNativeDevice() {
        return vk;
    }

    public PhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }
}
