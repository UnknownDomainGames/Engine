package engine.graphics.vulkan.shader;

import engine.graphics.GraphicsEngine;
import engine.graphics.shader.UniformBlock;
import engine.graphics.util.Struct;
import engine.graphics.vulkan.VKGraphicsBackend;
import engine.graphics.vulkan.buffer.VulkanBuffer;

public class VKUniformBlock implements UniformBlock {
    private final String name;
    private final int binding;
    private final VulkanBuffer buffer;

    private Struct value;

    public VKUniformBlock(String name, int binding, long size) {
        this.name = name;
        this.binding = binding;
        buffer = ((VKGraphicsBackend) GraphicsEngine.getGraphicsBackend()).getAllocator().createBuffer(size, new VulkanBuffer.Usage[]{VulkanBuffer.Usage.UNIFORM_BUFFER});
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Struct get() {
        return value;
    }

    @Override
    public void set(Struct value) {
        this.value = value;
    }
}
