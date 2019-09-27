package nullengine.client.rendering.font;

import org.lwjgl.stb.STBTTBakedChar;

public class NativeTTFont {

    private final NativeTTFontInfo parent;
    private final Font font;

    private final int textureId;
    private final STBTTBakedChar.Buffer charBuffer;
    private final int bitmapWidth;
    private final int bitmapHeight;

    private final float scaleForPixelHeight;

    public NativeTTFont(NativeTTFontInfo parent, Font font, int textureId, STBTTBakedChar.Buffer charBuffer, int bitmapWidth, int bitmapHeight, float scaleForPixelHeight) {
        this.parent = parent;
        this.textureId = textureId;
        this.charBuffer = charBuffer;
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
        this.font = font;
        this.scaleForPixelHeight = scaleForPixelHeight;
    }

    public NativeTTFontInfo getInfo() {
        return parent;
    }

    public Font getFont() {
        return font;
    }

    public int getTextureId() {
        return textureId;
    }

    public STBTTBakedChar.Buffer getCharBuffer() {
        return charBuffer;
    }

    public int getBitmapWidth() {
        return bitmapWidth;
    }

    public int getBitmapHeight() {
        return bitmapHeight;
    }

    public float getScaleForPixelHeight() {
        return scaleForPixelHeight;
    }
}
