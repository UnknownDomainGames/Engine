package engine.graphics.shader;

public interface ShaderResource {

    UniformBlock getUniformBlock(String name);

    UniformTexture getUniformTexture(String name);

    void refresh();
}
