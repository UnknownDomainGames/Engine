package engine.graphics.gl.texture;

import engine.graphics.texture.ColorFormat;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.*;

public enum GLColorFormat {

    RGBA8(ColorFormat.RGBA8, GL_RGBA8, GL_RGBA, GL_UNSIGNED_BYTE),
    BGRA8(ColorFormat.BGRA8, GL_RGBA8, GL_BGRA, GL_UNSIGNED_BYTE),
    RGB8(ColorFormat.RGB8, GL_RGB8, GL_RGB, GL_UNSIGNED_BYTE),
    BGR8(ColorFormat.BGR8, GL_RGB8, GL_BGR, GL_UNSIGNED_BYTE),
    RG8(ColorFormat.RG8, GL_RG8, GL_RG, GL_UNSIGNED_BYTE),
    RED8(ColorFormat.RED8, GL_R8, GL_RED, GL_UNSIGNED_BYTE),
    RGB10_A2(ColorFormat.RGB10_A2, GL_RGB10_A2, GL_RGBA, GL_UNSIGNED_BYTE),
    R11F_G11F_B10F(ColorFormat.R11F_G11F_B10F, GL_R11F_G11F_B10F, GL_RGB, GL_FLOAT),
    RGBA32F(ColorFormat.RGBA32F, GL_RGBA32F, GL_RGBA, GL_FLOAT),
    RGBA16F(ColorFormat.RGBA16F, GL_RGBA16F, GL_RGBA, GL_FLOAT),
    RGB32F(ColorFormat.RGB32F, GL_RGB32F, GL_RGB, GL_FLOAT),
    RGB16F(ColorFormat.RGB16F, GL_RGB16F, GL_RGB, GL_FLOAT),
    RED32F(ColorFormat.RED32F, GL_R32F, GL_RED, GL_FLOAT),
    RED16F(ColorFormat.RED16F, GL_R16F, GL_RED, GL_FLOAT),
    RGBA32UI(ColorFormat.RGBA32UI, GL_RGBA32UI, GL_RGBA, GL_UNSIGNED_INT),
    RED32UI(ColorFormat.RED32UI, GL_R32UI, GL_RED_INTEGER, GL_UNSIGNED_INT),
    RED16UI(ColorFormat.RED16UI, GL_R16UI, GL_RED_INTEGER, GL_UNSIGNED_INT),
    RED8UI(ColorFormat.RED8UI, GL_R8UI, GL_RED_INTEGER, GL_UNSIGNED_INT),
    DEPTH32(ColorFormat.DEPTH32, GL_DEPTH_COMPONENT32, GL_DEPTH_COMPONENT, GL_FLOAT),
    DEPTH24(ColorFormat.DEPTH24, GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT, GL_FLOAT),
    DEPTH24_STENCIL8(ColorFormat.DEPTH24_STENCIL8, GL_DEPTH24_STENCIL8, GL_DEPTH_STENCIL, GL_FLOAT);

    private static final GLColorFormat[] VALUES = values();

    public final ColorFormat peer;
    public final int internalFormat;
    public final int format;
    public final int type;

    public static GLColorFormat valueOf(ColorFormat format) {
        return VALUES[format.ordinal()];
    }

    GLColorFormat(ColorFormat peer, int internalFormat, int format, int type) {
        this.peer = peer;
        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
    }
}
