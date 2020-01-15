package nullengine.client.rendering.gl.util;

import nullengine.client.rendering.util.GPUInfo;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.ATIMeminfo.*;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX;
import static org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX;

public final class GPUInfoImpl implements GPUInfo {

    private String name;
    private Vendor vendor;
    private String vendorName;
    private int totalMemory;

    public GPUInfoImpl() {
        this.name = GL11.glGetString(GL11.GL_RENDERER);
        this.vendorName = GL11.glGetString(GL11.GL_VENDOR);

        String lowerVendorName = vendorName.toLowerCase();
        if (lowerVendorName.contains("nvidia")) {
            vendor = Vendor.NVIDIA;
        } else if (lowerVendorName.contains("amd") || lowerVendorName.contains("ati")) {
            vendor = Vendor.AMD;
        } else if (lowerVendorName.contains("inter")) {
            vendor = Vendor.INTER;
        } else {
            vendor = Vendor.UNKNOWN;
        }

        switch (vendor) {
            case NVIDIA:
                totalMemory = glGetInteger(GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX);
                break;
            case AMD:
                totalMemory = glGetInteger(GL_VBO_FREE_MEMORY_ATI) + glGetInteger(GL_TEXTURE_FREE_MEMORY_ATI) +
                        glGetInteger(GL_RENDERBUFFER_FREE_MEMORY_ATI);
                break;
            default:
                totalMemory = -1;
                break;
        }
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
        switch (vendor) {
            case NVIDIA:
                return glGetInteger(GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX);
            case AMD:
                return glGetInteger(GL_VBO_FREE_MEMORY_ATI) + glGetInteger(GL_TEXTURE_FREE_MEMORY_ATI) +
                        glGetInteger(GL_RENDERBUFFER_FREE_MEMORY_ATI);
            default:
                return -1;
        }
    }
}
