package nullengine.client.rendering.gl.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import nullengine.client.rendering.font.Font;
import nullengine.client.rendering.font.FontHelper;
import nullengine.client.rendering.font.FontLoadException;
import nullengine.client.rendering.font.UnavailableFontException;
import nullengine.client.rendering.gl.GLBuffer;
import nullengine.client.rendering.gl.GLDrawMode;
import nullengine.client.rendering.gl.vertex.GLVertexFormats;
import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.BreakIterator;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public final class WindowsFontHelper implements FontHelper {

    public static final int SUPPORTED_CHARACTER_COUNT = 0x10000;

    private static final int[] EIDs = {STBTT_MS_EID_UNICODE_BMP, STBTT_MS_EID_SHIFTJIS, STBTT_MS_EID_UNICODE_FULL, STBTT_MS_EID_SYMBOL};
    private static final int[] LANGs = {
            STBTT_MS_LANG_ENGLISH,
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
            STBTT_MS_LANG_SWEDISH};

    private final List<Font> availableFonts = new ArrayList<>();
    private final Table<String, String, NativeTTFontInfo> loadedFontInfos = Tables.newCustomTable(new HashMap<>(), HashMap::new);
    private final Map<Font, NativeTTFont> loadedNativeFonts = new HashMap<>();

    private Font defaultFont;

    public WindowsFontHelper() {
        initLocalFonts();
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
        for (Path fontFile : findLocalTTFonts()) {
            try {
                loadNativeFontInfo(fontFile);
            } catch (IOException | IllegalStateException ignored) {
            }
        }
    }

    private List<Path> findLocalTTFonts() {
        try {
            Predicate<Path> typefaceFilter = path -> path.getFileName().toString().endsWith(".ttf") || path.getFileName().toString().endsWith(".ttc");
            var fonts = Files.walk(Path.of("C:\\Windows\\Fonts").toAbsolutePath())
                    .filter(typefaceFilter)
                    .collect(Collectors.toList());
            var userFontDir = Path.of(System.getProperty("user.home"), "Appdata","Local","Microsoft", "Windows", "Fonts");
            if(userFontDir.toFile().exists()){
                var userFont = Files.walk(userFontDir).filter(typefaceFilter).collect(Collectors.toList());
                fonts.addAll(userFont);
            }
            return fonts;
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public Font getDefaultFont() {
        return defaultFont;
    }

    public void setDefaultFont(Font defaultFont) {
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
        return loadNativeFontInfo(path).getFont();
    }

    @Override
    public Font loadFont(Path path, float size) throws IOException {
        NativeTTFontInfo nativeTTFontInfo = loadNativeFontInfo(path);
        return new Font(nativeTTFontInfo.getFont(), size);
    }

    @Override
    public Font loadFont(InputStream input) throws IOException {
        return new Font(loadNativeFontInfo(input).getFont(), 1);
    }

    @Override
    public Font loadFont(InputStream input, float size) throws IOException {
        return new Font(loadNativeFontInfo(input).getFont(), size);
    }

    @Override
    public List<String> wrapText(String text, float width, Font font){
        if(computeTextWidth(text, font) <= width || width == 0){
            return Lists.newArrayList(text);
        }else{
            int l = 0, h = text.length() - 1;
            var breaker = BreakIterator.getLineInstance();
            breaker.setText(text);
            while (l < h){
                int m = (l + h) / 2;
                if(computeTextWidth(text.substring(0, breaker.following(m)), font) <= width){
                    l = m + 1;
                }
                else{
                    h = m - 1;
                }
            }
            return Stream.concat(Stream.of(text.substring(0, breaker.following(l))), wrapText(text.substring(breaker.following(l)), width, font).stream()).collect(Collectors.toList());
        }
    }

    @Override
    public float computeTextWidth(String text, Font font, float ceilingWidth) {
        if (text == null || text.length() == 0) {
            return 0;
        }
        List<String> trial;
        if(ceilingWidth != -1){
            trial = text.lines().flatMap(str->wrapText(str, ceilingWidth, font).stream()).collect(Collectors.toList());
        }else{
            trial = text.lines().collect(Collectors.toList());
        }
        if(trial.size() > 1){
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
        if(ceilingWidth != -1){
            trial = text.lines().flatMap(str->wrapText(str, ceilingWidth, font).stream()).collect(Collectors.toList());
        }else{
            trial = text.lines().collect(Collectors.toList());
        }
        if(trial.size() > 1){
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
                if(!isSupportedCharacter(nativeTTFont, charPoint)) {
                    continue;
                }
                if(!nativeTTFont.isBlockLoaded((char) charPoint)){
                    plane.putBlock(Character.UnicodeBlock.of(charPoint));
                }
                var quad = plane.getQuad((char) charPoint);
                if(quad == null) continue;
                float diff = /*Math.abs(stbQuad.y0() - stbQuad.y1())*/ quad.getPos().w();
                if (maxY < diff) {
                    maxY = diff;
                }
            }
            return maxY * leading;
        }
    }

    private void bindTexture(NativeTTFont nativeTTFont) {
        nativeTTFont.getPlaneTextures().get(0).bind();
    }

    @Override
    public void renderText(GLBuffer buffer, CharSequence text, Font font, int color, Runnable renderer) throws UnavailableFontException {
        if (text == null || text.length() == 0) {
            return;
        }

        NativeTTFont nativeFont = getNativeFont(font);
        bindTexture(nativeFont);
        generateMesh(buffer, text, nativeFont, color);
        renderer.run();
    }

    private void generateMesh(GLBuffer buffer, CharSequence text, NativeTTFont nativeTTFont, int color) {
        STBTTFontinfo fontInfo = nativeTTFont.getInfo().getFontInfo();
        float fontHeight = nativeTTFont.getFont().getSize();
        float scale = nativeTTFont.getScaleForPixelHeight();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer charPointBuffer = stack.mallocInt(1);
            FloatBuffer posX = stack.floats(0);
            FloatBuffer posY = stack.floats(0 + fontHeight);

            float factorX = 1.0f * nativeTTFont.getInfo().getContentScaleX();
            float factorY = 1.0f * nativeTTFont.getInfo().getContentScaleY();
            factorX = 1.0f;
            factorY = 1.0f;

            float r = ((color >> 16) & 255) / 255f;
            float g = ((color >> 8) & 255) / 255f;
            float b = (color & 255) / 255f;
            float a = ((color >> 24) & 255) / 255f;

            float centerY = 0 + fontHeight;

            var fontPlaneTexture = nativeTTFont.getPlaneTextures().get(0);
            for (int i = 0; i < text.length();) {
                i += getCodePoint(text, i, charPointBuffer);
                int charPoint = charPointBuffer.get(0);
                if(!isSupportedCharacter(nativeTTFont, charPoint)) {
                    continue;
                }
                if(!nativeTTFont.isBlockLoaded((char) charPoint)){
                    fontPlaneTexture.putBlock(Character.UnicodeBlock.of(charPoint));
                }
                if(fontPlaneTexture.isWaitingForReloading()){
                    fontPlaneTexture.bakeTexture(nativeTTFont.getFont(), nativeTTFont.getInfo());
                    fontPlaneTexture.bind();
                }
            }
            buffer.begin(GLDrawMode.TRIANGLES, GLVertexFormats.POSITION_COLOR_ALPHA_TEXTURE);
            for (int i = 0; i < text.length();) {
                i += getCodePoint(text, i, charPointBuffer);

                int charPoint = charPointBuffer.get(0);

                if(!isSupportedCharacter(nativeTTFont, charPoint)) {
                    continue;
                }

                float centerX = posX.get(0);
                var quads = fontPlaneTexture.getQuad((char) charPoint);
                if(quads == null) continue;
                posX.put(0, scale(centerX, posX.get(0) + quads.getXOffset(), factorX));
                if (i < text.length()) {
                    getCodePoint(text, i, charPointBuffer);
                    posX.put(0, posX.get(0)
                            + stbtt_GetCodepointKernAdvance(fontInfo, charPoint, charPointBuffer.get(0)) * scale);
                }
                float x0 = (float)Math.floor(scale(centerX, centerX + quads.getPos().x(), factorX) + 0.5),
                        x1 = (float)Math.floor(scale(centerX, centerX + quads.getPos().z(), factorX) + 0.5),
                        y0 = (float)Math.floor(scale(centerY, quads.getPos().y(), factorY) + 0.5),
                        y1 = (float)Math.floor(scale(centerY, quads.getPos().w(), factorY) + 0.5); // FIXME: Incorrect y0
                buffer.pos(x0, y0, 0).color(r, g, b, a).uv(quads.getTexCoord().x(), quads.getTexCoord().y()).endVertex();
                buffer.pos(x0, y1, 0).color(r, g, b, a).uv(quads.getTexCoord().x(), quads.getTexCoord().w()).endVertex();
                buffer.pos(x1, y0, 0).color(r, g, b, a).uv(quads.getTexCoord().z(), quads.getTexCoord().y()).endVertex();

                buffer.pos(x1, y0, 0).color(r, g, b, a).uv(quads.getTexCoord().z(), quads.getTexCoord().y()).endVertex();
                buffer.pos(x0, y1, 0).color(r, g, b, a).uv(quads.getTexCoord().x(), quads.getTexCoord().w()).endVertex();
                buffer.pos(x1, y1, 0).color(r, g, b, a).uv(quads.getTexCoord().z(), quads.getTexCoord().w()).endVertex();
            }
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
        ByteBuffer fontData = info.getFontData();
        float scale = stbtt_ScaleForPixelHeight(info.getFontInfo(), font.getSize());
        var plane = new FontPlaneTexture();
        plane.putBlock(Character.UnicodeBlock.BASIC_LATIN);
        plane.bakeTexture(font, info);
        return new NativeTTFont(info, font, scale, plane);
    }

    private NativeTTFontInfo loadNativeFontInfo(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        ByteBuffer fontData = ByteBuffer.allocateDirect(bytes.length).put(bytes).flip();
        var fontCount = stbtt_GetNumberOfFonts(fontData);
        if(fontCount == -1){
            throw new IllegalArgumentException(String.format("Cannot determine the number of fonts in the font file. File: %s", path));
        }
        NativeTTFontInfo parent = null;
        for (int i = 0; i < fontCount; i++) {
            STBTTFontinfo fontInfo = STBTTFontinfo.create();
            if (!stbtt_InitFont(fontInfo, fontData, stbtt_GetFontOffsetForIndex(fontData, i))) {
                throw new IllegalStateException(String.format("Failed in initializing ttf font info. File: %s", path));
            }

            int encodingId = findEncodingId(fontInfo);
            if (encodingId == -1) {
                throw new FontLoadException("Cannot load font because of not found encoding id. Path: " + path);
            }

            String family = stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, encodingId, STBTT_MS_LANG_ENGLISH, 1)
                    .order(ByteOrder.BIG_ENDIAN).asCharBuffer().toString();
            String style = stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, encodingId, STBTT_MS_LANG_ENGLISH, 2)
                    .order(ByteOrder.BIG_ENDIAN).asCharBuffer().toString();

            try (MemoryStack stack = stackPush()) {
                IntBuffer pAscent = stack.mallocInt(1);
                IntBuffer pDescent = stack.mallocInt(1);
                IntBuffer pLineGap = stack.mallocInt(1);

                stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

                FloatBuffer p1 = stack.mallocFloat(1);
                FloatBuffer p2 = stack.mallocFloat(1);

                GLFW.glfwGetMonitorContentScale(GLFW.glfwGetPrimaryMonitor(), p1, p2);

                IntBuffer x0 = stack.mallocInt(1);
                IntBuffer y0 = stack.mallocInt(1);
                IntBuffer x1 = stack.mallocInt(1);
                IntBuffer y1 = stack.mallocInt(1);
                stbtt_GetFontBoundingBox(fontInfo, x0, y0, x1, y1);

            parent = NativeTTFontInfo.builder()
                    .fontFile(path)
                    .platformId(STBTT_PLATFORM_ID_MICROSOFT)
                    .encodingId(encodingId)
                    .languageId(STBTT_MS_LANG_ENGLISH)
                    .family(family).style(style).offsetIndex(i)
                    .ascent(pAscent.get(0)).descent(pDescent.get(0)).lineGap(pLineGap.get(0))
                    .contentScaleX(p1.get(0)).contentScaleY(p2.get(0))
                    .boundingBox(new int[]{x0.get(), y0.get(), x1.get(), y1.get()})
                    .build();
            loadedFontInfos.put(family, style, parent);
            availableFonts.add(parent.getFont());
            }
        }
        return parent;
    }

    private NativeTTFontInfo loadNativeFontInfo(InputStream input) throws IOException {
        byte[] bytes = IOUtils.toByteArray(input);
        return loadNativeFontInfo(ByteBuffer.allocateDirect(bytes.length).put(bytes).flip());
    }

    private NativeTTFontInfo loadNativeFontInfo(ByteBuffer buffer) {
        var fontCount = stbtt_GetNumberOfFonts(buffer);
        if(fontCount == -1){
            throw new IllegalArgumentException("Cannot determine the number of fonts in the font buffer.");
        }
        NativeTTFontInfo parent = null;
        for (int i = 0; i < fontCount; i++) {
            STBTTFontinfo fontInfo = STBTTFontinfo.create();
            if (!stbtt_InitFont(fontInfo, buffer)) {
                throw new IllegalStateException("Failed in initializing ttf font info");
            }

            int encodingId = findEncodingId(fontInfo);
            if (encodingId == -1) {
                throw new FontLoadException("Cannot load font because of not found encoding id.");
            }

            String family = stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, encodingId, STBTT_MS_LANG_ENGLISH, 1)
                    .order(ByteOrder.BIG_ENDIAN).asCharBuffer().toString();
            String style = stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, encodingId, STBTT_MS_LANG_ENGLISH, 2)
                    .order(ByteOrder.BIG_ENDIAN).asCharBuffer().toString();

            try (MemoryStack stack = stackPush()) {
                IntBuffer pAscent = stack.mallocInt(1);
                IntBuffer pDescent = stack.mallocInt(1);
                IntBuffer pLineGap = stack.mallocInt(1);

                stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

                FloatBuffer p1 = stack.mallocFloat(1);
                FloatBuffer p2 = stack.mallocFloat(1);

                GLFW.glfwGetMonitorContentScale(GLFW.glfwGetPrimaryMonitor(), p1, p2);

                IntBuffer x0 = stack.mallocInt(1);
                IntBuffer y0 = stack.mallocInt(1);
                IntBuffer x1 = stack.mallocInt(1);
                IntBuffer y1 = stack.mallocInt(1);
                stbtt_GetFontBoundingBox(fontInfo, x0, y0, x1, y1);

                parent = NativeTTFontInfo.builder()
                        .fontData(buffer)
                        .fontInfo(fontInfo)
                        .platformId(STBTT_PLATFORM_ID_MICROSOFT)
                        .encodingId(encodingId)
                        .languageId(STBTT_MS_LANG_ENGLISH)
                        .family(family).style(style).offsetIndex(i)
                        .ascent(pAscent.get(0)).descent(pDescent.get(0)).lineGap(pLineGap.get(0))
                        .contentScaleX(p1.get(0)).contentScaleY(p2.get(0))
                        .boundingBox(new int[]{x0.get(), y0.get(), x1.get(), y1.get()})
                        .build();
                loadedFontInfos.put(family, style, parent);
                availableFonts.add(parent.getFont());
            }
        }
        return parent;
    }

    private int findEncodingId(STBTTFontinfo fontInfo) {
        for (int i = 0; i < EIDs.length; i++) {
            if (stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, EIDs[i], STBTT_MS_LANG_ENGLISH, 1) != null) {
                return EIDs[i];
            }
        }
        return -1;
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

    private boolean isSupportedCharacter(NativeTTFont font, int character){
        if(character == '\u001A' || character == '\uFFFD') return true;
        var counter = stbtt_FindGlyphIndex(font.getInfo().getFontInfo(), 0x1A);
        var ci = stbtt_FindGlyphIndex(font.getInfo().getFontInfo(), character);
        return counter != ci;
    }
}
