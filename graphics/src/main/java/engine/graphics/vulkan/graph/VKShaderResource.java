package engine.graphics.vulkan.graph;

import engine.graphics.shader.*;
import engine.graphics.vulkan.pipeline.PipelineLayout;
import engine.graphics.vulkan.shader.VKUniformBlock;

import java.util.ArrayList;
import java.util.List;

public class VKShaderResource implements ShaderResource {
    private final PipelineLayout state;

    private final List<VKUniformBlock> blocks = new ArrayList<>();

    public VKShaderResource(PipelineLayout state) {
        this.state = state;
    }

    @Override
    public TextureBinding createTextureBinding() {
        return null;
    }

    @Override
    public TextureBinding getTextureBinding(int unit) {
        return null;
    }

    @Override
    public ImageBinding createImageBinding() {
        return null;
    }

    @Override
    public ImageBinding getImageBinding(int unit) {
        return null;
    }

    @Override
    public UniformBlock getUniformBlock(String name) {
        return null;
    }

    @Override
    public UniformTexture getUniformTexture(String name) {
        return null;
    }

    @Override
    public UniformImage getUniformImage(String name) {
        return null;
    }

    @Override
    public void refresh() {

    }
}
