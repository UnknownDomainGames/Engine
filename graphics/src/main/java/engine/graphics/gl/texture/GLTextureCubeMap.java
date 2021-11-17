package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLHelper;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.TextureCubeMap;
import engine.graphics.texture.WrapMode;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

public final class GLTextureCubeMap extends GLTexture implements TextureCubeMap {
    private final int length;

    public static Builder builder() {
        return new Builder();
    }

    private GLTextureCubeMap(Builder builder, int length) {
        super(GL13C.GL_TEXTURE_CUBE_MAP, builder.format);
        this.length = length;
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45C.glTextureParameteri(id, GL11C.GL_TEXTURE_MAG_FILTER, builder.magFilter);
            GL45C.glTextureParameteri(id, GL11C.GL_TEXTURE_MIN_FILTER, builder.minFilter);
            GL45C.glTextureParameteri(id, GL11C.GL_TEXTURE_WRAP_S, builder.wrapS);
            GL45C.glTextureParameteri(id, GL11C.GL_TEXTURE_WRAP_T, builder.wrapT);
            GL45C.glTextureParameteri(id, GL12C.GL_TEXTURE_WRAP_R, builder.wrapR);
            GL45C.glTextureStorage2D(id, 1, format.internalFormat, length, length);
        } else {
            bind();
            GL11C.glTexParameteri(GL13C.GL_TEXTURE_CUBE_MAP, GL11C.GL_TEXTURE_MAG_FILTER, builder.magFilter);
            GL11C.glTexParameteri(GL13C.GL_TEXTURE_CUBE_MAP, GL11C.GL_TEXTURE_MIN_FILTER, builder.minFilter);
            GL11C.glTexParameteri(GL13C.GL_TEXTURE_CUBE_MAP, GL11C.GL_TEXTURE_WRAP_S, builder.wrapS);
            GL11C.glTexParameteri(GL13C.GL_TEXTURE_CUBE_MAP, GL11C.GL_TEXTURE_WRAP_T, builder.wrapT);
            GL11C.glTexParameteri(GL13C.GL_TEXTURE_CUBE_MAP, GL12C.GL_TEXTURE_WRAP_R, builder.wrapR);
            if (GLHelper.isSupportARBTextureStorage()) {
                GL42C.glTexStorage2D(GL13C.GL_TEXTURE_CUBE_MAP, 1, format.internalFormat, length, length);
            } else {
                for (int i = GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_X; i <= GL13C.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z; i++)
                    GL11C.nglTexImage2D(i, 0, format.internalFormat, length, length, 0,
                            format.format, format.type, MemoryUtil.NULL);
            }
        }
    }

    @Override
    public int getLength() {
        return length;
    }

    public static final class Builder implements TextureCubeMap.Builder {
        private GLColorFormat format = GLColorFormat.RGBA8;
        private int magFilter = GL11C.GL_LINEAR;
        private int minFilter = GL11C.GL_LINEAR;
        private int wrapS = GL11C.GL_REPEAT;
        private int wrapT = GL11C.GL_REPEAT;
        private int wrapR = GL11C.GL_REPEAT;

        private Builder() {
        }

        @Override
        public Builder format(ColorFormat format) {
            this.format = GLColorFormat.valueOf(format);
            return this;
        }

        @Override
        public Builder magFilter(FilterMode mode) {
            magFilter = GLHelper.toGLFilterMode(mode);
            return this;
        }

        @Override
        public Builder minFilter(FilterMode mode) {
            minFilter = GLHelper.toGLFilterMode(mode);
            return this;
        }

        @Override
        public Builder wrapS(WrapMode mode) {
            wrapS = GLHelper.toGLWrapMode(mode);
            return this;
        }

        @Override
        public Builder wrapT(WrapMode mode) {
            wrapT = GLHelper.toGLWrapMode(mode);
            return this;
        }

        @Override
        public Builder wrapR(WrapMode mode) {
            wrapR = GLHelper.toGLWrapMode(mode);
            return this;
        }

        @Override
        public TextureCubeMap build(int length) {
            return new GLTextureCubeMap(this, length);
        }
    }
}
