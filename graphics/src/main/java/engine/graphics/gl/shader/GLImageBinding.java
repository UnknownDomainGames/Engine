package engine.graphics.gl.shader;

import engine.graphics.gl.texture.GLColorFormat;
import engine.graphics.gl.texture.GLTexture;
import engine.graphics.gl.texture.GLTexture2D;
import engine.graphics.shader.ImageBinding;
import engine.graphics.texture.Texture;
import org.lwjgl.opengl.GL42C;

public class GLImageBinding implements ImageBinding {
    private final int unit;

    private Texture texture = GLTexture2D.EMPTY;
    private boolean canRead = true;
    private boolean canWrite = true;

    public GLImageBinding(int unit) {
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
    public void set(Texture texture) {
        this.texture = texture;
    }

    @Override
    public boolean canRead() {
        return canRead;
    }

    @Override
    public boolean canWrite() {
        return canWrite;
    }

    @Override
    public void setCanRead(boolean flag) {
        canRead = flag;
        if (!canRead && !canWrite) {
            canWrite = true;
        }
    }

    @Override
    public void setCanWrite(boolean flag) {
        canWrite = flag;
        if (!canWrite && !canRead) {
            canRead = true;
        }
    }

    public void bind() {
        GLTexture glTexture = (GLTexture) texture;
        GL42C.glBindImageTexture(unit, glTexture.getId(), 0, false, 0, canRead && canWrite ? GL42C.GL_READ_WRITE : canRead ? GL42C.GL_READ_ONLY : GL42C.GL_WRITE_ONLY, GLColorFormat.valueOf(glTexture.getFormat()).internalFormat);
    }
}
