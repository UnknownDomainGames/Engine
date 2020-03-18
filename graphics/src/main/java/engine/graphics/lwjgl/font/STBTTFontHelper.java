package engine.graphics.lwjgl.font;

import engine.graphics.util.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.*;

public final class STBTTFontHelper {

    private STBTTFontHelper() {
    }

    public static TTFontNameTable getFontNameTable(STBTTFontinfo fontInfo) {
        long address = fontInfo.address();
        long data = memGetLong(address + 8);
        int fontStart = memGetInt(address + 16);
        long nm = stbtt__find_table(data, fontStart, "name");
        if (nm == NULL) return new TTFontNameTable(List.of());

        List<TTFontNameEntry> entries = new ArrayList<>();
        int count = ttGetUShort(data + nm + 2);
        long stringOffset = nm + ttGetUShort(data + nm + 4);
        for (int i = 0; i < count; i++) {
            long loc = nm + 6 + 12 * i;
            int platform = ttGetUShort(data + loc);
            int encoding = ttGetUShort(data + loc + 2);
            if (encoding != STBFontManager.ENCODING_ID) {
                // No need to load others encoding.
                continue;
            }
            int language = ttGetUShort(data + loc + 4);
            int name = ttGetUShort(data + loc + 6);
            int length = ttGetUShort(data + loc + 8);
            long stringPtr = data + stringOffset + ttGetUShort(data + loc + 10);
            byte[] bytes = BufferUtils.toBytes(memByteBuffer(stringPtr, length).order(ByteOrder.BIG_ENDIAN));
            String string = new String(bytes, StandardCharsets.UTF_16BE);
            entries.add(new TTFontNameEntry(platform, encoding, language, name, string));
        }
        return new TTFontNameTable(List.copyOf(entries));
    }

    public static long stbtt__find_table(STBTTFontinfo fontInfo, String table) {
        long address = fontInfo.address();
        long data = memGetLong(address + 8);
        int fontStart = memGetInt(address + 16);
        return stbtt__find_table(data, fontStart, table);
    }

    private static long stbtt__find_table(long data, int fontStart, String table) {
        int num_tables = ttGetUShort(data + fontStart + 4);
        long tabledir = fontStart + 12;
        for (int i = 0; i < num_tables; i++) {
            long loc = tabledir + 16 * i;
            if (stbtt_tag(data + loc, table)) {
                return ttGetUInt(data + loc + 8);
            }
        }
        return NULL;
    }

    private static boolean stbtt_tag(long ptr, String table) {
        return table.equals(memUTF8Safe(ptr, 4));
    }

    private static int memGetUByte(long ptr) {
        return memGetByte(ptr) & 0xff;
    }

    private static int ttGetUShort(long ptr) {
        return (memGetUByte(ptr) << 8) + memGetUByte(ptr + 1);
    }

    private static long ttGetUInt(long ptr) {
        return (((long) memGetUByte(ptr)) << 24L) + (memGetUByte(ptr + 1) << 16L) + (memGetUByte(ptr + 2) << 8L) + memGetUByte(ptr + 3);
    }

//    struct stbtt_fontinfo
//    {
//        void           * userdata;
//        unsigned char  * data;              // pointer to .ttf file
//        int              fontstart;         // offset of start of font
//
//        int numGlyphs;                     // number of glyphs, needed for range checking
//
//        int loca,head,glyf,hhea,hmtx,kern,gpos; // table locations as offset from start of .ttf
//        int index_map;                     // a cmap mapping for our chosen character encoding
//        int indexToLocFormat;              // format needed to map from glyph index to glyph
//
//        stbtt__buf cff;                    // cff font data
//        stbtt__buf charstrings;            // the charstring index
//        stbtt__buf gsubrs;                 // global charstring subroutines index
//        stbtt__buf subrs;                  // private charstring subroutines index
//        stbtt__buf fontdicts;              // array of font dicts
//        stbtt__buf fdselect;               // map from glyph to fontdict
//    };

//    static stbtt_uint16 ttUSHORT(stbtt_uint8 *p) { return p[0]*256 + p[1]; }
//    static stbtt_int16 ttSHORT(stbtt_uint8 *p)   { return p[0]*256 + p[1]; }
//    static stbtt_uint32 ttULONG(stbtt_uint8 *p)  { return (p[0]<<24) + (p[1]<<16) + (p[2]<<8) + p[3]; }
//    static stbtt_int32 ttLONG(stbtt_uint8 *p)    { return (p[0]<<24) + (p[1]<<16) + (p[2]<<8) + p[3]; }

//    static stbtt_uint32 stbtt__find_table(stbtt_uint8 *data, stbtt_uint32 fontstart, const char *tag)
//    {
//        stbtt_int32 num_tables = ttUSHORT(data+fontstart+4);
//        stbtt_uint32 tabledir = fontstart + 12;
//        stbtt_int32 i;
//        for (i=0; i < num_tables; ++i) {
//            stbtt_uint32 loc = tabledir + 16*i;
//            if (stbtt_tag(data+loc+0, tag))
//                return ttULONG(data+loc+8);
//        }
//        return 0;
//    }

//    STBTT_DEF const char *stbtt_GetFontNameString(const stbtt_fontinfo *font, int *length, int platformID, int encodingID, int languageID, int nameID)
//    {
//        stbtt_int32 i,count,stringOffset;
//        stbtt_uint8 *fc = font->data;
//        stbtt_uint32 offset = font->fontstart;
//        stbtt_uint32 nm = stbtt__find_table(fc, offset, "name");
//        if (!nm) return NULL;
//
//        count = ttUSHORT(fc+nm+2);
//        stringOffset = nm + ttUSHORT(fc+nm+4);
//        for (i=0; i < count; ++i) {
//            stbtt_uint32 loc = nm + 6 + 12 * i;
//            if (platformID == ttUSHORT(fc+loc+0) && encodingID == ttUSHORT(fc+loc+2)
//                    && languageID == ttUSHORT(fc+loc+4) && nameID == ttUSHORT(fc+loc+6)) {
//         *length = ttUSHORT(fc+loc+8);
//                return (const char *) (fc+stringOffset+ttUSHORT(fc+loc+10));
//            }
//        }
//        return NULL;
//    }
}
