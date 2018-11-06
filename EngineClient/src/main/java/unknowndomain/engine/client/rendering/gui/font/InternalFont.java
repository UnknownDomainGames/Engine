package unknowndomain.engine.client.rendering.gui.font;

import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;

public class InternalFont {

    private STBTTFontinfo fontInfo;
    private double ascent;
    private double descent;
    private double lineGap;
    private double contentScaleX;
    private double contentScaleY;

    private int textureId;
    private STBTTBakedChar.Buffer charBuffer;

    public InternalFont(STBTTFontinfo fontInfo, double ascent, double descent, double lineGap, double contentScaleX, double contentScaleY, int textureId, STBTTBakedChar.Buffer charBuffer) {
        this.fontInfo = fontInfo;
        this.ascent = ascent;
        this.descent = descent;
        this.lineGap = lineGap;
        this.contentScaleX = contentScaleX;
        this.contentScaleY = contentScaleY;
        this.textureId = textureId;
        this.charBuffer = charBuffer;
    }

    public STBTTFontinfo getFontInfo() {
        return fontInfo;
    }

    public double getAscent() {
        return ascent;
    }

    public double getDescent() {
        return descent;
    }

    public double getLineGap() {
        return lineGap;
    }

    public double getContentScaleX() {
        return contentScaleX;
    }

    public double getContentScaleY() {
        return contentScaleY;
    }

    public int getTextureId() {
        return textureId;
    }

    public STBTTBakedChar.Buffer getCharBuffer() {
        return charBuffer;
    }
}
