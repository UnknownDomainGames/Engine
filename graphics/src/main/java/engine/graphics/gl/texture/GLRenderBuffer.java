package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.TextureFormat;
import engine.graphics.util.Cleaner;
import org.lwjgl.opengl.GL30;

import javax.annotation.Nullable;

public final class GLRenderBuffer implements GLFrameBuffer.Attachable {

    private int id;
    private Cleaner.Disposable disposable;

    private GLTextureFormat format;
    private Sampler sampler;
    private int width;
    private int height;

    public static Builder builder() {
        return new Builder();
    }

    public GLRenderBuffer(TextureFormat format, int width, int height) {
        this(format, null, width, height);
    }

    public GLRenderBuffer(TextureFormat format, Sampler sampler, int width, int height) {
        id = GL30.glGenRenderbuffers();
        disposable = GLCleaner.registerRenderBuffer(this, id);
        this.format = GLTextureFormat.valueOf(format);
        this.sampler = sampler;
        this.width = width;
        this.height = height;
        if (sampler == null) {
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, this.format.internalFormat, width, height);
        } else {
            GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, sampler.getId(), this.format.internalFormat, width, height);
        }
    }

    public int getId() {
        return id;
    }

    public TextureFormat getFormat() {
        return format.peer;
    }

    public boolean isMultiSample() {
        return sampler != null;
    }

    @Nullable
    public Sampler getSampler() {
        return sampler;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void bind() {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, id);
    }

    public void dispose() {
        if (id == 0) return;

        disposable.dispose();
        id = 0;
    }

    public boolean isDisposed() {
        return id == 0;
    }

    public static final class Builder {

        private TextureFormat format;
        private Sampler sampler;

        private Builder() {
        }

        public Builder format(TextureFormat format) {
            this.format = format;
            return this;
        }

        public Builder sampler(Sampler sampler) {
            this.sampler = sampler;
            return this;
        }

        public GLRenderBuffer build(int width, int height) {
            return new GLRenderBuffer(format, sampler, width, height);
        }
    }
}
