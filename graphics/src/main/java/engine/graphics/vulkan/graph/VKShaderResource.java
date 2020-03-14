package engine.graphics.vulkan.graph;

import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformTexture;
import engine.graphics.vulkan.pipeline.PipelineState;
import engine.graphics.vulkan.shader.VKUniformBlock;

import java.util.ArrayList;
import java.util.List;

public class VKShaderResource implements ShaderResource {
    private final PipelineState state;

    private final List<VKUniformBlock> blocks = new ArrayList<>();

    public VKShaderResource(PipelineState state) {
        this.state = state;
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
    public void refresh() {

    }
}
