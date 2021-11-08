package engine.graphics.gl.shader;

import engine.graphics.shader.TextureBinding;
import engine.graphics.shader.UniformTexture;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture;
import org.lwjgl.opengl.GL20C;

public final class GLUniformTexture implements UniformTexture {
    private final String name;
    private final int location;
    private final TextureBinding binding;

    public GLUniformTexture(String name, int location, TextureBinding binding) {
        this.name = name;
        this.location = location;
        this.binding = binding;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TextureBinding getBinding() {
        return binding;
    }

    @Override
    public int getUnit() {
        return binding.getUnit();
    }

    @Override
    public Texture getTexture() {
        return binding.getTexture();
    }

    @Override
    public Sampler getSampler() {
        return binding.getSampler();
    }

    @Override
    public void set(Texture texture) {
        binding.set(texture);
    }

    @Override
    public void set(Texture texture, Sampler sampler) {
        binding.set(texture, sampler);
    }

    public void bind() {
        GL20C.glUniform1i(location, binding.getUnit());
    }
}
