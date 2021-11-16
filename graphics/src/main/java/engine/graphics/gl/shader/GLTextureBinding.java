package engine.graphics.gl.shader;

import engine.graphics.gl.texture.GLSampler;
import engine.graphics.gl.texture.GLTexture;
import engine.graphics.gl.texture.GLTexture2D;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.shader.TextureBinding;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GL45C;

public final class GLTextureBinding implements TextureBinding {
    private final int unit;

    private Texture texture = GLTexture2D.EMPTY;
    private Sampler sampler = GLSampler.DEFAULT;

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
        set(texture, GLSampler.DEFAULT);
    }

    @Override
    public void set(Texture texture, Sampler sampler) {
        this.texture = texture == null ? GLTexture2D.EMPTY : texture;
        this.sampler = sampler == null ? GLSampler.DEFAULT : sampler;
    }

    public void bind() {
        GLTexture glTexture = (GLTexture) texture;
        if (texture == GLTexture2D.EMPTY) return;
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45C.glBindTextureUnit(unit, glTexture.getId());
        } else {
            GL13C.glActiveTexture(GL13C.GL_TEXTURE0 + unit);
            GL11C.glBindTexture(glTexture.getTarget(), glTexture.getId());
        }
        GL33C.glBindSampler(unit, sampler.getId());
    }
}
