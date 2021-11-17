package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLHelper;
import engine.graphics.image.ReadOnlyImage;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Texture2D;
import engine.graphics.texture.WrapMode;
import engine.util.Color;
import org.joml.Vector2ic;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL42C;
import org.lwjgl.opengl.GL45C;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public final class GLTexture2D extends GLTexture implements Texture2D, GLFrameBuffer.Attachable {

    public static final GLTexture2D NONE = new GLTexture2D();

    private final int width;
    private final int height;

    private final boolean mipmap;

    public static GLTexture2D of(ByteBuffer pixelBuffer, int width, int height) {
        if (!pixelBuffer.isDirect()) {
            ByteBuffer direct = ByteBuffer.allocateDirect(pixelBuffer.capacity());
            direct.put(pixelBuffer);
            pixelBuffer = direct;
        }
        return builder().build(pixelBuffer, width, height);
    }

    public static Builder builder() {
        return new Builder();
    }

    private GLTexture2D(Builder builder, int width, int height) {
        super(GL11C.GL_TEXTURE_2D, builder.format);
        this.width = width;
        this.height = height;
        this.mipmap = builder.mipmap;

        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45C.glTextureParameteri(id, GL11C.GL_TEXTURE_MAG_FILTER, builder.magFilter);
            GL45C.glTextureParameteri(id, GL11C.GL_TEXTURE_MIN_FILTER, builder.minFilter);
            GL45C.glTextureParameteri(id, GL11C.GL_TEXTURE_WRAP_S, builder.wrapS);
            GL45C.glTextureParameteri(id, GL11C.GL_TEXTURE_WRAP_T, builder.wrapT);
            if (builder.borderColor != null) {
                GL45C.glTextureParameterfv(id, GL11C.GL_TEXTURE_BORDER_COLOR, builder.borderColor.toRGBAFloatArray());
            }
            GL45C.glTextureStorage2D(id, 1, format.internalFormat, width, height);
        } else {
            bind();
            GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, builder.magFilter);
            GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MIN_FILTER, builder.minFilter);
            GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_S, builder.wrapS);
            GL11C.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_WRAP_T, builder.wrapT);
            if (builder.borderColor != null) {
                GL11C.glTexParameterfv(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_BORDER_COLOR, builder.borderColor.toRGBAFloatArray());
            }
            if (GLHelper.isSupportARBTextureStorage()) {
                GL42C.glTexStorage2D(GL11C.GL_TEXTURE_2D, 1, format.internalFormat, width, height);
            } else {
                GL11C.nglTexImage2D(GL11C.GL_TEXTURE_2D, 0, format.internalFormat,
                        width, height, 0, format.format, format.type, MemoryUtil.NULL);
            }
        }
    }

    private GLTexture2D() {
        super(GL11C.GL_TEXTURE_2D);
        this.width = 0;
        this.height = 0;
        this.mipmap = false;
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
    public void upload(int level, ReadOnlyImage image) {
        upload(level, 0, 0, image);
    }

    @Override
    public void upload(int level, int offsetX, int offsetY, ReadOnlyImage image) {
        upload(level, offsetX, offsetY, image.getWidth(), image.getHeight(), image.getPixelBuffer());
    }

    @Override
    public void upload(int level, int width, int height, ByteBuffer pixels) {
        upload(level, 0, 0, width, height, pixels);
    }

    @Override
    public void upload(int level, int offsetX, int offsetY, int width, int height, ByteBuffer pixels) {
        if (pixels == null) {
            if (GLHelper.isSupportARBDirectStateAccess()) {
                GL45C.nglTextureSubImage2D(id, level, offsetX, offsetY, width, height,
                        format.format, format.type, MemoryUtil.NULL);
                if (mipmap) GL45C.glGenerateTextureMipmap(id);
            } else {
                bind();
                GL11C.nglTexSubImage2D(GL11C.GL_TEXTURE_2D, level, offsetX, offsetY, width, height,
                        format.format, format.type, MemoryUtil.NULL);
                if (mipmap) GL30C.glGenerateMipmap(GL11C.GL_TEXTURE_2D);
            }
        } else {
            if (GLHelper.isSupportARBDirectStateAccess()) {
                GL45C.glTextureSubImage2D(id, level, 0, 0, width, height,
                        format.format, format.type, pixels);
                if (mipmap) GL45C.glGenerateTextureMipmap(id);
            } else {
                bind();
                GL11C.glTexSubImage2D(GL11C.GL_TEXTURE_2D, level,
                        offsetX, offsetY, width, height,
                        format.format, format.type, pixels);
                if (mipmap) GL30C.glGenerateMipmap(GL11C.GL_TEXTURE_2D);
            }
        }
    }

    public static final class Builder implements Texture2D.Builder {
        private GLColorFormat format = GLColorFormat.RGBA8;
        private int magFilter = GL11C.GL_NEAREST;
        private int minFilter = GL11C.GL_NEAREST;
        private int wrapS = GL11C.GL_REPEAT;
        private int wrapT = GL11C.GL_REPEAT;
        private Color borderColor;
        private boolean mipmap;

        private Builder() {
        }

        @Override
        public Builder format(ColorFormat format) {
            this.format = GLColorFormat.valueOf(format);
            return this;
        }

        @Override
        public Builder magFilter(FilterMode mode) {
            this.magFilter = GLHelper.toGLFilterMode(mode);
            return this;
        }

        @Override
        public Builder minFilter(FilterMode mode) {
            this.minFilter = GLHelper.toGLFilterMode(mode);
            return this;
        }

        @Override
        public Builder wrapS(WrapMode mode) {
            this.wrapS = GLHelper.toGLWrapMode(mode);
            return this;
        }

        @Override
        public Builder wrapT(WrapMode mode) {
            this.wrapT = GLHelper.toGLWrapMode(mode);
            return this;
        }

        @Override
        public Builder borderColor(Color color) {
            borderColor = color;
            return this;
        }

        @Override
        public Builder generateMipmap() {
            mipmap = true;
            return this;
        }

        @Override
        public GLTexture2D build() {
            return build(null, 0, 0);
        }

        @Override
        public GLTexture2D build(Vector2ic size) {
            return build(null, size.x(), size.y());
        }

        @Override
        public GLTexture2D build(int width, int height) {
            return build(null, width, height);
        }

        @Override
        public GLTexture2D build(ReadOnlyImage image) {
            return build(image.getPixelBuffer(), image.getWidth(), image.getHeight());
        }

        @Override
        public GLTexture2D build(ByteBuffer pixelBuffer, int width, int height) {
            GLTexture2D texture = new GLTexture2D(this, width, height);
            if (pixelBuffer != null) {
                texture.upload(0, width, height, pixelBuffer);
            }
            return texture;
        }
    }
}
