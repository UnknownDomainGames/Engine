package nullengine.client.rendering.gl.font;

import nullengine.client.rendering.font.Font;
import nullengine.client.rendering.font.UnicodeBlockWrapper;
import nullengine.client.rendering.gl.texture.FilterMode;
import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.math.Math2;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackRange;
import org.lwjgl.stb.STBTTPackedchar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;

public class FontPlaneTexture {
//    private List<Integer> abandonedTexIds;
    private GLTexture2D glTexture;
    private Font font;
    private int bitmapWidth;
    private int bitmapHeight;
    private NativeTTFontInfo fontInfo;

    private ArrayList<Character.UnicodeBlock> blocks;
    private Map<Character, CharQuad> charQuads;
    private boolean dirty = false;

    public FontPlaneTexture(){
//        abandonedTexIds = new ArrayList<>();
        blocks = new ArrayList<>();
        charQuads = new HashMap<>();
    }

    public void bind(){
        if(glTexture != null){
            glTexture.bind();
        }
    }

    public void putBlock(Character.UnicodeBlock block){
        if(!blocks.contains(block)) { //Make sure we don't load the same block twice
            blocks.add(block);
            dirty = true;
        }
    }

    public void removeBlock(Character.UnicodeBlock block){
        blocks.remove(block);
    }

    public boolean isBlockInList(Character.UnicodeBlock block){
        return blocks.contains(block);
    }

    public boolean isWaitingForReloading(){
        return dirty;
    }

    public CharQuad getQuad(char ch){
        return charQuads.get(ch);
    }

    public void bakeTexture(Font font, NativeTTFontInfo fontInfo){
        if(glTexture != null){
            glTexture.dispose();
        }
        this.font = font;
        this.fontInfo = fontInfo;
        charQuads.clear();
        var blockSize = blocks.stream().mapToInt(UnicodeBlockWrapper::getBlockSize).sum();
        int bitmapSize = getBitmapSize(font.getSize(), blockSize);
        bitmapWidth = bitmapSize;
        bitmapHeight = bitmapSize;
        ByteBuffer bitmap = ByteBuffer.allocateDirect(bitmapSize * bitmapSize);
        try(var context = STBTTPackContext.malloc()) {
            var ranges = STBTTPackRange.malloc(blocks.size());
            for (Character.UnicodeBlock block : blocks) {
                var range = STBTTPackRange.malloc();
                var blockSize1 = UnicodeBlockWrapper.getBlockSize(block);
                var charBuffer = STBTTPackedchar.malloc(blockSize1);
                range.set(font.getSize(), UnicodeBlockWrapper.getRange(block).lowerEndpoint(), null, blockSize1, charBuffer,(byte)1,(byte)1);
                ranges.put(range);
            }
            ranges.flip();
            stbtt_PackBegin(context, bitmap, bitmapSize, bitmapSize, 0, 1);
            stbtt_PackFontRanges(context, fontInfo.getFontData(),fontInfo.getOffsetIndex(), ranges);
            stbtt_PackEnd(context);

            glTexture = GLTexture2D.builder().format(GL_RED).internalFormat(GL_RED).type(GL_UNSIGNED_BYTE).magFilter(FilterMode.LINEAR).minFilter(FilterMode.LINEAR).level(0).build(bitmap, bitmapSize,bitmapSize);
            STBTTAlignedQuad stbQuad = STBTTAlignedQuad.mallocStack();
            for (int i = 0; i < blocks.size(); i++) {
                FloatBuffer posX = BufferUtils.createFloatBuffer(1);
                FloatBuffer posY = BufferUtils.createFloatBuffer(1);
                posY.put(0, font.getSize());
                var startPoint = UnicodeBlockWrapper.getRange(blocks.get(i)).lowerEndpoint();
                var cdata = ranges.get(i).chardata_for_range();
                for (int j = 0; j < UnicodeBlockWrapper.getBlockSize(blocks.get(i)); j++) {
                    stbtt_GetPackedQuad(cdata,bitmapWidth,bitmapHeight, j, posX, posY, stbQuad, false);
                    var quads = new Vector4f(stbQuad.x0(),stbQuad.y0(), stbQuad.x1(), stbQuad.y1());
                    var tex = new Vector4f(stbQuad.s0(),stbQuad.t0(), stbQuad.s1(), stbQuad.t1());
                    charQuads.put((char) (startPoint + j), new CharQuad((char) (startPoint + j), quads, tex, posX.get(0)));
                    posX.put(0,0);
                    posY.put(0, font.getSize());
                }
            }
        }
        dirty = false;
    }

    private int getBitmapSize(float size, int countOfChar) {
        return Math2.ceilPowerOfTwo((int) Math.ceil(size * Math.sqrt(countOfChar)));
    }

    public static class CharQuad{
        private final Vector4f pos;
        private final Vector4f texCoord;
        private final char character;
        private final float xOffset;

        public CharQuad(char character, Vector4f pos, Vector4f texCoord, float xOffset){
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
