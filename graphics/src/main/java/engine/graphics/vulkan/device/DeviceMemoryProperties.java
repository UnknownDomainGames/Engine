package engine.graphics.vulkan.device;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

import java.util.List;
import java.util.stream.Collectors;

public class DeviceMemoryProperties {

    private List<MemoryType> memoryTypes;

    private List<MemoryHeap> memoryHeaps;

    private DeviceMemoryProperties(){}

    public static DeviceMemoryProperties fromNative(VkPhysicalDeviceMemoryProperties properties){
        var wrapper = new DeviceMemoryProperties();
        wrapper.memoryTypes = properties.memoryTypes().stream().map(vk->{
            var type = new MemoryType();
            type.property = vk.propertyFlags();
            type.heapIndex = vk.heapIndex();
            return type;
        }).collect(Collectors.toList());
        wrapper.memoryHeaps = properties.memoryHeaps().stream().map(vk->{
            var heap = new MemoryHeap();
            heap.flag = vk.flags();
            heap.size = vk.size();
            return heap;
        }).collect(Collectors.toList());
        return wrapper;
    }

    public List<MemoryType> getMemoryTypes() {
        return memoryTypes;
    }

    public List<MemoryHeap> getMemoryHeaps() {
        return memoryHeaps;
    }

    public static class MemoryType{
        private int property;
        private int heapIndex;

        public int getProperty() {
            return property;
        }

        public int getHeapIndex() {
            return heapIndex;
        }
    }

    public static class MemoryHeap{
        private int flag;
        private long size;

        public int getFlag() {
            return flag;
        }

        public long getSize() {
            return size;
        }
    }
}
