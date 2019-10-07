package nullengine.client.rendering.font;

import nullengine.Platform;
import org.lwjgl.stb.STBTTFontinfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.stb.STBTruetype.*;

public class NativeTTFontInfo {

    private Path fontFile;
    private ByteBuffer fontData;

    private STBTTFontinfo fontInfo;
    private Font font;

    private int platformId;
    private int encodingId;
    private int languageId;

    private String family;
    private String style;
    private int offsetIndex;

    private double ascent;
    private double descent;
    private double lineGap;
    private float contentScaleX;
    private float contentScaleY;

    private int[] boundingBox;

    public STBTTFontinfo getFontInfo() {
        return fontInfo;
    }

    public ByteBuffer getFontData() {
        if (fontData == null) {
            try {
                byte[] bytes = Files.readAllBytes(fontFile);
                fontData = ByteBuffer.allocateDirect(bytes.length).put(bytes).flip();
                fontInfo = STBTTFontinfo.create();
                if (!stbtt_InitFont(fontInfo, fontData, stbtt_GetFontOffsetForIndex(fontData, offsetIndex))) {
                    throw new IllegalStateException("Failed in initializing ttf font info");
                }
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read font data", e);
            }
        }
        return fontData;
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
        return Platform.isClient() ? Platform.getEngineClient().getRenderManager().getWindow().getContentScaleX() : contentScaleX;
    }

    public float getContentScaleY() {
        return Platform.isClient() ? Platform.getEngineClient().getRenderManager().getWindow().getContentScaleY() : contentScaleY;
    }

    public int[] getBoundingBox() {
        return boundingBox;
    }

    public Font getFont() {
        return font;
    }

    public String getFontName(int nameID) {
        ByteBuffer buffer = stbtt_GetFontNameString(fontInfo, platformId, encodingId, languageId, nameID);
        if (buffer == null) {
            return null;
        }
        return buffer.order(ByteOrder.BIG_ENDIAN).asCharBuffer().toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Path fontFile;
        private ByteBuffer fontData;
        private STBTTFontinfo fontInfo;
        private Font font;
        private int platformId;
        private int encodingId;
        private int languageId;
        private String family;
        private String style;
        private int offsetIndex;
        private double ascent;
        private double descent;
        private double lineGap;
        private float contentScaleX;
        private float contentScaleY;
        private int[] boundingBox;

        private Builder() {
        }

        public Builder fontFile(Path fontFile) {
            this.fontFile = fontFile;
            return this;
        }

        public Builder fontData(ByteBuffer fontData) {
            this.fontData = fontData;
            return this;
        }

        public Builder fontInfo(STBTTFontinfo fontInfo) {
            this.fontInfo = fontInfo;
            return this;
        }

        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        public Builder platformId(int platformId) {
            this.platformId = platformId;
            return this;
        }

        public Builder encodingId(int encodingId) {
            this.encodingId = encodingId;
            return this;
        }

        public Builder languageId(int languageId) {
            this.languageId = languageId;
            return this;
        }

        public Builder family(String family) {
            this.family = family;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public Builder offsetIndex(int i) {
            this.offsetIndex = i;
            return this;
        }

        public Builder ascent(double ascent) {
            this.ascent = ascent;
            return this;
        }

        public Builder descent(double descent) {
            this.descent = descent;
            return this;
        }

        public Builder lineGap(double lineGap) {
            this.lineGap = lineGap;
            return this;
        }

        public Builder contentScaleX(float contentScaleX) {
            this.contentScaleX = contentScaleX;
            return this;
        }

        public Builder contentScaleY(float contentScaleY) {
            this.contentScaleY = contentScaleY;
            return this;
        }

        public Builder boundingBox(int[] boundingBox) {
            this.boundingBox = boundingBox;
            return this;
        }

        public NativeTTFontInfo build() {
            NativeTTFontInfo nativeTTFontInfo = new NativeTTFontInfo();
            nativeTTFontInfo.contentScaleX = this.contentScaleX;
            nativeTTFontInfo.fontData = this.fontData;
            nativeTTFontInfo.contentScaleY = this.contentScaleY;
            nativeTTFontInfo.fontInfo = this.fontInfo;
            nativeTTFontInfo.languageId = this.languageId;
            nativeTTFontInfo.descent = this.descent;
            nativeTTFontInfo.encodingId = this.encodingId;
            nativeTTFontInfo.lineGap = this.lineGap;
            nativeTTFontInfo.family = this.family;
            nativeTTFontInfo.fontFile = this.fontFile;
            nativeTTFontInfo.font = this.font;
            nativeTTFontInfo.style = this.style;
            nativeTTFontInfo.offsetIndex = this.offsetIndex;
            nativeTTFontInfo.ascent = this.ascent;
            nativeTTFontInfo.platformId = this.platformId;
            nativeTTFontInfo.boundingBox = this.boundingBox;
            return nativeTTFontInfo;
        }
    }
}
