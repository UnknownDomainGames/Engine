package engine.graphics.lwjgl.font;

import engine.graphics.font.Font;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Texture2D;
import engine.math.Math2;
import org.joml.Vector4f;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackRange;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.stb.STBTruetype.*;

public final class FontPlaneTexture {
    private Texture2D texture;
    private Font font;
    private TTFontInfo fontInfo;

    private ArrayList<Character.UnicodeBlock> blocks;
    private Map<Character, CharQuad> charQuads;
    private boolean dirty = false;

    public FontPlaneTexture() {
        blocks = new ArrayList<>();
        charQuads = new HashMap<>();
    }

    public Font getFont() {
        return font;
    }

    public Texture2D getTexture() {
        return texture;
    }

    public TTFontInfo getFontInfo() {
        return fontInfo;
    }

    public void putBlock(Character.UnicodeBlock block) {
        if (!blocks.contains(block)) { //Make sure we don't load the same block twice
            blocks.add(block);
            dirty = true;
        }
    }

    public void removeBlock(Character.UnicodeBlock block) {
        blocks.remove(block);
    }

    public boolean isBlockInList(Character.UnicodeBlock block) {
        return blocks.contains(block);
    }

    public boolean isWaitingForReloading() {
        return dirty;
    }

    public CharQuad getQuad(char ch) {
        return charQuads.get(ch);
    }

    public void bakeTexture(Font font, TTFontInfo fontInfo) {
        if (texture != null) {
            texture.dispose();
        }
        this.font = font;
        this.fontInfo = fontInfo;
        charQuads.clear();

        float fontSize = (float) font.getSize();
        int bitmapSize = getBitmapSize(fontSize);
        ByteBuffer bitmap = ByteBuffer.allocateDirect(bitmapSize * bitmapSize);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var context = STBTTPackContext.mallocStack(stack);

            var ranges = STBTTPackRange.malloc(blocks.size());
            for (Character.UnicodeBlock block : blocks) {
                var range = STBTTPackRange.malloc();
                var blockSize = UnicodeBlockWrapper.getBlockSize(block);
                var charBuffer = STBTTPackedchar.malloc(blockSize);
                range.set(fontSize, UnicodeBlockWrapper.getRange(block).lowerEndpoint(), null, blockSize, charBuffer, (byte) 1, (byte) 1);
                ranges.put(range);
            }
            ranges.flip();
            stbtt_PackBegin(context, bitmap, bitmapSize, bitmapSize, 0, 1);
            stbtt_PackFontRanges(context, fontInfo.getFontData(), fontInfo.getOffsetIndex(), ranges);
            stbtt_PackEnd(context);

            texture = Texture2D.builder().format(ColorFormat.RED8).magFilter(FilterMode.LINEAR).minFilter(FilterMode.LINEAR).build(bitmap, bitmapSize, bitmapSize);

            STBTTAlignedQuad stbQuad = STBTTAlignedQuad.mallocStack(stack);
            FloatBuffer posX = stack.mallocFloat(1);
            FloatBuffer posY = stack.mallocFloat(1);
            for (int i = 0; i < blocks.size(); i++) {
                var startPoint = UnicodeBlockWrapper.getRange(blocks.get(i)).lowerEndpoint();
                var cdata = ranges.get(i).chardata_for_range();
                for (int j = 0; j < UnicodeBlockWrapper.getBlockSize(blocks.get(i)); j++) {
                    posX.put(0, 0);
                    posY.put(0, fontSize);
                    stbtt_GetPackedQuad(cdata, bitmapSize, bitmapSize, j, posX, posY, stbQuad, false);
                    var quads = new Vector4f(stbQuad.x0(), stbQuad.y0(), stbQuad.x1(), stbQuad.y1());
                    var tex = new Vector4f(stbQuad.s0(), stbQuad.t0(), stbQuad.s1(), stbQuad.t1());
                    charQuads.put((char) (startPoint + j), new CharQuad((char) (startPoint + j), quads, tex, posX.get(0)));
                }
            }
        }
        dirty = false;
    }

    private int getBitmapSize(float size) {
        int countOfChar = 0;
        for (Character.UnicodeBlock block : blocks) {
            countOfChar += UnicodeBlockWrapper.getBlockSize(block);
        }
        return Math2.ceilPowerOfTwo((int) Math.ceil(size * Math.sqrt(countOfChar)));
    }

    public static class CharQuad {
        private final Vector4f pos;
        private final Vector4f texCoord;
        private final char character;
        private final float xOffset;

        public CharQuad(char character, Vector4f pos, Vector4f texCoord, float xOffset) {
            this.character = character;
            this.pos = pos;
            this.texCoord = texCoord;
            this.xOffset = xOffset;
        }

        public char getCharacter() {
            return character;
        }

        public Vector4f getPos() {
            return pos;
        }

        public Vector4f getTexCoord() {
            return texCoord;
        }

        public float getXOffset() {
            return xOffset;
        }
    }
}
