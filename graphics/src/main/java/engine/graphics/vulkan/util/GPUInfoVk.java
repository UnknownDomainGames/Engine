package engine.graphics.vulkan.util;

import engine.graphics.util.GPUInfo;
import engine.graphics.vulkan.device.DeviceMemoryProperties;
import engine.graphics.vulkan.device.PhysicalDevice;

public class GPUInfoVk implements GPUInfo {

    private final PhysicalDevice device;
    private String name;
    private Vendor vendor;
    private String vendorName;
    //In KiB
    private int totalMemory;

    public GPUInfoVk(PhysicalDevice device) {
        this.device = device;
        this.name = device.getDeviceName();
        switch (device.getVenderId()) {
            // From: https://www.reddit.com/r/vulkan/comments/4ta9nj/is_there_a_comprehensive_list_of_the_names_and/d5nso2t/
            case 0x1002:
                vendor = Vendor.AMD;
                vendorName = vendor.name();
                break;
            case 0x1010:
                vendor = Vendor.UNKNOWN;
                vendorName = "ImgTec";
                break;
            case 0x10DE:
                vendor = Vendor.NVIDIA;
                vendorName = vendor.name();
                break;
            case 0x13B5:
                vendor = Vendor.UNKNOWN;
                vendorName = "ARM";
                break;
            case 0x5143:
                vendor = Vendor.UNKNOWN;
                vendorName = "Qualcomm";
                break;
            case 0x8086:
                vendor = Vendor.INTEL;
                vendorName = vendor.name();
                break;
            default:
                vendor = Vendor.UNKNOWN;
                vendorName = vendor.name();
        }
        totalMemory = Math.toIntExact(device.getMemoryProperties().getMemoryHeaps().stream().mapToLong(DeviceMemoryProperties.MemoryHeap::getSize).sum() / 1024);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Vendor getVendor() {
        return vendor;
    }

    @Override
    public String getVendorName() {
        return vendorName;
    }

    @Override
    public int getTotalMemory() {
        return totalMemory;
    }

    @Override
    public int getFreeMemory() {
        return -1;
    }
}
