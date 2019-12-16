package nullengine.client.rendering.util;

public interface GPUInfo {

    String getName();

    String getVendor();

    int getTotalMemory();

    int getFreeMemory();
}
