package engine.graphics.gl.texture;

import engine.graphics.texture.Texture2D;
import engine.graphics.texture.TextureFormat;
import org.lwjgl.opengl.GL32;

import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

public final class GLTexture2DMultiSample extends GLTexture implements Texture2D, GLFrameBuffer.Attachable {

    private int samples;
    private boolean fixedSampleLocations;

    private int width;
    private int height;

    public static Builder builder() {
        return new Builder();
    }

    private GLTexture2DMultiSample() {
        super(glGenTextures());
    }

    @Override
    public int getTarget() {
        return GL_TEXTURE_2D_MULTISAMPLE;
    }

    public void glTexImage2DMultisample(int width, int height) {
        this.width = width;
        this.height = height;
//        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        GL32.glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, format.internalFormat, width, height, fixedSampleLocations);
//        glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
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
            GLTexture2DMultiSample texture = new GLTexture2DMultiSample();
            texture.format = format;
            texture.samples = samples;
            texture.fixedSampleLocations = fixedSampleLocations;
            texture.bind();
            texture.glTexImage2DMultisample(width, height);
            return texture;
        }
    }
}
