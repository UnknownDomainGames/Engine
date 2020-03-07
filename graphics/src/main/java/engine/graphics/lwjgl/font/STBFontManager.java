package engine.graphics.lwjgl.font;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import engine.graphics.font.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.BreakIterator;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public final class STBFontManager extends FontManager {

    public static final Logger LOGGER = LoggerFactory.getLogger("Font");

    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("engine.font.debug", "false"));

    //    private static final int[] PLATFORMs = {STBTT_PLATFORM_ID_MICROSOFT, STBTT_PLATFORM_ID_UNICODE, STBTT_PLATFORM_ID_MAC, STBTT_PLATFORM_ID_ISO};
    private static final int[] EIDs = {STBTT_MS_EID_UNICODE_BMP, STBTT_MS_EID_SHIFTJIS, STBTT_MS_EID_UNICODE_FULL, STBTT_MS_EID_SYMBOL};
    private static final int[] LANGs = {
            STBTT_MS_LANG_CHINESE,
            STBTT_MS_LANG_DUTCH,
            STBTT_MS_LANG_FRENCH,
            STBTT_MS_LANG_GERMAN,
            STBTT_MS_LANG_HEBREW,
            STBTT_MS_LANG_ITALIAN,
            STBTT_MS_LANG_JAPANESE,
            STBTT_MS_LANG_KOREAN,
            STBTT_MS_LANG_RUSSIAN,
            STBTT_MS_LANG_SPANISH,
            STBTT_MS_LANG_SWEDISH,
            STBTT_MS_LANG_ENGLISH};

    private final List<Font> availableFonts = new CopyOnWriteArrayList<>();
    private final Table<String, String, NativeTTFontInfo> loadedFontInfos = HashBasedTable.create();
    private final Map<Font, NativeTTFont> loadedNativeFonts = new HashMap<>();

    private Font defaultFont;

    private STBFontManager() {
        initLocalFonts();
        Font defaultFont = new Font("Arial", "Regular", 16);
        setDefaultFont(defaultFont);
    }

    private int getLanguageId(Locale locale) {
        switch (locale.getLanguage()) {
            case "zh":
                return STBTT_MS_LANG_CHINESE;
            case "nl":
                return STBTT_MS_LANG_DUTCH;
            case "fr":
                return STBTT_MS_LANG_FRENCH;
            case "de":
                return STBTT_MS_LANG_GERMAN;
            case "it":
                return STBTT_MS_LANG_ITALIAN;
            case "ja":
                return STBTT_MS_LANG_JAPANESE;
            case "ko":
                return STBTT_MS_LANG_KOREAN;
            case "ru":
                return STBTT_MS_LANG_RUSSIAN;
            case "es":
                return STBTT_MS_LANG_SPANISH;
            case "sv":
                return STBTT_MS_LANG_SWEDISH;
            default:
                return STBTT_MS_LANG_ENGLISH;
        }
    }

    private void initLocalFonts() {
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        List<NativeTTFontInfo> loadedFonts = Collections.synchronizedList(new ArrayList<>());
        List<Path> localTTFonts = findLocalTTFonts();
        if (localTTFonts.size() == 0) {
            LOGGER.warn("Not found any local font.");
            return;
        }

        for (var fontFile : localTTFonts) {
            tasks.add(CompletableFuture.runAsync(() -> {
                try {
                    Collections.addAll(loadedFonts, loadFontInfo(fontFile, true, false));
                } catch (Exception e) {
                    if (DEBUG) LOGGER.debug("Cannot load local font. Path: " + fontFile, e);
                }
            }));
        }
        CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new))
                .thenRun(() -> loadedFonts.forEach(info -> {
                    availableFonts.add(info.getFont());
                    loadedFontInfos.put(info.getFamily(), info.getStyle(), info);
                })).join();
    }

    private List<Path> findLocalTTFonts() {
        try {
            Predicate<Path> typefaceFilter = path -> path.getFileName().toString().endsWith(".ttf") || path.getFileName().toString().endsWith(".ttc");
            List<Path> fonts;
            if (SystemUtils.IS_OS_LINUX) {
                fonts = Files.walk(Path.of("/usr/share/fonts/WindowsFonts"))
                        .filter(typefaceFilter)
                        .collect(Collectors.toList());
            } else if (SystemUtils.IS_OS_WINDOWS) {
                fonts = Files.walk(Path.of("C:\\Windows\\Fonts"))
                        .filter(typefaceFilter)
                        .collect(Collectors.toList());
                var userFontDir = Path.of(SystemUtils.USER_HOME, "Appdata", "Local", "Microsoft", "Windows", "Fonts");
                if (userFontDir.toFile().exists()) {
                    var userFont = Files.walk(userFontDir).filter(typefaceFilter).collect(Collectors.toList());
                    fonts.addAll(userFont);
                }
            } else {
                fonts = List.of();
            }
            return fonts;
        } catch (IOException e) {
            LOGGER.warn("Cannot collect local font.", e);
            return List.of();
        }
    }

    @Override
    public Font getDefaultFont() {
        return defaultFont;
    }

    public void setDefaultFont(Font defaultFont) {
        if (!isAvailableFont(defaultFont))
            throw new IllegalStateException("Failed to set default font cause by " + defaultFont + " is unavailable");
        this.defaultFont = defaultFont;
    }

    @Override
    public List<Font> getAvailableFonts() {
        return Collections.unmodifiableList(availableFonts);
    }

    @Override
    public boolean isAvailableFont(Font font) {
        return loadedFontInfos.contains(font.getFamily(), font.getStyle());
    }

    @Override
    public Font loadFont(Path path) throws IOException {
        return loadFontInfo(path, true, true)[0].getFont();
    }

    @Override
    public Font loadFont(Path path, float size) throws IOException {
        NativeTTFontInfo nativeTTFontInfo = loadFontInfo(path, true, true)[0];
        return new Font(nativeTTFontInfo.getFont(), size);
    }

    @Override
    public Font loadFont(InputStream input) throws IOException {
        return new Font(loadFontInfo(input)[0].getFont(), 1);
    }

    @Override
    public Font loadFont(InputStream input, float size) throws IOException {
        return new Font(loadFontInfo(input)[0].getFont(), size);
    }

    @Override
    public List<String> wrapText(String text, float width, Font font) {
        if (computeTextWidth(text, font) <= width || width <= 0) {
            return Lists.newArrayList(text);
        } else {
            int l = 0, h = text.length() - 1;
            var breaker = BreakIterator.getLineInstance();
            breaker.setText(text);
            while (l < h) {
                int m = (l + h) / 2;
                if (computeTextWidth(text.substring(0, breaker.following(m)), font) <= width) {
                    l = m + 1;
                } else {
                    h = m - 1;
                }
            }
            return Stream.concat(Stream.of(text.substring(0, breaker.following(l))), wrapText(text.substring(breaker.following(l)), width, font).stream()).collect(Collectors.toList());
        }
    }

    /**
     * compute the text width
     *
     * @param text         the text
     * @param font         the font of the text
     * @param ceilingWidth the maximum width of the text. -1 if no limitation on it
     */
    @Override
    public float computeTextWidth(String text, Font font, float ceilingWidth) {
        if (text == null || text.length() == 0) {
            return 0;
        }
        List<String> trial;
        if (ceilingWidth != -1) {
            trial = text.lines().flatMap(str -> wrapText(str, ceilingWidth, font).stream()).collect(Collectors.toList());
        } else {
            trial = text.lines().collect(Collectors.toList());
        }
        if (trial.size() > 1) {
            var max = -1.0f;
            for (String s : trial) {
                max = Math.max(max, computeTextWidth(s, font));
            }
            return max;
        }

        NativeTTFont nativeFont = getNativeFont(font);
        STBTTFontinfo info = nativeFont.getInfo().getFontInfo();
        int width = 0;

        try (MemoryStack stack = stackPush()) {
            IntBuffer pCodePoint = stack.mallocInt(1);
            IntBuffer pAdvancedWidth = stack.mallocInt(1);
            IntBuffer pLeftSideBearing = stack.mallocInt(1);

            int i = 0;
            while (i < text.length()) {
                i += getCodePoint(text, i, pCodePoint);
                int cp = pCodePoint.get(0);

                stbtt_GetCodepointHMetrics(info, cp, pAdvancedWidth, pLeftSideBearing);
                if (i < text.length()) {
                    getCodePoint(text, i, pCodePoint);
                    pAdvancedWidth.put(0, pAdvancedWidth.get(0)
                            + stbtt_GetCodepointKernAdvance(info, cp, pCodePoint.get(0)));
                }
                width += pAdvancedWidth.get(0);
            }
        }

        return width * nativeFont.getScaleForPixelHeight();
    }

    @Override
    public float computeTextHeight(String text, Font font, float ceilingWidth, float leading) {
        if (text == null || text.length() == 0) {
            return 0;
        }
        List<String> trial;
        if (ceilingWidth != -1) {
            trial = text.lines().flatMap(str -> wrapText(str, ceilingWidth, font).stream()).collect(Collectors.toList());
        } else {
            trial = text.lines().collect(Collectors.toList());
        }
        if (trial.size() > 1) {
            var max = -1.0f;
            for (String s : trial) {
                max = Math.max(max, computeTextHeight(s, font, -1, leading));
            }
            return max * trial.size();
        }

        var nativeTTFont = getNativeFont(font);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer charPointBuffer = stack.mallocInt(1);
            FloatBuffer posX = stack.floats(0);
            FloatBuffer posY = stack.floats(0 + font.getSize());

            float maxY = (float) (nativeTTFont.getInfo().getAscent() - nativeTTFont.getInfo().getDescent()) * stbtt_ScaleForPixelHeight(nativeTTFont.getInfo().getFontInfo(), font.getSize());
            var plane = nativeTTFont.getPlaneTextures().get(0);
            for (int i = 0; i < text.length(); ) {
                i += getCodePoint(text, i, charPointBuffer);

                int charPoint = charPointBuffer.get(0);
                if (!isSupportedCharacter(nativeTTFont, charPoint)) {
                    continue;
                }
                if (!nativeTTFont.isBlockLoaded((char) charPoint)) {
                    plane.putBlock(Character.UnicodeBlock.of(charPoint));
                }
                var quad = plane.getQuad((char) charPoint);
                if (quad == null) continue;
                float diff = /*Math.abs(stbQuad.y0() - stbQuad.y1())*/ quad.getPos().w();
                if (maxY < diff) {
                    maxY = diff;
                }
            }
            return maxY * leading;
        }
    }

    @Override
    public TextMesh bakeTextMesh(CharSequence text, Font font) {
        TextMesh.CharQuad[] quads = new TextMesh.CharQuad[text.length()];

        NativeTTFont nativeTTFont = getNativeFont(font);
        STBTTFontinfo fontInfo = nativeTTFont.getInfo().getFontInfo();
        float scale = nativeTTFont.getScaleForPixelHeight();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer charPointBuffer = stack.mallocInt(1);
            FloatBuffer posX = stack.floats(0);

            var fontPlaneTexture = nativeTTFont.getPlaneTextures().get(0);
            for (int i = 0; i < text.length(); ) {
                i += getCodePoint(text, i, charPointBuffer);
                int charPoint = charPointBuffer.get(0);
                if (!isSupportedCharacter(nativeTTFont, charPoint)) {
                    continue;
                }
                if (!nativeTTFont.isBlockLoaded((char) charPoint)) {
                    fontPlaneTexture.putBlock(Character.UnicodeBlock.of(charPoint));
                }
                if (fontPlaneTexture.isWaitingForReloading()) {
                    fontPlaneTexture.bakeTexture(nativeTTFont.getFont(), nativeTTFont.getInfo());
                }
            }
            for (int i = 0, j = 0; i < text.length(); j++) {
                i += getCodePoint(text, i, charPointBuffer);

                int charPoint = charPointBuffer.get(0);

                if (!isSupportedCharacter(nativeTTFont, charPoint)) {
                    charPoint = '\u001A';
                    charPointBuffer.put(0, charPoint);
                }

                float centerX = posX.get(0);
                var quad = fontPlaneTexture.getQuad((char) charPoint);
                if (quad == null) continue;
                posX.put(0, posX.get(0) + quad.getXOffset());
                if (i < text.length()) {
                    getCodePoint(text, i, charPointBuffer);
                    posX.put(0, posX.get(0)
                            + stbtt_GetCodepointKernAdvance(fontInfo, charPoint, charPointBuffer.get(0)) * scale);
                }
                float x0 = (float) Math.floor(centerX + quad.getPos().x() + 0.5),
                        x1 = (float) Math.floor(centerX + quad.getPos().z() + 0.5),
                        y0 = (float) Math.floor(quad.getPos().y() + 0.5),
                        y1 = (float) Math.floor(quad.getPos().w() + 0.5);
                quads[j] = new TextMesh.CharQuad((char) charPoint, x0, y0, x1, y1, quad.getTexCoord());
            }
            return new TextMesh(text, font, fontPlaneTexture.getTexture(), quads);
        }
    }

    public NativeTTFont getNativeFont(Font font) {
        return loadedNativeFonts.computeIfAbsent(font, this::loadNativeFont);
    }

    private NativeTTFont loadNativeFont(Font font) {
        NativeTTFontInfo parent = loadedFontInfos.get(font.getFamily(), font.getStyle());
        if (parent == null) {
            throw new UnavailableFontException(font);
        }
        return loadNativeFont(parent, font);
    }

    private NativeTTFont loadNativeFont(NativeTTFontInfo info, Font font) {
        float scale = stbtt_ScaleForPixelHeight(info.getFontInfo(), font.getSize());
        var plane = new FontPlaneTexture();
        plane.putBlock(Character.UnicodeBlock.BASIC_LATIN);
        plane.bakeTexture(font, info);
        return new NativeTTFont(info, font, scale, plane);
    }

    private NativeTTFontInfo[] loadFontInfo(InputStream input) throws IOException {
        byte[] bytes = IOUtils.toByteArray(input);
        return loadFontInfo(null, ByteBuffer.allocateDirect(bytes.length).put(bytes).flip(), false, true);
    }

    private NativeTTFontInfo[] loadFontInfo(Path fontFile, boolean delayLoading, boolean enable) throws
            IOException {
        ByteBuffer fontData;
        try (var fileChannel = FileChannel.open(fontFile)) {
            fontData = ByteBuffer.allocateDirect((int) fileChannel.size());
            fileChannel.read(fontData);
            fontData.flip();
        }
        return loadFontInfo(fontFile, fontData, delayLoading, enable);
    }

    private NativeTTFontInfo[] loadFontInfo(Path fontFile, ByteBuffer fontData, boolean delayLoading,
                                            boolean enable) {
        var fontCount = stbtt_GetNumberOfFonts(fontData);
        if (fontCount == -1) {
            throw new IllegalArgumentException("Cannot determine the number of fonts in the font buffer.");
        }
        NativeTTFontInfo[] results = new NativeTTFontInfo[fontCount];

        for (int i = 0; i < fontCount; i++) {
            STBTTFontinfo fontInfo = STBTTFontinfo.create();
            if (!stbtt_InitFont(fontInfo, fontData)) {
                throw new IllegalStateException("Failed in initializing ttf font info");
            }

            FontDataFormat format = findDataFormat(fontInfo);
            if (format == null) {
                throw new FontLoadException("Cannot load font because of not found encoding id.");
            }

            String family = stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, format.encodingId, format.languageId, 1)
                    .order(ByteOrder.BIG_ENDIAN).asCharBuffer().toString();
            String style = stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, format.encodingId, format.languageId, 2)
                    .order(ByteOrder.BIG_ENDIAN).asCharBuffer().toString();

            try (MemoryStack stack = stackPush()) {
                IntBuffer pAscent = stack.mallocInt(1);
                IntBuffer pDescent = stack.mallocInt(1);
                IntBuffer pLineGap = stack.mallocInt(1);
                stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

                IntBuffer x0 = stack.mallocInt(1);
                IntBuffer y0 = stack.mallocInt(1);
                IntBuffer x1 = stack.mallocInt(1);
                IntBuffer y1 = stack.mallocInt(1);
                stbtt_GetFontBoundingBox(fontInfo, x0, y0, x1, y1);

                var builder = NativeTTFontInfo.builder()
                        .fontInfo(fontInfo)
                        .platformId(STBTT_PLATFORM_ID_MICROSOFT)
                        .encodingId(format.encodingId)
                        .languageId(format.languageId)
                        .family(family).style(style).offsetIndex(i)
                        .ascent(pAscent.get(0)).descent(pDescent.get(0)).lineGap(pLineGap.get(0))
                        .boundingBox(new int[]{x0.get(), y0.get(), x1.get(), y1.get()});
                if (delayLoading) {
                    builder.fontFile(fontFile);
                } else {
                    builder.fontData(fontData);
                }
                var result = builder.build();
                if (enable) {
                    loadedFontInfos.put(family, style, result);
                    availableFonts.add(result.getFont());
                }
                results[i] = result;
            }
        }
        return results;
    }

    private static final class FontDataFormat {
        private int encodingId;
        private int languageId;

        private FontDataFormat(int encodingId, int languageId) {
            this.encodingId = encodingId;
            this.languageId = languageId;
        }
    }

    private FontDataFormat findDataFormat(STBTTFontinfo fontInfo) {
        for (int eid : EIDs) {
            for (int lang : LANGs) {
                if (stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, eid, lang, 1) != null) {
                    return new FontDataFormat(eid, lang);
                }
            }
        }
        return null;
    }

    private int getBitmapSize(float size, int countOfChar) {
        return (int) Math.ceil((size + 8 * size / 16.0f) * Math.sqrt(countOfChar));
    }

    private float scale(float center, float offset, float factor) {
        return (offset - center) * factor + center;
    }

    private int getCodePoint(CharSequence text, int i, IntBuffer cpOut) {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < text.length()) {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2)) {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }

    private boolean isSupportedCharacter(NativeTTFont font, int character) {
        if (character == '\u001A' || character == '\uFFFD') return true;
        var counter = stbtt_FindGlyphIndex(font.getInfo().getFontInfo(), 0x1A);
        var ci = stbtt_FindGlyphIndex(font.getInfo().getFontInfo(), character);
        return counter != ci;
    }

    public static final class Factory implements FontManager.Factory {

        @Override
        public String getName() {
            return "stb";
        }

        @Override
        public FontManager create() {
            return new STBFontManager();
        }
    }
}
