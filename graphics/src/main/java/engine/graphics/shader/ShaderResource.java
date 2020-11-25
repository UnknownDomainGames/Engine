package engine.graphics.shader;

public interface ShaderResource {

    TextureBinding createTextureBinding();

    TextureBinding getTextureBinding(int unit);

    //    @Override
    ImageBinding createImageBinding();

    //    @Override
    ImageBinding getImageBinding(int unit);

    UniformTexture getUniformTexture(String name);

    //    @Override
    UniformImage getUniformImage(String name);

    UniformBlock getUniformBlock(String name);

    void refresh();
}
