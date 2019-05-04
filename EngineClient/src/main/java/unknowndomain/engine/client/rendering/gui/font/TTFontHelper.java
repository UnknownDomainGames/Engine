package unknowndomain.engine.client.rendering.gui.font;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import unknowndomain.engine.client.gui.internal.FontHelper;
import unknowndomain.engine.client.gui.text.Font;
import unknowndomain.engine.client.rendering.Tessellator;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferFormats;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferMode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memUTF16;

public final class TTFontHelper implements FontHelper {

    public static final int SUPPORTING_CHARACTER_COUNT = 0x10000;

    private final Map<Font, NativeTTFont> loadedFont = new HashMap<>();
    private final Table<String, String, NativeTTFontParent> loadedFontParent = Tables.newCustomTable(new HashMap<>(), HashMap::new);

    private final Runnable beforeTextRender;
    private final Runnable afterTextRender;

    private Font defaultFont;

    public TTFontHelper(Runnable beforeTextRender, Runnable afterTextRender) {
        this.beforeTextRender = beforeTextRender;
        this.afterTextRender = afterTextRender;
    }

    @Override
    public Font getDefaultFont() {
        return defaultFont;
    }

    public void setDefaultFont(Font defaultFont) {
        this.defaultFont = defaultFont;
    }

    @Override
    public Font loadFont(InputStream input, float size) throws IOException {
        return loadNativeFont(loadNativeFontParent(input), size).getFont();
    }

