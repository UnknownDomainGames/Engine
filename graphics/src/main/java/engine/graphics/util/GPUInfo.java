package engine.graphics.util;

public interface GPUInfo {

    enum Vendor {
        NVIDIA, AMD, INTEL, UNKNOWN;
    }

    String getName();

    Vendor getVendor();

    String getVendorName();

    int getTotalMemory();

    int getFreeMemory();
}
