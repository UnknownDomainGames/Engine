package engine.graphics.gl.shader;

import engine.graphics.gl.texture.GLTexture;
import engine.graphics.shader.UniformImage;
import engine.graphics.texture.Texture;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL42C;

public class GLUniformImage extends GLUniform implements UniformImage {
    private final int location;
    private final int unit;
    private final int access;

    private Texture texture = GLTexture.NONE;

    public GLUniformImage(String name, int location, int unit, int access) {
        super(name);
        this.location = location;
        this.unit = unit;
        this.access = access;
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
        GL42C.glBindImageTexture(unit, glTexture.getId(), 0, false, 0, access, glTexture.getGLFormat().internalFormat);
    }

    public void bind() {
        GL20C.glUniform1i(location, unit);
    }
}
