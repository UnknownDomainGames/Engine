package engine.graphics.vulkan;

import engine.graphics.backend.ResourceFactory;
import engine.graphics.mesh.MultiBufMesh;
import engine.graphics.mesh.SingleBufMesh;
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
    public MultiBufMesh.Builder createMeshBuilder() {
        return null;
    }

    @Override
    public SingleBufMesh.Builder createSingleBufferMeshBuilder() {
        return null;
    }
}