    @Override
    public float computeTextWidth(CharSequence text, Font font) {
        if (text == null || text.length() == 0) {
            return 0;
        }

        STBTTFontinfo info = getNativeFont(font).getParent().getFontinfo();
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
                if(i < text.length()) {
                    getCodePoint(text, i, pCodePoint);
                pAdvancedWidth.put(0, pAdvancedWidth.get(0)
                        + stbtt_GetCodepointKernAdvance(info, cp, pCodePoint.get(0)));
                }
                width += pAdvancedWidth.get(0);
            }
        }

        return width * stbtt_ScaleForPixelHeight(info, font.getSize());
    }

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

            float factorX = 1.0f / nativeTTFont.getParent().getContentScaleX();
            float factorY = 1.0f / nativeTTFont.getParent().getContentScaleY();

            float centerY = 0 + font.getSize();

            int bitmapSize = nativeTTFont.getBitmapSize();
            STBTTAlignedQuad stbQuad = STBTTAlignedQuad.mallocStack(stack);
            float maxY = (float) (nativeTTFont.getParent().getAscent() - nativeTTFont.getParent().getDescent()) * stbtt_ScaleForPixelHeight(nativeTTFont.getParent().getFontinfo(), font.getSize());
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

    public NativeTTFont getNativeFont(Font font) {
        return loadedFont.computeIfAbsent(font, this::loadNativeFont);
    }

    public NativeTTFont loadNativeFont(ByteBuffer buffer, float size) {
        return loadNativeFont(loadNativeFontParent(buffer), size);
    }

    public NativeTTFont loadNativeFont(Font font) {
        return loadNativeFont(font.getFamily(), font.getStyle(), font.getSize());
    }

    public NativeTTFont loadNativeFont(String family, String style, float size) {
        NativeTTFontParent parent = loadedFontParent.get(family, style);
        if (parent == null) {
            throw new UnsupportedOperationException(String.format("Unsupported font. Family: %s, Style: %s", family, style));
        }
        return loadNativeFont(parent, size);
    }

    public NativeTTFont loadNativeFont(NativeTTFontParent parent, float size) {
        int textureId = GL11.glGenTextures();
        STBTTBakedChar.Buffer charBuffer = STBTTBakedChar.malloc(SUPPORTING_CHARACTER_COUNT);
        int bitmapSize = getBitmapSize(size, SUPPORTING_CHARACTER_COUNT);
        ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapSize * bitmapSize);
        stbtt_BakeFontBitmap(parent.getTtfData(), size, bitmap, bitmapSize, bitmapSize, 0, charBuffer);

        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, bitmapSize, bitmapSize, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
        glBindTexture(GL_TEXTURE_2D, 0);

        return new NativeTTFont(parent, size, textureId, charBuffer, bitmapSize);
    }

    public NativeTTFontParent loadNativeFontParent(InputStream input) throws IOException {
        byte[] bytes = IOUtils.toByteArray(input);
        return loadNativeFontParent(ByteBuffer.allocateDirect(bytes.length).put(bytes).flip());
    }

    public NativeTTFontParent loadNativeFontParent(ByteBuffer buffer) {
        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        if (!stbtt_InitFont(fontInfo, buffer)) {
            throw new IllegalStateException("Failed in initializing ttf font info");
        }

        String family = memUTF16(stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, STBTT_MS_EID_UNICODE_BMP, STBTT_MS_LANG_ENGLISH, 1)
                .order(ByteOrder.BIG_ENDIAN));
        String style = memUTF16(stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, STBTT_MS_EID_UNICODE_BMP, STBTT_MS_LANG_ENGLISH, 2)
                .order(ByteOrder.BIG_ENDIAN));

        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

            FloatBuffer p1 = stack.mallocFloat(1);
            FloatBuffer p2 = stack.mallocFloat(1);

            GLFW.glfwGetMonitorContentScale(GLFW.glfwGetPrimaryMonitor(), p1, p2);

            NativeTTFontParent parent = new NativeTTFontParent(fontInfo, buffer, family, style, pAscent.get(0), pDescent.get(0), pLineGap.get(0), p1.get(0), p2.get(0));
            loadedFontParent.put(family, style, parent);
            return parent;
        }
    }

    private int getBitmapSize(float size, int countOfChar) {
        return (int) Math.ceil((size + 2 * size / 16.0f) * Math.sqrt(countOfChar));
    }

    public void renderText(CharSequence text, float x, float y, int color, NativeTTFont nativeTTFont) {
        if (text == null || text.length() == 0) {
            return;
        }

        beforeTextRender.run();

        STBTTFontinfo fontinfo = nativeTTFont.getParent().getFontinfo();
        float fontHeight = nativeTTFont.getFontSize();
        float scale = stbtt_ScaleForPixelHeight(nativeTTFont.getParent().getFontinfo(), fontHeight);

        glBindTexture(GL_TEXTURE_2D, nativeTTFont.getTextureId());

        STBTTBakedChar.Buffer cdata = nativeTTFont.getCharBuffer();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer charPointBuffer = stack.mallocInt(1);
            FloatBuffer posX = stack.floats(x);
            FloatBuffer posY = stack.floats(y + fontHeight);

            float factorX = 1.0f / nativeTTFont.getParent().getContentScaleX();
            float factorY = 1.0f / nativeTTFont.getParent().getContentScaleY();
            factorX = 1.0f;
            factorY = 1.0f;

            float r = ((color >> 16) & 255) / 255f;
            float g = ((color >> 8) & 255) / 255f;
            float b = (color & 255) / 255f;
            float a = ((color >> 24) & 255) / 255f;

            float centerY = y + fontHeight;

            int bitmapSize = nativeTTFont.getBitmapSize();
            STBTTAlignedQuad stbQuad = STBTTAlignedQuad.mallocStack(stack);
            Tessellator tessellator = Tessellator.getInstance();
            GLBuffer builder = tessellator.getBuffer();
            builder.begin(GLBufferMode.TRIANGLES, GLBufferFormats.POSITION_COLOR_ALPHA_TEXTURE);
            for (int i = 0; i < text.length(); ) {
                i += getCodePoint(text, i, charPointBuffer);

                int charPoint = charPointBuffer.get(0);

                float centerX = posX.get(0);
                stbtt_GetBakedQuad(cdata, bitmapSize, bitmapSize, charPoint, posX, posY, stbQuad, true);
                posX.put(0, scale(centerX, posX.get(0), factorX));
                if (i < text.length()) {
                    getCodePoint(text, i, charPointBuffer);
                    posX.put(0, posX.get(0)
                            + stbtt_GetCodepointKernAdvance(fontinfo, charPoint, charPointBuffer.get(0)) * scale);
                }
                float x0 = scale(centerX, stbQuad.x0(), factorX), x1 = scale(centerX, stbQuad.x1(), factorX),
                        y0 = scale(centerY, stbQuad.y0(), factorY), y1 = scale(centerY, stbQuad.y1(), factorY); // FIXME: Incorrect y0
                builder.pos(x0, y0, 0).color(r, g, b, a).uv(stbQuad.s0(), stbQuad.t0()).endVertex();
                builder.pos(x0, y1, 0).color(r, g, b, a).uv(stbQuad.s0(), stbQuad.t1()).endVertex();
                builder.pos(x1, y0, 0).color(r, g, b, a).uv(stbQuad.s1(), stbQuad.t0()).endVertex();

                builder.pos(x1, y0, 0).color(r, g, b, a).uv(stbQuad.s1(), stbQuad.t0()).endVertex();
                builder.pos(x0, y1, 0).color(r, g, b, a).uv(stbQuad.s0(), stbQuad.t1()).endVertex();
                builder.pos(x1, y1, 0).color(r, g, b, a).uv(stbQuad.s1(), stbQuad.t1()).endVertex();

            }
            tessellator.draw();
        }

        afterTextRender.run();
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
