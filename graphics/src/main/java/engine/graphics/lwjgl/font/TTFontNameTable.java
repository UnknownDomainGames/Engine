package engine.graphics.lwjgl.font;

import java.util.List;

public final class TTFontNameTable {
    public static final int COPYRIGHT = 0;
    public static final int FAMILY = 1;
    public static final int STYLE = 2;
    public static final int UNIQUE = 3;
    public static final int FULL = 4;
    public static final int VERSION = 5;
    public static final int POSTSCRIPT = 6;
    public static final int TRADEMARK = 7;
    public static final int MANUFACTURER = 8;
    public static final int DESIGNER = 9;
    public static final int DESCRIPTION = 10;
    public static final int VENDOR_URL = 11;
    public static final int DESIGNER_URL = 12;
    public static final int LICENSE = 13;
    public static final int LICENSE_URL = 14;
    public static final int SAMPLE_TEXT = 19;

    private final List<TTFontNameEntry> entries;

    public TTFontNameTable(List<TTFontNameEntry> entries) {
        this.entries = entries;
    }

    public List<TTFontNameEntry> getEntries() {
        return entries;
    }

    public String getFontNameString(int platformId, int encodingId, int languageId, int nameId) {
        for (TTFontNameEntry entry : entries) {
            if (entry.match(platformId, encodingId, languageId, nameId)) {
                return entry.getString();
            }
        }
        return null;
    }
}
