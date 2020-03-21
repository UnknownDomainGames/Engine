package engine.graphics.lwjgl.font;

import engine.graphics.font.Font;
import engine.graphics.util.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.stb.STBTruetype.stbtt_GetFontOffsetForIndex;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

public final class TTFontInfo {

    private Path fontFile;
    private ByteBuffer fontData;

    private STBTTFontinfo stbFontInfo;
    private int offsetIndex;

    private Font font;

    private TTFontNameTable nameTable;

    private double ascent;
    private double descent;
    private double lineGap;

    private int[] boundingBox;

    public Path getFontFile() {
        return fontFile;
    }

    public ByteBuffer getFontData() {
        checkFontData();
        return fontData;
    }

    public STBTTFontinfo getSTBFontInfo() {
        checkFontData();
        return stbFontInfo;
    }

    private void checkFontData() {
        if (fontData != null) return;
        try {
            fontData = BufferUtils.wrapAsByteBuffer(Files.readAllBytes(fontFile));
            stbFontInfo = STBTTFontinfo.create();
            if (!stbtt_InitFont(stbFontInfo, fontData, stbtt_GetFontOffsetForIndex(fontData, offsetIndex))) {
                throw new IllegalStateException("Failed in initializing ttf font info");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read font data", e);
        }
    }

    public int getOffsetIndex() {
        return offsetIndex;
    }

    public Font getFont() {
        return font;
    }

    public TTFontNameTable getNameTable() {
        return nameTable;
    }

    public String getFamily() {
        return font.getFamily();
    }

    public String getStyle() {
        return font.getStyle();
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

    public int[] getBoundingBox() {
        return boundingBox;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Path fontFile;
        private ByteBuffer fontData;
        private STBTTFontinfo stbFontInfo;
        private int offsetIndex;
        private TTFontNameTable nameTable;
        private String family;
        private String style;
        private double ascent;
        private double descent;
        private double lineGap;
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

        public Builder stbFontInfo(STBTTFontinfo fontInfo) {
            this.stbFontInfo = fontInfo;
            return this;
        }

        public Builder offsetIndex(int i) {
            this.offsetIndex = i;
            return this;
        }

        public Builder nameTable(TTFontNameTable nameTable) {
            this.nameTable = nameTable;
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

        public Builder boundingBox(int[] boundingBox) {
            this.boundingBox = boundingBox;
            return this;
        }

        public TTFontInfo build() {
            TTFontInfo fontInfo = new TTFontInfo();
            fontInfo.fontFile = this.fontFile;
            fontInfo.fontData = this.fontData;

            fontInfo.stbFontInfo = this.stbFontInfo;
            fontInfo.offsetIndex = this.offsetIndex;

            fontInfo.font = new Font(family, style, 1);

            fontInfo.nameTable = this.nameTable;

            fontInfo.descent = this.descent;
            fontInfo.lineGap = this.lineGap;
            fontInfo.ascent = this.ascent;
            fontInfo.boundingBox = this.boundingBox;
            return fontInfo;
        }
    }
}
