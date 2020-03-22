package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLHelper;
import engine.graphics.image.ReadOnlyImage;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Texture2D;
import engine.graphics.texture.WrapMode;
import engine.util.Color;
import org.joml.Vector2ic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public final class GLTexture2D extends GLTexture implements Texture2D, GLFrameBuffer.Attachable {

    public static final GLTexture2D EMPTY = new GLTexture2D();

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

    private GLTexture2D(GLColorFormat format, int width, int height, boolean mipmap) {
        super(GL11.GL_TEXTURE_2D, format);
        this.width = width;
        this.height = height;
        this.mipmap = mipmap;
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glTextureStorage2D(id, 1, format.internalFormat, width, height);
        } else {
            bind();
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format.internalFormat,
                    width, height, 0, format.format, format.type, (ByteBuffer) null);
        }
    }

    private GLTexture2D() {
        super(GL11.GL_TEXTURE_2D);
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
        if (pixels == null) return;
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glTextureSubImage2D(id, level, 0, 0, width, height,
                    format.format, format.type, pixels);
            if (mipmap) GL45.glGenerateTextureMipmap(id);
        } else {
            bind();
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level,
                    offsetX, offsetY, width, height,
                    format.format, format.type, pixels);
            if (mipmap) GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        }
    }

    public static final class Builder implements Texture2D.Builder {

        private final Map<Integer, Integer> parameterMap = new HashMap<>();

        private GLColorFormat format = GLColorFormat.RGBA8;

        private boolean mipmap = false;

        private Color borderColor;

        private Builder() {
            magFilter(FilterMode.NEAREST);
            minFilter(FilterMode.NEAREST);
//            Following values has been set to default:
//            wrapS(WrapMode.REPEAT);
//            wrapT(WrapMode.REPEAT);
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
        public Builder generateMipmap() {
            mipmap = true;
            return this;
        }

        @Override
        public Builder borderColor(Color color) {
            borderColor = color;
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
            GLTexture2D texture = new GLTexture2D(format, width, height, mipmap);
            parameterMap.forEach(texture::setTextureParameteri);
            if (borderColor != null) {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    texture.setTextureParameterfv(GL11.GL_TEXTURE_BORDER_COLOR,
                            borderColor.get(stack.mallocFloat(4)));
                }
            }
            texture.upload(0, width, height, pixelBuffer);
            return texture;
        }
    }
}
