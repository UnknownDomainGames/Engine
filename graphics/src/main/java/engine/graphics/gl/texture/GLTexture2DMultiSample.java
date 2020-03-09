package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLHelper;
import engine.graphics.texture.Texture2D;
import engine.graphics.texture.TextureFormat;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL45;

import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

public final class GLTexture2DMultiSample extends GLTexture implements Texture2D, GLFrameBuffer.Attachable {

    private final int width;
    private final int height;

    private final int samples;
    private final boolean fixedSampleLocations;

    public static Builder builder() {
        return new Builder();
    }

    private GLTexture2DMultiSample(GLTextureFormat format, int width, int height, int samples, boolean fixedSampleLocations) {
        super(GL_TEXTURE_2D_MULTISAMPLE, format);
        this.width = width;
        this.height = height;
        this.samples = samples;
        this.fixedSampleLocations = fixedSampleLocations;
        if (GLHelper.isOpenGL45()) {
            GL45.glTexStorage2DMultisample(id, this.samples, format.internalFormat, width, height, this.fixedSampleLocations);
        } else {
            GL32.glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, this.samples, format.internalFormat, width, height, this.fixedSampleLocations);
        }
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
    public boolean isMultiSample() {
        return true;
    }

    @Override
    public int getSamples() {
        return samples;
    }

    public boolean isFixedSampleLocations() {
        return fixedSampleLocations;
    }

    @Override
    public TextureFormat getFormat() {
        return null;
    }

    public static final class Builder {
        private GLTextureFormat format;
        private int samples = 0;
        private boolean fixedSampleLocations = false;

        private Builder() {
        }

        public Builder format(TextureFormat format) {
            this.format = GLTextureFormat.valueOf(format);
            return this;
        }

        public Builder samples(int samples) {
            this.samples = samples;
            return this;
        }

        public Builder fixedSampleLocations() {
            this.fixedSampleLocations = true;
            return this;
        }

        public GLTexture2DMultiSample build(int width, int height) {
            return new GLTexture2DMultiSample(format, width, height, samples, fixedSampleLocations);
        }
    }
}
