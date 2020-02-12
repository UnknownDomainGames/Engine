package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.texture.RenderBuffer;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.TextureFormat;
import engine.graphics.util.Cleaner;
import org.lwjgl.opengl.GL30;

import javax.annotation.Nullable;

public final class GLRenderBuffer implements RenderBuffer {

    private int id;
    private Cleaner.Disposable disposable;

    private GLTextureFormat format;
    private Sampler sampler;
    private int width;
    private int height;

    public GLRenderBuffer(TextureFormat format, int width, int height) {
        id = GL30.glGenRenderbuffers();
        disposable = GLCleaner.registerRenderBuffer(this, id);
        this.format = GLTextureFormat.valueOf(format);
        this.width = width;
        this.height = height;
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, this.format.internalFormat, width, height);
    }

    public GLRenderBuffer(TextureFormat format, Sampler sampler, int width, int height) {
        id = GL30.glGenRenderbuffers();
        disposable = GLCleaner.registerRenderBuffer(this, id);
        this.format = GLTextureFormat.valueOf(format);
        this.sampler = sampler;
        this.width = width;
        this.height = height;
        GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, sampler.getId(), this.format.internalFormat, width, height);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public TextureFormat getFormat() {
        return format.peer;
    }

    @Override
    public boolean isMultiSample() {
        return sampler != null;
    }

    @Nullable
    @Override
    public Sampler getSampler() {
        return sampler;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void bind() {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, id);
    }

    @Override
    public void dispose() {
        if (id == 0) return;

        disposable.dispose();
        id = 0;
    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }
}
