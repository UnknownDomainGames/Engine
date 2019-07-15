package nullengine.client.rendering.gui.font;

import nullengine.client.gui.text.Font;
import org.lwjgl.stb.STBTTBakedChar;

public class NativeTTFont {

    private final NativeTTFontInfo parent;
    private final Font font;

    private final int textureId;
    private final STBTTBakedChar.Buffer charBuffer;
    private final int bitmapSize;

    public NativeTTFont(NativeTTFontInfo parent, float fontSize, int textureId, STBTTBakedChar.Buffer charBuffer, int bitmapSize) {
        this.parent = parent;
        this.textureId = textureId;
        this.charBuffer = charBuffer;
        this.bitmapSize = bitmapSize;
        this.font = new Font(parent.getFamily(), parent.getStyle(), fontSize);
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

    public int getBitmapSize() {
        return bitmapSize;
    }


}
