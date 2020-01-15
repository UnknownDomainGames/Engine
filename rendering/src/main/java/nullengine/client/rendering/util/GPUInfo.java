package nullengine.client.rendering.util;

public interface GPUInfo {

    enum Vendor {
        NVIDIA, AMD, INTER, UNKNOWN;
    }

    String getName();

    Vendor getVendor();

    String getVendorName();

    int getTotalMemory();

    int getFreeMemory();
}
