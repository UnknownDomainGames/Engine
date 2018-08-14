package unknowndomain.engine.client.rendering.gui;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import unknowndomain.engine.client.util.BufferBuilder;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

class TTFFontRenderer {
    private ByteBuffer ttfBuf;

    private final STBTTFontinfo fontinfo;
    private final int ascent;
    private final int descent;
    private final int lineGap;

    public static final int SUPPORTING_CHARACTER_COUNT = 256;
    private float contentScaleX;
    private float contentScaleY;

    public TTFFontRenderer(ByteBuffer ttfBuf) {
        this.ttfBuf = ttfBuf;
        fontinfo = STBTTFontinfo.create();
        if (!stbtt_InitFont(fontinfo, ttfBuf)) {
            throw new IllegalStateException("Failed in initializing ttf font info");
        }
        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(fontinfo, pAscent, pDescent, pLineGap);

            ascent = pAscent.get(0);
            descent = pDescent.get(0);
            lineGap = pLineGap.get(0);
            long moniter = GLFW.glfwGetPrimaryMonitor();

            FloatBuffer p1 = stack.mallocFloat(1);
            FloatBuffer p2 = stack.mallocFloat(1);

            GLFW.glfwGetMonitorContentScale(moniter, p1, p2);
            contentScaleX = p1.get(0);
            contentScaleY = p2.get(0);
        }
    }

    private Map<Integer, Pair<Integer, STBTTBakedChar.Buffer>> bufMap = new HashMap<>();

    private Pair<Integer, STBTTBakedChar.Buffer> getCharDataBuffer(int fontHeight) {
        if (!bufMap.containsKey(fontHeight)) {
            int texId = GL11.glGenTextures();
            STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(SUPPORTING_CHARACTER_COUNT);
            int side = getBitmapSide(fontHeight, SUPPORTING_CHARACTER_COUNT);
            ByteBuffer bitmap = BufferUtils.createByteBuffer(side * side);
            stbtt_BakeFontBitmap(ttfBuf, fontHeight, bitmap, side, side, 0, cdata);

            glEnable(GL_TEXTURE_2D);

            glBindTexture(GL_TEXTURE_2D, texId);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, side, side, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
            glBindTexture(GL_TEXTURE_2D, 0);
            bufMap.put(fontHeight, new ImmutablePair<>(texId, cdata));
        }
        return bufMap.get(fontHeight);
    }

    private void cleanCharDataBuffer(int fontHeight) {
        if (bufMap.containsKey(fontHeight)) {
            glDeleteTextures(bufMap.get(fontHeight).getLeft());
            bufMap.remove(fontHeight);
        }
    }

    public void drawText(String text, float x, float y, int color, int fontHeight) {
        float scale = stbtt_ScaleForPixelHeight(fontinfo, fontHeight);
        Pair<Integer, STBTTBakedChar.Buffer> pair = getCharDataBuffer(fontHeight);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, pair.getLeft());

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        STBTTBakedChar.Buffer cdata = pair.getRight();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer charPointBuffer = stack.mallocInt(1);
            FloatBuffer posX = stack.floats(x);
            FloatBuffer posY = stack.floats(y + fontHeight);

            float factorX = 1.0f / getContentScaleX();
            float factorY = 1.0f / getContentScaleY();

            float r = ((color >> 16) & 255) / 255f;
            float g = ((color >> 8) & 255) / 255f;
            float b = (color & 255) / 255f;
            float a = ((color >> 24) & 255) / 255f;

            float lineY = y + fontHeight;

            int side = getBitmapSide(fontHeight, SUPPORTING_CHARACTER_COUNT);
            STBTTAlignedQuad stbQuad = STBTTAlignedQuad.mallocStack(stack);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            builder.begin(GL_QUADS, true, true, true, false);
            for (int i = 0; i < text.length(); ) {
                i += getCodePoint(text, i, charPointBuffer);

                int charPoint = charPointBuffer.get(0);

                float cpX = posX.get(0);
                stbtt_GetBakedQuad(cdata, side, side, charPoint, posX, posY, stbQuad, true);
                posX.put(0, scale(cpX, posX.get(0), factorX));
                if (i < text.length()) {
                    getCodePoint(text, i, charPointBuffer);
                    posX.put(0, posX.get(0) + stbtt_GetCodepointKernAdvance(fontinfo, charPoint, charPointBuffer.get(0)) * scale);
                }
                float x0 = scale(x, stbQuad.x0(), factorX), x1 = scale(x, stbQuad.x1(), factorX),
                        y0 = scale(lineY, stbQuad.y0(), factorY), y1 = scale(lineY, stbQuad.y1(), factorY);
                System.out.println(x0 + " " + x1 + " " + y0 + " " + y1);
                builder.pos(x0, y0, 0).color(r, g, b, a).tex(stbQuad.s0(), stbQuad.t0()).endVertex();
                builder.pos(x0, y1, 0).color(r, g, b, a).tex(stbQuad.s0(), stbQuad.t1()).endVertex();
                builder.pos(x1, y1, 0).color(r, g, b, a).tex(stbQuad.s1(), stbQuad.t1()).endVertex();
                builder.pos(x1, y0, 0).color(r, g, b, a).tex(stbQuad.s1(), stbQuad.t0()).endVertex();
            }
            tessellator.draw();

            // renderLineBoundingBox(text, 0, text.length(), x, y + fontHeight, scale, fontHeight);
        }
    }

    private void renderLineBoundingBox(String text, int from, int to, float x, float y, float scale, int fontHeight) {
        glDisable(GL_TEXTURE_2D);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        float width = getStringWidth(fontinfo, text, from, to, fontHeight);
        y -= descent * scale;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL_QUADS, true, true, false, false);
        builder.pos(x, y, 0).color(1, 1, 1, 1).endVertex();
        builder.pos(x + width, y, 0).color(1, 1, 1, 1).endVertex();
        builder.pos(x + width, y - fontHeight, 0).color(1, 1, 1, 1).endVertex();
        builder.pos(x, y - fontHeight, 0).color(1, 1, 1, 1).endVertex();

        tessellator.draw();

        glEnable(GL_TEXTURE_2D);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    private float getStringWidth(STBTTFontinfo info, String text, int from, int to, int fontHeight) {
        int width = 0;

        try (MemoryStack stack = stackPush()) {
            IntBuffer pCodePoint = stack.mallocInt(1);
            IntBuffer pAdvancedWidth = stack.mallocInt(1);
            IntBuffer pLeftSideBearing = stack.mallocInt(1);

            int i = from;
            while (i < to) {
                i += getCodePoint(text, i, pCodePoint);
                int cp = pCodePoint.get(0);

                stbtt_GetCodepointHMetrics(info, cp, pAdvancedWidth, pLeftSideBearing);
                width += pAdvancedWidth.get(0);
            }
        }

        return width * stbtt_ScaleForPixelHeight(info, fontHeight);
    }

    private float scale(float center, float offset, float factor) {
        return (offset - center) * factor + center;
    }

    private static int getCodePoint(String text, int i, IntBuffer cpOut) {
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

    private int getBitmapSide(int fontHeight, int countOfChar) {
        return (int) Math.ceil((fontHeight + 2 * fontHeight / 16.0f) * Math.sqrt(countOfChar));
    }

    public float getContentScaleX() {
        return contentScaleX;
    }

    public float getContentScaleY() {
        return contentScaleY;
    }

}
