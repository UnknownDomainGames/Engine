package engine.graphics.lwjgl.font;

import java.util.List;
import java.util.stream.Collectors;

public final class TTFontNameTable {
    private final List<TTFontNameEntry> entries;

    public TTFontNameTable(List<TTFontNameEntry> entries) {
        this.entries = entries;
    }

    public List<TTFontNameEntry> getEntries() {
        return entries;
    }

    public List<TTFontNameEntry> getEntriesWithName(int nameId) {
        return entries.stream().filter(entry -> entry.getName() == nameId).collect(Collectors.toList());
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
