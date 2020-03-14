package engine.graphics.gl.shader;

import engine.graphics.gl.texture.GLTexture;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.shader.UniformTexture;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture;
import org.lwjgl.opengl.*;

public final class GLUniformTexture implements UniformTexture {
    private final String name;
    private final int location;
    private final int unit;

    private Texture texture;
    private Sampler sampler;

    public GLUniformTexture(String name, int location, int unit) {
        this.name = name;
        this.location = location;
        this.unit = unit;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public Sampler getSampler() {
        return sampler;
    }

    @Override
    public void set(Texture texture) {
        set(texture, null);
    }

    @Override
    public void set(Texture texture, Sampler sampler) {
        this.texture = texture;
        this.sampler = sampler;
    }

    public void bind() {
        if (texture != null) {
            GLTexture glTexture = (GLTexture) texture;
            if (GLHelper.isOpenGL45()) {
                GL45.glBindTextureUnit(unit, glTexture.getId());
            } else {
                GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
                GL11.glBindTexture(glTexture.getTarget(), glTexture.getId());
            }
        }

        if (sampler != null) {
            GL33.glBindSampler(unit, sampler.getId());
        }

        GL20.glUniform1i(location, unit);
    }
}
