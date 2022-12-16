package engine.graphics.lwjgl.font;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.common.collect.Table;
import engine.graphics.font.*;
import engine.graphics.util.BufferUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.dyncall.DynCall;
import org.lwjgl.system.windows.User32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.BreakIterator;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public final class STBTTFontManager extends FontManager {

    public static final Logger LOGGER = LoggerFactory.getLogger("Font");

    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("engine.font.debug", "false"));

    private final List<Font> availableFonts = new ArrayList<>();
    private final Table<String, String, TTFontInfo> loadedFontInfos = HashBasedTable.create();
    private final Map<Font, TTFont> loadedNativeFonts = new HashMap<>();

    private Font defaultFont;

    private STBTTFontManager() {
        initLocalFonts();
        setDefaultFont(null);
    }

    private void initLocalFonts() {
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        List<TTFontInfo> loadedFonts = Collections.synchronizedList(new ArrayList<>());
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
                .thenRun(() -> loadedFonts.forEach(this::setFontAvailable))
                .join();
    }

    private List<Path> findLocalTTFonts() {
        try {
            Predicate<Path> typefaceFilter = path -> {
                String lowerCaseFileName = path.getFileName().toString().toLowerCase();
                return lowerCaseFileName.endsWith(".ttf") || lowerCaseFileName.endsWith(".ttc");
            };
            List<Path> fonts;
            if (SystemUtils.IS_OS_LINUX) {
                fonts = Streams.concat(Files.walk(Path.of("/usr/share/fonts/")), Files.walk(Path.of("/usr/local/share/fonts/")))
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

    private Font getSystemDefaultFont() {
        if (!SystemUtils.IS_OS_WINDOWS) {
            return new Font("Arial", Font.REGULAR, 16);
        }

        long systemParametersInfoW = User32.getLibrary().getFunctionAddress("SystemParametersInfoW");
        if (systemParametersInfoW == MemoryUtil.NULL) {
            return new Font("Arial", Font.REGULAR, 16);
        }

        long callVM = MemoryUtil.NULL;
        try (MemoryStack stack = stackPush()) {
            int sizeLOGFONT = Integer.BYTES * 5 + Byte.BYTES * 8 + Character.BYTES * 32;
            ByteBuffer structLOGFONT = stack.malloc(sizeLOGFONT);
            callVM = DynCall.dcNewCallVM(Integer.BYTES * 3 + Long.BYTES);
            DynCall.dcArgInt(callVM, 0x001F); // SPI_GETICONTITLELOGFONT
            DynCall.dcArgInt(callVM, sizeLOGFONT);
            DynCall.dcArgPointer(callVM, MemoryUtil.memAddress(structLOGFONT));
            DynCall.dcArgInt(callVM, 0);
            DynCall.dcCallBool(callVM, systemParametersInfoW);
            String defaultFontFamily = MemoryUtil.memUTF16Safe(structLOGFONT.position(Integer.BYTES * 5 + Byte.BYTES * 8)).trim();
            return new Font(defaultFontFamily, Font.REGULAR, 16);
        } finally {
            if (callVM != MemoryUtil.NULL) {
                DynCall.dcFree(callVM);
            }
        }
    }

    @Override
    public Font getDefaultFont() {
        return defaultFont;
    }

    @Override
    public void setDefaultFont(@Nullable Font defaultFont) {
        if (defaultFont == null) {
            defaultFont = getSystemDefaultFont();
        }
        if (!isAvailableFont(defaultFont)) {
            var fallbackFont = availableFonts.get(0).withSize(defaultFont.getSize());
            LOGGER.error("Failed to set default font, {} is unavailable, fallback to {}", defaultFont, fallbackFont);
            defaultFont = fallbackFont;
        }
        this.defaultFont = defaultFont;
    }

    @Override
    public List<Font> getAvailableFonts() {
        return Collections.unmodifiableList(availableFonts);
    }

    @Override
    public boolean isAvailableFont(Font font) {
        return loadedFontInfos.contains(font.getFamily().toLowerCase(), font.getStyle().toLowerCase());
    }

    @Override
    public Font loadFont(Path path) throws IOException {
        return loadFontInfo(path, true, true)[0].getFont();
    }

    @Override
    public Font loadFont(Path path, float size) throws IOException {
        TTFontInfo TTFontInfo = loadFontInfo(path, true, true)[0];
        return new Font(TTFontInfo.getFont(), size);
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
    public String getFontFamily(Font font, Locale locale) {
        String family = findFirstFontNameString(getFontInfo(font).getNameTable(), TTFontNameTable.FAMILY);
        return family != null ? family : font.getFamily();
    }

    @Override
    public String getFontName(Font font, Locale locale) {
        String fullName = findFirstFontNameString(getFontInfo(font).getNameTable(), TTFontNameTable.FULL);
        return fullName != null ? fullName : font.getName();
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

        TTFont nativeFont = getNativeFont(font);
        STBTTFontinfo info = nativeFont.getFontInfo().getSTBFontInfo();
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

        float size = (float) font.getSize();
        TTFont nativeTTFont = getNativeFont(font);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer charPointBuffer = stack.mallocInt(1);
            FloatBuffer posX = stack.floats(0);
            FloatBuffer posY = stack.floats(size);

            float maxY = (float) (nativeTTFont.getFontInfo().getAscent() - nativeTTFont.getFontInfo().getDescent()) * stbtt_ScaleForPixelHeight(nativeTTFont.getFontInfo().getSTBFontInfo(), size);
            FontPlaneTexture plane = nativeTTFont.getPlaneTextures().get(0);
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

        TTFont TTFont = getNativeFont(font);
        STBTTFontinfo fontInfo = TTFont.getFontInfo().getSTBFontInfo();
        float scale = TTFont.getScaleForPixelHeight();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer charPointBuffer = stack.mallocInt(1);
            FloatBuffer posX = stack.floats(0);

            var fontPlaneTexture = TTFont.getPlaneTextures().get(0);
            for (int i = 0; i < text.length(); ) {
                i += getCodePoint(text, i, charPointBuffer);
                int charPoint = charPointBuffer.get(0);
                if (!isSupportedCharacter(TTFont, charPoint)) {
                    continue;
                }
                if (!TTFont.isBlockLoaded((char) charPoint)) {
                    fontPlaneTexture.putBlock(Character.UnicodeBlock.of(charPoint));
                }
                if (fontPlaneTexture.isWaitingForReloading()) {
                    fontPlaneTexture.bakeTexture(TTFont.getFont(), TTFont.getFontInfo());
                }
            }
            for (int i = 0, j = 0; i < text.length(); j++) {
                i += getCodePoint(text, i, charPointBuffer);

                int charPoint = charPointBuffer.get(0);

                if (!isSupportedCharacter(TTFont, charPoint)) {
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

    public TTFont getNativeFont(Font font) {
        return loadedNativeFonts.computeIfAbsent(font, this::loadNativeFont);
    }

    private TTFont loadNativeFont(Font font) {
        TTFontInfo parent = getFontInfo(font);
        if (parent == null) {
            throw new UnavailableFontException(font);
        }
        return loadNativeFont(parent, font);
    }

    private TTFont loadNativeFont(TTFontInfo info, Font font) {
        float scale = stbtt_ScaleForPixelHeight(info.getSTBFontInfo(), (float) font.getSize());
        var plane = new FontPlaneTexture();
        plane.putBlock(Character.UnicodeBlock.BASIC_LATIN);
        plane.bakeTexture(font, info);
        return new TTFont(info, font, scale, plane);
    }

    private TTFontInfo[] loadFontInfo(InputStream input) throws IOException {
        return loadFontInfo(null, BufferUtils.wrapAsByteBuffer(IOUtils.toByteArray(input)), false, true);
    }

    private TTFontInfo[] loadFontInfo(Path fontFile, boolean delayLoading, boolean available) throws IOException {
        ByteBuffer fontData;
        try (var fileChannel = FileChannel.open(fontFile)) {
            fontData = ByteBuffer.allocateDirect((int) fileChannel.size());
            fileChannel.read(fontData);
            fontData.flip();
        }
        return loadFontInfo(fontFile, fontData, delayLoading, available);
    }

    private TTFontInfo[] loadFontInfo(Path fontFile, ByteBuffer fontData, boolean delayLoading,
                                      boolean available) {
        var fontCount = stbtt_GetNumberOfFonts(fontData);
        if (fontCount == -1) {
            throw new IllegalStateException("Cannot determine the number of fonts in the font buffer.");
        }
        TTFontInfo[] fontInfos = new TTFontInfo[fontCount];

        for (int i = 0; i < fontCount; i++) {
            STBTTFontinfo stbFontInfo = STBTTFontinfo.create();
            if (!stbtt_InitFont(stbFontInfo, fontData, stbtt_GetFontOffsetForIndex(fontData, i))) {
                throw new IllegalStateException("Failed in initializing ttf font info");
            }

            TTFontNameTable nameTable = STBTTFontHelper.getFontNameTable(stbFontInfo);

            String family = findFirstFontNameString(nameTable, TTFontNameTable.FAMILY);
            String style = findFirstFontNameString(nameTable, TTFontNameTable.STYLE);

            if (family == null || style == null) {
                throw new FontLoadException("Failed to find font family or style");
            }

            try (MemoryStack stack = stackPush()) {
                IntBuffer pAscent = stack.mallocInt(1);
                IntBuffer pDescent = stack.mallocInt(1);
                IntBuffer pLineGap = stack.mallocInt(1);
                stbtt_GetFontVMetrics(stbFontInfo, pAscent, pDescent, pLineGap);

                IntBuffer x0 = stack.mallocInt(1);
                IntBuffer y0 = stack.mallocInt(1);
                IntBuffer x1 = stack.mallocInt(1);
                IntBuffer y1 = stack.mallocInt(1);
                stbtt_GetFontBoundingBox(stbFontInfo, x0, y0, x1, y1);

                var builder = TTFontInfo.builder()
                        .offsetIndex(i).family(family).style(style)
                        .nameTable(nameTable)
                        .ascent(pAscent.get(0)).descent(pDescent.get(0)).lineGap(pLineGap.get(0))
                        .boundingBox(new int[]{x0.get(), y0.get(), x1.get(), y1.get()});
                if (delayLoading) {
                    builder.fontFile(fontFile);
                } else {
                    builder.fontData(fontData).stbFontInfo(stbFontInfo);
                }

                var fontInfo = builder.build();

                if (available) {
                    setFontAvailable(fontInfo);
                }
                fontInfos[i] = fontInfo;
            }
        }
        return fontInfos;
    }

    private void setFontAvailable(TTFontInfo fontInfo) {
        Font font = fontInfo.getFont();
        loadedFontInfos.put(font.getFamily().toLowerCase(), font.getStyle().toLowerCase(), fontInfo);
        availableFonts.add(font);
    }

    private TTFontInfo getFontInfo(Font font) {
        return loadedFontInfos.get(font.getFamily().toLowerCase(), font.getStyle().toLowerCase());
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

    private boolean isSupportedCharacter(TTFont font, int character) {
        if (character == '\u001A' || character == '\uFFFD') return true;
        var counter = stbtt_FindGlyphIndex(font.getFontInfo().getSTBFontInfo(), 0x1A);
        var ci = stbtt_FindGlyphIndex(font.getFontInfo().getSTBFontInfo(), character);
        return counter != ci;
    }

    private static String findFirstFontNameString(TTFontNameTable nameTable, int nameId) {
        for (TTFontNameEntry entry : nameTable.getEntries()) {
            if (entry.getName() == nameId) {
                return entry.getString();
            }
        }
        return null;
    }

    public static final class Factory implements FontManager.Factory {

        @Override
        public String getName() {
            return "stb";
        }

        @Override
        public FontManager create() {
            return new STBTTFontManager();
        }
    }
}
