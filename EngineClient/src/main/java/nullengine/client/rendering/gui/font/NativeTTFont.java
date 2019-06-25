package nullengine.client.rendering.gui.font;

import nullengine.client.gui.text.Font;
import org.lwjgl.stb.STBTTBakedChar;

public class NativeTTFont {

    private final NativeTTFontParent parent;

    private final float fontSize;
    private final int textureId;
    private final STBTTBakedChar.Buffer charBuffer;
    private final int bitmapSize;
    private final Font font;

    public NativeTTFont(NativeTTFontParent parent, float fontSize, int textureId, STBTTBakedChar.Buffer charBuffer, int bitmapSize) {
        this.parent = parent;
        this.fontSize = fontSize;
        this.textureId = textureId;
        this.charBuffer = charBuffer;
        this.bitmapSize = bitmapSize;
        this.font = new Font(parent.getFamily(), parent.getStyle(), fontSize);
    }

    public NativeTTFontParent getParent() {
        return parent;
    }

    public float getFontSize() {
        return fontSize;
    }

    public int getTextureId() {
        return textureId;
    }

    public STBTTBakedChar.Buffer getCharBuffer() {
        return charBuffer;
    }

    public int getBitmapSize() {
        return bitmapSize;
    }

    public Font getFont() {
        return font;
    }
}
