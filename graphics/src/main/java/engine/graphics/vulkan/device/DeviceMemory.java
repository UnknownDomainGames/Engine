package engine.graphics.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;

import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class DeviceMemory {
    private long handle;
    private final LogicalDevice device;
    private boolean released;

    public DeviceMemory(long handle, LogicalDevice device){
        this.device = device;
        this.handle = handle;
    }

    public void checkReleased(){
        if(released) throw new IllegalStateException("Memory already released!");
    }

    public void free(){
        checkReleased();
        VK10.vkFreeMemory(device.getNativeDevice(), handle, null);
        released = true;
        handle = 0;
    }

    public long mapMemory(long offset, long size, int flag) {
        try(var stack = MemoryStack.stackPush()) {
            var ptr = stack.mallocPointer(1);
            var err = VK10.vkMapMemory(device.getNativeDevice(), handle, offset, size, flag, ptr);
            if(err != VK_SUCCESS) {
                return VK_NULL_HANDLE;
            }
            return ptr.get(0);
        }
    }

    public void unmapMemory() {
        VK10.vkUnmapMemory(device.getNativeDevice(), handle);
    }
}
