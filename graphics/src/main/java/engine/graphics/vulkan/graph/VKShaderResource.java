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
    public Uniform getUniform(String name) {
        return null;
    }

    @Override
    public UniformTexture getUniformTexture(String name) {
        return null;
    }

    @Override
    public UniformImage getUniformImage(String name, boolean canRead, boolean canWrite) {
        return null;
    }

    @Override
    public UniformBlock getUniformBlock(String name, long size) {
        return null;
    }

    @Override
    public void setup() {

    }
}
