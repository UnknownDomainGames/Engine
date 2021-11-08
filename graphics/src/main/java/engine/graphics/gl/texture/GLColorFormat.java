package engine.graphics.gl.texture;

import engine.graphics.texture.ColorFormat;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12C;
import org.lwjgl.opengl.GL14C;
import org.lwjgl.opengl.GL30C;

public enum GLColorFormat {

    RGBA8(ColorFormat.RGBA8, GL11C.GL_RGBA8, GL11C.GL_RGBA, GL11C.GL_UNSIGNED_BYTE),
    BGRA8(ColorFormat.BGRA8, GL11C.GL_RGBA8, GL12C.GL_BGRA, GL11C.GL_UNSIGNED_BYTE),
    RGB8(ColorFormat.RGB8, GL11C.GL_RGB8, GL11C.GL_RGB, GL11C.GL_UNSIGNED_BYTE),
    BGR8(ColorFormat.BGR8, GL11C.GL_RGB8, GL12C.GL_BGR, GL11C.GL_UNSIGNED_BYTE),
    RG8(ColorFormat.RG8, GL30C.GL_RG8, GL30C.GL_RG, GL11C.GL_UNSIGNED_BYTE),
    RED8(ColorFormat.RED8, GL30C.GL_R8, GL11C.GL_RED, GL11C.GL_UNSIGNED_BYTE),
    RGB10_A2(ColorFormat.RGB10_A2, GL11C.GL_RGB10_A2, GL11C.GL_RGBA, GL11C.GL_UNSIGNED_BYTE),
    R11F_G11F_B10F(ColorFormat.R11F_G11F_B10F, GL30C.GL_R11F_G11F_B10F, GL11C.GL_RGB, GL11C.GL_FLOAT),
    RGBA32F(ColorFormat.RGBA32F, GL30C.GL_RGBA32F, GL11C.GL_RGBA, GL11C.GL_FLOAT),
    RGBA16F(ColorFormat.RGBA16F, GL30C.GL_RGBA16F, GL11C.GL_RGBA, GL11C.GL_FLOAT),
    RGB32F(ColorFormat.RGB32F, GL30C.GL_RGB32F, GL11C.GL_RGB, GL11C.GL_FLOAT),
    RGB16F(ColorFormat.RGB16F, GL30C.GL_RGB16F, GL11C.GL_RGB, GL11C.GL_FLOAT),
    RED32F(ColorFormat.RED32F, GL30C.GL_R32F, GL11C.GL_RED, GL11C.GL_FLOAT),
    RED16F(ColorFormat.RED16F, GL30C.GL_R16F, GL11C.GL_RED, GL11C.GL_FLOAT),
    RGBA32UI(ColorFormat.RGBA32UI, GL30C.GL_RGBA32UI, GL11C.GL_RGBA, GL11C.GL_UNSIGNED_INT),
    RED32UI(ColorFormat.RED32UI, GL30C.GL_R32UI, GL30C.GL_RED_INTEGER, GL11C.GL_UNSIGNED_INT),
    RED16UI(ColorFormat.RED16UI, GL30C.GL_R16UI, GL30C.GL_RED_INTEGER, GL11C.GL_UNSIGNED_INT),
    RED8UI(ColorFormat.RED8UI, GL30C.GL_R8UI, GL30C.GL_RED_INTEGER, GL11C.GL_UNSIGNED_INT),
    DEPTH32(ColorFormat.DEPTH32, GL14C.GL_DEPTH_COMPONENT32, GL11C.GL_DEPTH_COMPONENT, GL11C.GL_FLOAT),
    DEPTH24(ColorFormat.DEPTH24, GL14C.GL_DEPTH_COMPONENT24, GL11C.GL_DEPTH_COMPONENT, GL11C.GL_FLOAT),
    DEPTH24_STENCIL8(ColorFormat.DEPTH24_STENCIL8, GL30C.GL_DEPTH24_STENCIL8, GL30C.GL_DEPTH_STENCIL, GL11C.GL_FLOAT);

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
