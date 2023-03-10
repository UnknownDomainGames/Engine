package engine.graphics.util;

public interface GPUInfo {

    String getName();

    GPUVendor getVendor();

    String getVendorName();

    /**
     * Total size of GPU memory.
     *
     * @return bytes of total memory.
     */
    long getTotalMemory();

    /**
     * Used size of GPU memory.
     *
     * @return bytes of used memory.
     */
    long getUsedMemory();

    /**
     * Free size of GPU memory.
     *
     * @return bytes of free memory.
     */
    long getFreeMemory();
}
