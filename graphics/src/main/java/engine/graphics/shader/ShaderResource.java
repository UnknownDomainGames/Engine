package engine.graphics.shader;

public interface ShaderResource {

    TextureBinding createTextureBinding();

    TextureBinding getTextureBinding(int unit);

    UniformTexture getUniformTexture(String name);

    UniformBlock getUniformBlock(String name);

    void refresh();
}
