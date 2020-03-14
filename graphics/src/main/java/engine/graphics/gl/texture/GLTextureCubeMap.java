package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLHelper;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.TextureCubeMap;
import engine.graphics.texture.WrapMode;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public final class GLTextureCubeMap extends GLTexture implements TextureCubeMap {
    private final int length;

    public static Builder builder() {
        return new Builder();
    }

    private GLTextureCubeMap(GLColorFormat format, int length) {
        super(GL13.GL_TEXTURE_CUBE_MAP, format);
        this.length = length;
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45C.glTextureStorage2D(id, 0, format.internalFormat, length, length);
        } else if (GLHelper.isSupportARBTextureStorage()) {
            bind();
            GL42C.glTexStorage2D(GL13.GL_TEXTURE_CUBE_MAP, 0, format.internalFormat, length, length);
        } else {
            bind();
            for (int i = GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X; i <= GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z; i++)
                GL11.glTexImage2D(i, 0, format.internalFormat, length, length, 0,
                        format.format, format.type, (ByteBuffer) null);
        }
    }

    @Override
    public int getLength() {
        return length;
    }

    public static final class Builder implements TextureCubeMap.Builder {

        private final Map<Integer, Integer> parameterMap = new HashMap<>();

        private GLColorFormat format = GLColorFormat.RGBA8;

        private Builder() {
            magFilter(FilterMode.NEAREST);
            minFilter(FilterMode.NEAREST);
            wrapS(WrapMode.CLAMP_TO_EDGE);
            wrapT(WrapMode.CLAMP_TO_EDGE);
            wrapR(WrapMode.CLAMP_TO_EDGE);
        }

        @Override
        public Builder format(ColorFormat format) {
            this.format = GLColorFormat.valueOf(format);
            return this;
        }

        @Override
        public Builder magFilter(FilterMode mode) {
            parameterMap.put(GL11.GL_TEXTURE_MAG_FILTER, toGLFilterMode(mode));
            return this;
        }

        @Override
        public Builder minFilter(FilterMode mode) {
            parameterMap.put(GL11.GL_TEXTURE_MIN_FILTER, toGLFilterMode(mode));
            return this;
        }

        @Override
        public Builder wrapS(WrapMode mode) {
            parameterMap.put(GL11.GL_TEXTURE_WRAP_S, toGLWrapMode(mode));
            return this;
        }

        @Override
        public Builder wrapT(WrapMode mode) {
            parameterMap.put(GL11.GL_TEXTURE_WRAP_T, toGLWrapMode(mode));
            return this;
        }

        @Override
        public Builder wrapR(WrapMode mode) {
            parameterMap.put(GL12.GL_TEXTURE_WRAP_R, toGLWrapMode(mode));
            return this;
        }

        @Override
        public TextureCubeMap build(int length) {
            GLTextureCubeMap texture = new GLTextureCubeMap(format, length);
            parameterMap.forEach(texture::setTextureParameteri);
            return texture;
        }
    }
}
