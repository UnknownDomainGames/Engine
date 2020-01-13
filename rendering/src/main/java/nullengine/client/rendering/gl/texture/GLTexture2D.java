package nullengine.client.rendering.gl.texture;

import nullengine.client.rendering.gl.util.GLCleaner;
import nullengine.client.rendering.image.BufferedImage;
import nullengine.client.rendering.image.ImageHelper;
import nullengine.client.rendering.image.ReadOnlyImage;
import nullengine.client.rendering.texture.Texture2D;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.GL_SRGB_ALPHA;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public final class GLTexture2D implements Texture2D, GLTexture {

    public static final GLTexture2D EMPTY = new GLTexture2D(0);

    private int id;
    private GLCleaner.Disposable disposable;

    private int level;
    private int internalFormat;
    private int format;
    private int type;

    private int width;
    private int height;

    private boolean mipmap;

    public static GLTexture2D of(ByteBuffer fileBuffer) throws IOException {
        ReadOnlyImage image = ImageHelper.instance().loadImage(fileBuffer);
        return of(image.getPixelBuffer(), image.getWidth(), image.getHeight());
    }

    public static GLTexture2D of(BufferedImage image) {
        return of(image.getPixelBuffer(), image.getWidth(), image.getHeight());
    }

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

    private GLTexture2D(int id) {
        this.id = id;
        this.disposable = GLCleaner.registerTexture(this, id);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getTarget() {
        return GL_TEXTURE_2D;
    }

    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void upload(ReadOnlyImage image) {
        bind();
        glTexImage2D(image.getPixelBuffer(), image.getWidth(), image.getHeight());
    }

    public void upload(ByteBuffer texture, int width, int height) {
        bind();
        glTexImage2D(texture, width, height);
    }

    public void glTexImage2D(ByteBuffer texture, int width, int height) {
        this.width = width;
        this.height = height;
//        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL_TEXTURE_2D, level, internalFormat, width, height, 0, format, type, texture);
        if (mipmap) glGenerateMipmap(GL_TEXTURE_2D);
//        glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
    }

    public void upload(int offsetX, int offsetY, BufferedImage buffer) {
        bind();
        glTexSubImage2D(offsetX, offsetY, buffer);
    }

    public void glTexSubImage2D(int offsetX, int offsetY, BufferedImage buffer) {
//        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexSubImage2D(GL_TEXTURE_2D, level, offsetX, offsetY, buffer.getWidth(), buffer.getHeight(), format, type, buffer.getPixelBuffer());
        if (mipmap) glGenerateMipmap(GL_TEXTURE_2D);
//        glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
    }

    @Override
    public void dispose() {
        if (id == 0) return;

        disposable.dispose();
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

    public int getLevel() {
        return level;
    }

    public int getInternalFormat() {
        return internalFormat;
    }

    public int getFormat() {
        return format;
    }

    public int getType() {
        return type;
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
        private boolean mipmap = false;
        private int level = 0;
        private int internalFormat = GL_SRGB_ALPHA;
        private int format = GL_RGBA;
        private int type = GL_UNSIGNED_BYTE;
        private final Map<Integer, Integer> parameterMap = new HashMap<>();

        private Builder() {
            magFilter(FilterMode.NEAREST);
            minFilter(FilterMode.NEAREST);
            wrapS(WrapMode.REPEAT);
            wrapT(WrapMode.REPEAT);
        }

        public Builder mipmap() {
            mipmap = true;
            return this;
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

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public Builder internalFormat(int internalFormat) {
            this.internalFormat = internalFormat;
            return this;
        }

        public Builder format(int format) {
            this.format = format;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public GLTexture2D build() {
            return build(null, 0, 0);
        }

        public GLTexture2D build(ReadOnlyImage image) {
            return build(image.getPixelBuffer(), image.getWidth(), image.getHeight());
        }

        public GLTexture2D build(int width, int height) {
            return build(null, width, height);
        }

        public GLTexture2D build(ByteBuffer pixelBuffer, int width, int height) {
            GLTexture2D glTexture2D = new GLTexture2D(glGenTextures());
            glTexture2D.level = level;
            glTexture2D.internalFormat = internalFormat;
            glTexture2D.format = format;
            glTexture2D.type = type;
            glTexture2D.mipmap = mipmap;
            glTexture2D.bind();

            parameterMap.forEach((key, value) -> glTexParameteri(GL_TEXTURE_2D, key, value));

            glTexture2D.glTexImage2D(pixelBuffer, width, height);
            return glTexture2D;
        }
    }
}
