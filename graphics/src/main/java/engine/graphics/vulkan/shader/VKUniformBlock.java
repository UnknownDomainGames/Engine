package engine.graphics.vulkan.shader;

import engine.graphics.GraphicsEngine;
import engine.graphics.shader.UniformBlock;
import engine.graphics.util.Struct;
import engine.graphics.vulkan.VKGraphicsBackend;
import engine.graphics.vulkan.buffer.VulkanBuffer;
import org.joml.*;

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
    public void set(long offset, int value) {

    }

    @Override
    public void set(long offset, float value) {

    }

    @Override
    public void set(long offset, int x, int y) {

    }

    @Override
    public void set(long offset, float x, float y) {

    }

    @Override
    public void set(long offset, int x, int y, int z) {

    }

    @Override
    public void set(long offset, float x, float y, float z) {

    }

    @Override
    public void set(long offset, int x, int y, int z, int w) {

    }

    @Override
    public void set(long offset, float x, float y, float z, float w) {

    }

    @Override
    public void set(long offset, Matrix2fc matrix) {

    }

    @Override
    public void set(long offset, Matrix3x2fc matrix) {

    }

    @Override
    public void set(long offset, Matrix3fc matrix) {

    }

    @Override
    public void set(long offset, Matrix4x3fc matrix) {

    }

    @Override
    public void set(long offset, Matrix4fc matrix) {

    }

    @Override
    public void set(long offset, Struct struct) {

    }
}
