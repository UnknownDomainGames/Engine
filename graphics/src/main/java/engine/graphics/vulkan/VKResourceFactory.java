package engine.graphics.vulkan;

import engine.graphics.management.ResourceFactory;
import engine.graphics.mesh.Mesh;
import engine.graphics.mesh.SingleBufferMesh;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture2D;
import engine.graphics.vulkan.device.LogicalDevice;

public class VKResourceFactory implements ResourceFactory {

    private final LogicalDevice device;

    public VKResourceFactory(LogicalDevice device) {
        this.device = device;
    }

    @Override
    public Texture2D.Builder createTexture2DBuilder() {
        return null;
    }

    @Override
    public Sampler createSampler() {
        return null;
    }

    @Override
    public Mesh.Builder createMeshBuilder() {
        return null;
    }

    @Override
    public SingleBufferMesh.Builder createSingleBufferMeshBuilder() {
        return null;
    }
}
