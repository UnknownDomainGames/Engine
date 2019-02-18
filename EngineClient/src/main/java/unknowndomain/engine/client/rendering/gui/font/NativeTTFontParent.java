package unknowndomain.engine.client.rendering.gui.font;

import org.lwjgl.stb.STBTTFontinfo;
import unknowndomain.engine.Platform;

import java.nio.ByteBuffer;

public class NativeTTFontParent {

    private final STBTTFontinfo fontinfo;
    private final ByteBuffer ttfData;

    private final String family;
    private final String style;

    private final double ascent;
    private final double descent;
    private final double lineGap;
    private final float contentScaleX;
    private final float contentScaleY;

    public NativeTTFontParent(STBTTFontinfo fontinfo, ByteBuffer ttfData, String family, String style, double ascent, double descent, double lineGap, float contentScaleX, float contentScaleY) {
        this.fontinfo = fontinfo;
        this.ttfData = ttfData;
        this.family = family;
        this.style = style;
        this.ascent = ascent;
        this.descent = descent;
        this.lineGap = lineGap;
        this.contentScaleX = contentScaleX;
        this.contentScaleY = contentScaleY;
    }

    public STBTTFontinfo getFontinfo() {
        return fontinfo;
    }

    public ByteBuffer getTtfData() {
        return ttfData;
    }

    public String getFamily() {
        return family;
    }

    public String getStyle() {
        return style;
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

    public float getContentScaleX() {
        return Platform.isClient() ? Platform.getEngineClient().getRenderContext().getWindow().getContentScaleX() : contentScaleX;
    }

    public float getContentScaleY() {
        return Platform.isClient() ? Platform.getEngineClient().getRenderContext().getWindow().getContentScaleY() : contentScaleY;
    }
}
