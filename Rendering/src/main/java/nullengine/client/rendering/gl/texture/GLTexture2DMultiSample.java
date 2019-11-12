package nullengine.client.rendering.gl.texture;

import nullengine.client.rendering.texture.Texture2D;
import org.lwjgl.opengl.GL40;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.GL_SRGB_ALPHA;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

public final class GLTexture2DMultiSample implements Texture2D {

    public static final GLTexture2DMultiSample EMPTY = new GLTexture2DMultiSample(0);

    private int id;

    private int internalFormat;
    private int sample;
    private boolean fixedSampleLocation;

    private int width;
    private int height;

    public static Builder builder() {
        return new Builder();
    }

    private GLTexture2DMultiSample(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, id);
    }

    public void glTexImage2DMultisample(int width, int height) {
        this.width = width;
        this.height = height;
//        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        GL40.glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, sample, internalFormat, width, height, fixedSampleLocation);
//        glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
    }

    @Override
    public void dispose() {
        if (id == 0) return;

        glDeleteTextures(id);
        id = 0;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public int getInternalFormat() {
        return internalFormat;
    }

    public int getSample() {
        return sample;
    }

    public boolean isFixedSampleLocation() {
        return fixedSampleLocation;
    }

    @Override
    public float getMinU() {
        return 0;
    }

    @Override
    public float getMinV() {
        return 0;
    }

    @Override
    public float getMaxU() {
        return 1;
    }

    @Override
    public float getMaxV() {
        return 1;
    }

    public static final class Builder {
        private int internalFormat = GL_SRGB_ALPHA;
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
            parameterMap.put(GL_TEXTURE_MAG_FILTER, mode.gl);
            return this;
        }

        public Builder minFilter(FilterMode mode) {
            parameterMap.put(GL_TEXTURE_MIN_FILTER, mode.gl);
            return this;
        }

        public Builder wrapS(WrapMode mode) {
            parameterMap.put(GL_TEXTURE_WRAP_S, mode.gl);
            return this;
        }

        public Builder wrapT(WrapMode mode) {
            parameterMap.put(GL_TEXTURE_WRAP_T, mode.gl);
            return this;
        }

        public Builder internalFormat(int internalFormat) {
            this.internalFormat = internalFormat;
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
            texture.internalFormat = internalFormat;
            texture.sample = sample;
            texture.fixedSampleLocation = fixedSampleLocation;
            texture.bind();
            parameterMap.forEach((key, value) -> glTexParameteri(GL_TEXTURE_2D, key, value));
            texture.glTexImage2DMultisample(width, height);
            return texture;
        }
    }
}
