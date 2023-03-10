package engine.graphics.vulkan.util;

import engine.graphics.util.GPUInfo;
import engine.graphics.util.GPUVendor;
import engine.graphics.vulkan.device.DeviceMemoryProperties;
import engine.graphics.vulkan.device.PhysicalDevice;

public class GPUInfoVk implements GPUInfo {

    private final PhysicalDevice device;
    private String name;
    private GPUVendor vendor;
    private String vendorName;
    //In KiB
    private int totalMemory;

    public GPUInfoVk(PhysicalDevice device) {
        this.device = device;
        this.name = device.getDeviceName();
        switch (device.getVenderId()) {
            // From: https://www.reddit.com/r/vulkan/comments/4ta9nj/is_there_a_comprehensive_list_of_the_names_and/d5nso2t/
            case 0x1002:
                vendor = GPUVendor.AMD;
                vendorName = vendor.name();
                break;
            case 0x1010:
                vendor = GPUVendor.UNKNOWN;
                vendorName = "ImgTec";
                break;
            case 0x10DE:
                vendor = GPUVendor.NVIDIA;
                vendorName = vendor.name();
                break;
            case 0x13B5:
                vendor = GPUVendor.UNKNOWN;
                vendorName = "ARM";
                break;
            case 0x5143:
                vendor = GPUVendor.UNKNOWN;
                vendorName = "Qualcomm";
                break;
            case 0x8086:
                vendor = GPUVendor.INTEL;
                vendorName = vendor.name();
                break;
            default:
                vendor = GPUVendor.UNKNOWN;
                vendorName = vendor.name();
        }
        totalMemory = Math.toIntExact(device.getMemoryProperties().getMemoryHeaps().stream().mapToLong(DeviceMemoryProperties.MemoryHeap::getSize).sum() / 1024);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public GPUVendor getVendor() {
        return vendor;
    }

    @Override
    public String getVendorName() {
        return vendorName;
    }

    @Override
    public long getTotalMemory() {
        return totalMemory;
    }

    @Override
    public long getUsedMemory() {
        return -1;
    }

    @Override
    public long getFreeMemory() {
        return -1;
    }
}
