package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.texture.TextureFormat;
import engine.graphics.util.Cleaner;
import org.lwjgl.opengl.GL30;

public final class GLRenderBuffer implements GLFrameBuffer.Attachable {

    private int id;
    private Cleaner.Disposable disposable;

    private GLTextureFormat format;
    private int samples;
    private int width;
    private int height;

    public static Builder builder() {
        return new Builder();
    }

    public GLRenderBuffer(TextureFormat format, int width, int height) {
        this(format, 0, width, height);
    }

    public GLRenderBuffer(TextureFormat format, int samples, int width, int height) {
        id = GL30.glGenRenderbuffers();
        disposable = GLCleaner.registerRenderBuffer(this, id);
        this.format = GLTextureFormat.valueOf(format);
        this.samples = samples;
        this.width = width;
        this.height = height;
        if (samples == 0) {
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, this.format.internalFormat, width, height);
        } else {
            GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, this.format.internalFormat, width, height);
        }
    }

    public int getId() {
        return id;
    }

    public TextureFormat getFormat() {
        return format.peer;
    }

    public boolean isMultiSample() {
        return samples != 0;
    }

    public int getSamples() {
        return samples;
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
        private int samples;

        private Builder() {
        }

        public Builder format(TextureFormat format) {
            this.format = format;
            return this;
        }

        public Builder samples(int samples) {
            this.samples = samples;
            return this;
        }

        public GLRenderBuffer build(int width, int height) {
            return new GLRenderBuffer(format, samples, width, height);
        }
    }
}
