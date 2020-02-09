package engine.graphics.gl.texture;

import engine.graphics.texture.TextureFormat;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.*;

public enum GLTextureFormat {

    RGBA8(TextureFormat.RGBA8, GL_RGBA8, GL_RGBA, GL_UNSIGNED_BYTE),
    RGB8(TextureFormat.RGB8, GL_RGB8, GL_RGB, GL_UNSIGNED_BYTE),
    RG8(TextureFormat.RG8, GL_RG, GL_RG, GL_UNSIGNED_BYTE),
    RED8(TextureFormat.RED8, GL_RED, GL_RED, GL_UNSIGNED_BYTE),
    RGB10_A2(TextureFormat.RGB10_A2, GL_RGB10_A2, GL_RGBA, GL_UNSIGNED_BYTE),
    R11F_G11F_B10F(TextureFormat.R11F_G11F_B10F, GL_R11F_G11F_B10F, GL_RGB, GL_FLOAT),
    RGBA32F(TextureFormat.RGBA32F, GL_RGBA32F, GL_RGBA, GL_FLOAT),
    RGBA16F(TextureFormat.RGBA16F, GL_RGBA16F, GL_RGBA, GL_FLOAT),
    RGB32F(TextureFormat.RGB32F, GL_RGB32F, GL_RGB, GL_FLOAT),
    RGB16F(TextureFormat.RGB16F, GL_RGB16F, GL_RGB, GL_FLOAT),
    RED32F(TextureFormat.RED32F, GL_R32F, GL_RED, GL_FLOAT),
    RED16F(TextureFormat.RED16F, GL_R16F, GL_RED, GL_FLOAT),
    RED16UI(TextureFormat.RED16UI, GL_R16UI, GL_RED_INTEGER, GL_INT),
    RED8UI(TextureFormat.RED8UI, GL_R8UI, GL_RED_INTEGER, GL_INT),
    DEPTH32(TextureFormat.DEPTH32, GL_DEPTH_COMPONENT32, GL_DEPTH_COMPONENT, GL_FLOAT),
    DEPTH24(TextureFormat.DEPTH24, GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT, GL_FLOAT),
    DEPTH24_STENCIL8(TextureFormat.DEPTH24_STENCIL8, GL_DEPTH24_STENCIL8, GL_DEPTH_STENCIL, GL_FLOAT);

    public final TextureFormat peer;
    public final int internalFormat;
    public final int format;
    public final int type;

    public static GLTextureFormat valueOf(TextureFormat format) {
        return values()[format.ordinal()];
    }

    GLTextureFormat(TextureFormat peer, int internalFormat, int format, int type) {
        this.peer = peer;
        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
    }
}
