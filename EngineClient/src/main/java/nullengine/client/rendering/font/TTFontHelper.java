package nullengine.client.rendering.font;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import nullengine.Platform;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.client.rendering.util.buffer.GLBufferFormats;
import nullengine.client.rendering.util.buffer.GLBufferMode;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
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
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public final class TTFontHelper implements FontHelper {

    public static final int SUPPORTING_CHARACTER_COUNT = 0x10000;

    private final List<Font> availableFonts = new ArrayList<>();
    private final Table<String, String, NativeTTFontInfo> loadedFontInfos = Tables.newCustomTable(new HashMap<>(), HashMap::new);
    private final Map<Font, NativeTTFont> loadedNativeFonts = new HashMap<>();

    private Font defaultFont;

    public TTFontHelper() {
        initLocalFonts();
    }

    private void initLocalFonts() {
        for (Path fontFile : LocalFontUtils.findLocalTTFonts()) {
            try {
                loadNativeFontInfo(fontFile);
            } catch (IOException | IllegalStateException ignored) {
            }
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
    public float computeTextWidth(CharSequence text, Font font) {
        if (text == null || text.length() == 0) {
            return 0;
        }

        STBTTFontinfo info = getNativeFont(font).getInfo().getFontInfo();
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

        return width * stbtt_ScaleForPixelHeight(info, font.getSize());
    }

    @Override
    public float computeTextHeight(CharSequence text, Font font) {
        if (text == null || text.length() == 0) {
            return 0;
        }

        var nativeTTFont = getNativeFont(font);
        STBTTBakedChar.Buffer cdata = nativeTTFont.getCharBuffer();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer charPointBuffer = stack.mallocInt(1);
            FloatBuffer posX = stack.floats(0);
            FloatBuffer posY = stack.floats(0 + font.getSize());

            float factorX = 1.0f / nativeTTFont.getInfo().getContentScaleX();
            float factorY = 1.0f / nativeTTFont.getInfo().getContentScaleY();

            float centerY = 0 + font.getSize();

            int bitmapSize = nativeTTFont.getBitmapSize();
            STBTTAlignedQuad stbQuad = STBTTAlignedQuad.mallocStack(stack);
            float maxY = (float) (nativeTTFont.getInfo().getAscent() - nativeTTFont.getInfo().getDescent()) * stbtt_ScaleForPixelHeight(nativeTTFont.getInfo().getFontInfo(), font.getSize());
            for (int i = 0; i < text.length(); ) {
                i += getCodePoint(text, i, charPointBuffer);

                int charPoint = charPointBuffer.get(0);

                float centerX = posX.get(0);
                stbtt_GetBakedQuad(cdata, bitmapSize, bitmapSize, charPoint, posX, posY, stbQuad, true);
                float diff = /*Math.abs(stbQuad.y0() - stbQuad.y1())*/ stbQuad.y1();
                if (maxY < diff) {
                    maxY = diff;
                }
            }
            return maxY;
        }
    }

    private void bindTexture(NativeTTFont nativeTTFont) {
        glBindTexture(GL_TEXTURE_2D, nativeTTFont.getTextureId());
    }

    @Override
    public void renderText(GLBuffer buffer, CharSequence text, Font font, int color, Runnable renderer) throws UnavailableFontException {
        NativeTTFont nativeFont = getNativeFont(font);
        bindTexture(nativeFont);
        generateMesh(buffer, text, nativeFont, color);
        renderer.run();
        Platform.getEngineClient().getRenderContext().getTextureManager().getWhiteTexture().bind();
    }

    private void generateMesh(GLBuffer buffer, CharSequence text, NativeTTFont nativeTTFont, int color) {
        STBTTFontinfo fontInfo = nativeTTFont.getInfo().getFontInfo();
        float fontHeight = nativeTTFont.getFont().getSize();
        float scale = stbtt_ScaleForPixelHeight(nativeTTFont.getInfo().getFontInfo(), fontHeight);

        STBTTBakedChar.Buffer cdata = nativeTTFont.getCharBuffer();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer charPointBuffer = stack.mallocInt(1);
            FloatBuffer posX = stack.floats(0);
            FloatBuffer posY = stack.floats(0 + fontHeight);

            float factorX = 1.0f / nativeTTFont.getInfo().getContentScaleX();
            float factorY = 1.0f / nativeTTFont.getInfo().getContentScaleY();
            factorX = 1.0f;
            factorY = 1.0f;

            float r = ((color >> 16) & 255) / 255f;
            float g = ((color >> 8) & 255) / 255f;
            float b = (color & 255) / 255f;
            float a = ((color >> 24) & 255) / 255f;

            float centerY = 0 + fontHeight;

            int bitmapSize = nativeTTFont.getBitmapSize();
            STBTTAlignedQuad stbQuad = STBTTAlignedQuad.mallocStack(stack);
            buffer.begin(GLBufferMode.TRIANGLES, GLBufferFormats.POSITION_COLOR_ALPHA_TEXTURE);
            for (int i = 0; i < text.length(); ) {
                i += getCodePoint(text, i, charPointBuffer);

                int charPoint = charPointBuffer.get(0);

                float centerX = posX.get(0);
                stbtt_GetBakedQuad(cdata, bitmapSize, bitmapSize, charPoint, posX, posY, stbQuad, true);
                posX.put(0, scale(centerX, posX.get(0), factorX));
                if (i < text.length()) {
                    getCodePoint(text, i, charPointBuffer);
                    posX.put(0, posX.get(0)
                            + stbtt_GetCodepointKernAdvance(fontInfo, charPoint, charPointBuffer.get(0)) * scale);
                }
                float x0 = scale(centerX, stbQuad.x0(), factorX), x1 = scale(centerX, stbQuad.x1(), factorX),
                        y0 = scale(centerY, stbQuad.y0(), factorY), y1 = scale(centerY, stbQuad.y1(), factorY); // FIXME: Incorrect y0
                buffer.pos(x0, y0, 0).color(r, g, b, a).uv(stbQuad.s0(), stbQuad.t0()).endVertex();
                buffer.pos(x0, y1, 0).color(r, g, b, a).uv(stbQuad.s0(), stbQuad.t1()).endVertex();
                buffer.pos(x1, y0, 0).color(r, g, b, a).uv(stbQuad.s1(), stbQuad.t0()).endVertex();

                buffer.pos(x1, y0, 0).color(r, g, b, a).uv(stbQuad.s1(), stbQuad.t0()).endVertex();
                buffer.pos(x0, y1, 0).color(r, g, b, a).uv(stbQuad.s0(), stbQuad.t1()).endVertex();
                buffer.pos(x1, y1, 0).color(r, g, b, a).uv(stbQuad.s1(), stbQuad.t1()).endVertex();
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
        int textureId = GL11.glGenTextures();
        STBTTBakedChar.Buffer charBuffer = STBTTBakedChar.malloc(SUPPORTING_CHARACTER_COUNT);
        int bitmapSize = getBitmapSize(font.getSize(), SUPPORTING_CHARACTER_COUNT);
        ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapSize * bitmapSize);
        stbtt_BakeFontBitmap(info.getFontData(), font.getSize(), bitmap, bitmapSize, bitmapSize, 0, charBuffer);

        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, bitmapSize, bitmapSize, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
        glBindTexture(GL_TEXTURE_2D, 0);

        float scale = stbtt_ScaleForPixelHeight(info.getFontInfo(), font.getSize());

        return new NativeTTFont(info, font, textureId, charBuffer, bitmapSize, scale);
    }

    private NativeTTFontInfo loadNativeFontInfo(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        ByteBuffer fontData = ByteBuffer.allocateDirect(bytes.length).put(bytes).flip();
        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        if (!stbtt_InitFont(fontInfo, fontData)) {
            throw new IllegalStateException("Failed in initializing ttf font info");
        }

        String family = stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, STBTT_MS_EID_UNICODE_BMP, STBTT_MS_LANG_ENGLISH, 1)
                .order(ByteOrder.BIG_ENDIAN).asCharBuffer().toString();
        String style = stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, STBTT_MS_EID_UNICODE_BMP, STBTT_MS_LANG_ENGLISH, 2)
                .order(ByteOrder.BIG_ENDIAN).asCharBuffer().toString();

        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

            FloatBuffer p1 = stack.mallocFloat(1);
            FloatBuffer p2 = stack.mallocFloat(1);

            GLFW.glfwGetMonitorContentScale(GLFW.glfwGetPrimaryMonitor(), p1, p2);

            NativeTTFontInfo parent = new NativeTTFontInfo(path, family, style, pAscent.get(0), pDescent.get(0), pLineGap.get(0), p1.get(0), p2.get(0));
            loadedFontInfos.put(family, style, parent);
            availableFonts.add(parent.getFont());
            return parent;
        }
    }

    private NativeTTFontInfo loadNativeFontInfo(InputStream input) throws IOException {
        byte[] bytes = IOUtils.toByteArray(input);
        return loadNativeFontInfo(ByteBuffer.allocateDirect(bytes.length).put(bytes).flip());
    }

    private NativeTTFontInfo loadNativeFontInfo(ByteBuffer buffer) {
        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        if (!stbtt_InitFont(fontInfo, buffer)) {
            throw new IllegalStateException("Failed in initializing ttf font info");
        }

        String family = stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, STBTT_MS_EID_UNICODE_BMP, STBTT_MS_LANG_ENGLISH, 1).asCharBuffer().toString();
        String style = stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, STBTT_MS_EID_UNICODE_BMP, STBTT_MS_LANG_ENGLISH, 2).asCharBuffer().toString();

        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

            FloatBuffer p1 = stack.mallocFloat(1);
            FloatBuffer p2 = stack.mallocFloat(1);

            GLFW.glfwGetMonitorContentScale(GLFW.glfwGetPrimaryMonitor(), p1, p2);

            NativeTTFontInfo parent = new NativeTTFontInfo(buffer, fontInfo, family, style, pAscent.get(0), pDescent.get(0), pLineGap.get(0), p1.get(0), p2.get(0));
            loadedFontInfos.put(family, style, parent);
            availableFonts.add(parent.getFont());
            return parent;
        }
    }

    private int getBitmapSize(float size, int countOfChar) {
        return (int) Math.ceil((size + 2 * size / 16.0f) * Math.sqrt(countOfChar));
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
}
