package engine.graphics.gl.texture;

import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Texture2D;
import engine.graphics.texture.TextureFormat;
import engine.graphics.texture.WrapMode;
import org.lwjgl.opengl.GL40;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

public final class GLTexture2DMultiSample extends GLTexture implements Texture2D {

    public static final GLTexture2DMultiSample EMPTY = new GLTexture2DMultiSample(0);

    private int sample;
    private boolean fixedSampleLocation;

    private int width;
    private int height;

    public static Builder builder() {
        return new Builder();
    }

    private GLTexture2DMultiSample(int id) {
        super(id);
    }

    @Override
    public int getTarget() {
        return GL_TEXTURE_2D_MULTISAMPLE;
    }

    public void glTexImage2DMultisample(int width, int height) {
        this.width = width;
        this.height = height;
//        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        GL40.glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, sample, format.internalFormat, width, height, fixedSampleLocation);
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

    public int getSample() {
        return sample;
    }

    public boolean isFixedSampleLocation() {
        return fixedSampleLocation;
    }

    @Override
    public TextureFormat getFormat() {
        return null;
    }

    public static final class Builder {
        private GLTextureFormat format;
        private int sample = 1;
        private boolean fixedSampleLocation = false;
        private final Map<Integer, Integer> parameterMap = new HashMap<>();

        private Builder() {
            magFilter(FilterMode.NEAREST);
            minFilter(FilterMode.NEAREST);
            wrapS(WrapMode.REPEAT);
            wrapT(WrapMode.REPEAT);
        }

        public Builder magFilter(FilterMode mode) {
            parameterMap.put(GL_TEXTURE_MAG_FILTER, toGLFilterMode(mode));
            return this;
        }

        public Builder minFilter(FilterMode mode) {
            parameterMap.put(GL_TEXTURE_MIN_FILTER, toGLFilterMode(mode));
            return this;
        }

        public Builder wrapS(WrapMode mode) {
            parameterMap.put(GL_TEXTURE_WRAP_S, toGLWrapMode(mode));
            return this;
        }

        public Builder wrapT(WrapMode mode) {
            parameterMap.put(GL_TEXTURE_WRAP_T, toGLWrapMode(mode));
            return this;
        }

        public Builder format(TextureFormat format) {
            this.format = GLTextureFormat.valueOf(format);
            return this;
        }

        public Builder sample(int sample) {
            this.sample = sample;
            return this;
        }

        public Builder fixedSampleLocation() {
            this.fixedSampleLocation = true;
            return this;
        }

        public GLTexture2DMultiSample build(int width, int height) {
            GLTexture2DMultiSample texture = new GLTexture2DMultiSample(glGenTextures());
            texture.format = format;
            texture.sample = sample;
            texture.fixedSampleLocation = fixedSampleLocation;
            texture.bind();
            parameterMap.forEach((key, value) -> glTexParameteri(GL_TEXTURE_2D, key, value));
            texture.glTexImage2DMultisample(width, height);
            return texture;
        }
    }
}
