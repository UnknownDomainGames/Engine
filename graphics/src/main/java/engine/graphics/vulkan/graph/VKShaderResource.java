package engine.graphics.vulkan.graph;

import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformTexture;
import engine.graphics.vulkan.pipeline.PipelineState;
import engine.graphics.vulkan.shader.VKUniformBlock;
import org.joml.*;

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
    public void setUniform(String name, int value) {
        //Unsupported
    }

    @Override
    public void setUniform(String name, float value) {
        //Unsupported
    }

    @Override
    public void setUniform(String name, boolean value) {
        //Unsupported
    }

    @Override
    public void setUniform(String name, Vector2fc value) {
        //Unsupported
    }

    @Override
    public void setUniform(String name, Vector3fc value) {
        //Unsupported
    }

    @Override
    public void setUniform(String name, Vector4fc value) {
        //Unsupported
    }

    @Override
    public void setUniform(String name, Matrix3fc value) {
        //Unsupported
    }

    @Override
    public void setUniform(String name, Matrix3x2fc value) {
        //Unsupported
    }

    @Override
    public void setUniform(String name, Matrix4fc value) {
        //Unsupported
    }

    @Override
    public void setUniform(String name, Matrix4x3fc value) {
        //Unsupported
    }

    @Override
    public void setUniform(String name, Matrix4fc[] values) {
        //Unsupported
    }

    @Override
    public void refresh() {

    }
}
