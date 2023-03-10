package engine.graphics.gl.util;

import engine.graphics.util.GPUInfo;
import engine.graphics.util.GPUVendor;

import static org.lwjgl.opengl.ATIMeminfo.*;
import static org.lwjgl.opengl.GL11C.glGetInteger;
import static org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX;
import static org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX;

public final class GLGPUInfo implements GPUInfo {
    private final String name;
    private final GPUVendor vendor;
    private final String vendorName;
    private final long totalMemory;

    public GLGPUInfo() {
        this.name = GLHelper.getRenderer();
        this.vendorName = GLHelper.getVendor();

        String lowerVendorName = vendorName.toLowerCase();
        if (lowerVendorName.contains("nvidia")) {
            vendor = GPUVendor.NVIDIA;
            totalMemory = (long) glGetInteger(GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX) << 10;
        } else if (lowerVendorName.contains("amd") || lowerVendorName.contains("ati")) {
            vendor = GPUVendor.AMD;
            totalMemory = (long) (glGetInteger(GL_VBO_FREE_MEMORY_ATI) + glGetInteger(GL_TEXTURE_FREE_MEMORY_ATI) +
                    glGetInteger(GL_RENDERBUFFER_FREE_MEMORY_ATI)) << 10;
        } else if (lowerVendorName.contains("intel")) {
            vendor = GPUVendor.INTEL;
            totalMemory = -1;
        } else {
            vendor = GPUVendor.UNKNOWN;
            totalMemory = -1;
        }
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
        return getTotalMemory() - getFreeMemory();
    }

    @Override
    public long getFreeMemory() {
        switch (vendor) {
            case NVIDIA:
                return (long) glGetInteger(GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX) << 10;
            case AMD:
                return (long) (glGetInteger(GL_VBO_FREE_MEMORY_ATI) + glGetInteger(GL_TEXTURE_FREE_MEMORY_ATI) +
                        glGetInteger(GL_RENDERBUFFER_FREE_MEMORY_ATI)) << 10;
            default:
                return -1;
        }
    }
}
