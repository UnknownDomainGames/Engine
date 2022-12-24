package engine.graphics.gl.shader;

import engine.graphics.gl.texture.GLSampler;
import engine.graphics.gl.texture.GLTexture;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.shader.UniformTexture;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture;
import org.lwjgl.opengl.*;

public final class GLUniformTexture extends GLUniform implements UniformTexture {
    private final int location;
    private final int unit;

    private Texture texture = GLTexture.NONE;
    private Sampler sampler = GLSampler.NONE;

    public GLUniformTexture(String name, int location, int unit) {
        super(name);
        this.location = location;
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
    public void setTexture(Texture texture) {
        this.texture = texture != null ? texture : GLTexture.NONE;
        GLTexture glTexture = (GLTexture) this.texture;
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45C.glBindTextureUnit(unit, glTexture.getId());
        } else {
            GL13C.glActiveTexture(GL13C.GL_TEXTURE0 + unit);
            GL11C.glBindTexture(glTexture.getTarget(), glTexture.getId());
        }
    }

    @Override
    public Sampler getSampler() {
        return sampler;
    }

    @Override
    public void setSampler(Sampler sampler) {
        this.sampler = sampler != null ? sampler : GLSampler.NONE;
        GL33C.glBindSampler(unit, sampler.getId());
    }

    public void bind() {
        GL20C.glUniform1i(location, unit);
    }
}
