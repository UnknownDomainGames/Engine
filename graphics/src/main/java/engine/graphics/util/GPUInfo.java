package engine.graphics.util;

public interface GPUInfo {

    String getName();

    GPUVendor getVendor();

    String getVendorName();

    int getTotalMemory();

    int getFreeMemory();
}
