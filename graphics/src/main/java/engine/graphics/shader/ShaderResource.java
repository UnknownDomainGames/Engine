package engine.graphics.shader;

public interface ShaderResource {
    UniformTexture getUniformTexture(String name);

    UniformImage getUniformImage(String name, boolean canRead, boolean canWrite);

    UniformBlock getUniformBlock(String name, long size);

    void setup();
}
