package engine.graphics.vulkan.device;

import org.lwjgl.vulkan.VK10;

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
}
