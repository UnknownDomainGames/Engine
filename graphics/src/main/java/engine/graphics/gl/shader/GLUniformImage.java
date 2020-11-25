package engine.graphics.gl.shader;

import engine.graphics.shader.ImageBinding;
import engine.graphics.shader.UniformImage;
import engine.graphics.texture.Texture;
import org.lwjgl.opengl.GL20;

public class GLUniformImage implements UniformImage {
    private final String name;
    private final int location;
    private final ImageBinding binding;

    public GLUniformImage(String name, int location, ImageBinding binding) {
        this.name = name;
        this.location = location;
        this.binding = binding;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ImageBinding getBinding() {
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
    public void set(Texture texture) {
        binding.set(texture);
    }

    public void bind() {
        GL20.glUniform1i(location, binding.getUnit());
    }
}
