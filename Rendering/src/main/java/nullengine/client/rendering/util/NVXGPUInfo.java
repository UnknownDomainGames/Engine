package nullengine.client.rendering.util;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX;
import static org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX;

public class NVXGPUInfo implements GPUInfo {

    private int totalMemory = -1;
    private int freeMemory = -1;

    @Override
    public int getTotalMemory() {
        return totalMemory;
    }

    @Override
    public int getFreeMemory() {
        return freeMemory;
    }

    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(1);
            GL20.glGetIntegerv(GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX, buffer);
            totalMemory = buffer.get();
        }
        update();
    }

    public void update() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(1);
            GL20.glGetIntegerv(GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX, buffer);
            freeMemory = buffer.get();
        }
    }
}
