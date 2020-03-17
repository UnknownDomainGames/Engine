package engine.graphics.gl.shader;

import engine.graphics.gl.texture.GLTexture;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.shader.TextureBinding;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL45;

public final class GLTextureBinding implements TextureBinding {
    private final int unit;

    private Texture texture;
    private Sampler sampler;

    public GLTextureBinding(int unit) {
        this.unit = unit;
    }

    @Override
    public int getUnit() {
        return unit;
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
    }
}
