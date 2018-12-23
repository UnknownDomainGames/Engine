package unknowndomain.engine.client.rendering.util;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.util.Color;
import unknowndomain.engine.util.Math2;

import java.nio.ByteBuffer;

public class BufferBuilder {

    private ByteBuffer byteBuffer;

    public BufferBuilder(int size) {
        byteBuffer = BufferUtils.createByteBuffer(size);
    }

    private float posOffsetX;
    private float posOffsetY;
    private float posOffsetZ;

    private boolean isDrawing;
    private int drawMode;
    private boolean usePos;
    private boolean useColor;
    private boolean useTex;
    private boolean useNormal;
    private int offset;

    public boolean isDrawing() {
        return isDrawing;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public boolean isColorEnabled() {
        return useColor;
    }

    public boolean isPosEnabled() {
        return usePos;
    }

    public boolean isTexEnabled() {
        return useTex;
    }

    public boolean isNormalEnabled() {
        return useNormal;
    }

    public void begin(int mode, boolean pos, boolean color, boolean tex) {
        begin(mode, pos, color, tex, false);
    }

    public void begin(int mode, boolean pos, boolean color, boolean tex, boolean normal) {
        if (isDrawing) {
            throw new IllegalStateException("Already drawing!");
        } else {
            isDrawing = true;
            reset();
            drawMode = mode;
            usePos = pos;
            useColor = color;
            useTex = tex;
            useNormal = normal;
            byteBuffer.limit(byteBuffer.capacity());
            offset = (usePos ? Float.BYTES * 3 : 0) + (useColor ? Float.BYTES * 4 : 0) + (useTex ? Float.BYTES * 2 : 0) + (useNormal ? Float.BYTES * 3 : 0);
        }
    }

    public void finish() {
        if (!isDrawing) {
            throw new IllegalStateException("Not yet drawn!");
        } else {
            if (drawMode == GL11.GL_QUADS || drawMode == GL11.GL_QUAD_STRIP) {
                if (vertexCount % 4 != 0)
                    throw new IllegalArgumentException(String.format("Not enough vertexes! Expected: %d, Found: %d", (vertexCount / 4 + 1) * 4, vertexCount));
                ByteBuffer bb = ByteBuffer.allocate(byteBuffer.capacity());
                byteBuffer.rewind();
                bb.put(byteBuffer);
                byteBuffer.rewind();
                byteBuffer.clear();
                bb.flip();
                for (int i = 0; i < vertexCount / 4; i++) {
                    byte[] bs = new byte[getOffset() * 4];
                    bb.get(bs, 0, getOffset() * 4);
                    byteBuffer.put(bs, 0, getOffset() * 3);
                    byteBuffer.put(bs, getOffset() * 2, getOffset() * 2);
                    byteBuffer.put(bs, 0, getOffset());
                }
                vertexCount = vertexCount / 4 * 6;
                drawMode = drawMode == GL11.GL_QUAD_STRIP ? GL11.GL_TRIANGLE_STRIP : GL11.GL_TRIANGLES;
            }
            isDrawing = false;
            byteBuffer.position(0);
            byteBuffer.limit(vertexCount * getOffset());
        }
    }

    public void reset() {
        drawMode = 0;
        usePos = false;
        useColor = false;
        useTex = false;
        useNormal = false;
        vertexCount = 0;
        posOffest(0, 0, 0);
        offset = 0;
    }

    public int getOffset() {
        return offset;
    }

    public void grow(int deltaLen) {
        if (deltaLen > byteBuffer.remaining()) {
            int i = this.byteBuffer.capacity();
            int newSize = i + Math2.roundUp(deltaLen, 0x200000);
            int k = byteBuffer.position();
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer(newSize);
            this.byteBuffer.position(0);
            bytebuffer.put(this.byteBuffer);
            bytebuffer.rewind();
            this.byteBuffer = bytebuffer;
            bytebuffer.position(k);
        }
    }

    private int vertexCount;

    public int getCount() {
        return byteBuffer.limit();
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public ByteBuffer build() {
        return byteBuffer;
    }

    public BufferBuilder pos(float x, float y, float z) {
        if (usePos) {
            int i = vertexCount * getOffset();
            byteBuffer.putFloat(i, x + posOffsetX);
            byteBuffer.putFloat(i + 4, y + posOffsetY);
            byteBuffer.putFloat(i + 8, z + posOffsetZ);
        }
        return this;
    }

    public BufferBuilder posOffest(float x, float y, float z) {
        posOffsetX = x;
        posOffsetY = y;
        posOffsetZ = z;
        return this;
    }

    public BufferBuilder color(Color color) {
        if (useColor) {
            color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
        return this;
    }

    public BufferBuilder color(int color) {
        if (useColor) {
            color(((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f, ((color >> 24) & 255) / 255f);
        }
        return this;
    }

    public BufferBuilder color(float r, float g, float b) {
        color(r, g, b, 1);
        return this;
    }

    public BufferBuilder color(float r, float g, float b, float a) {
        if (useColor) {
            int i = vertexCount * getOffset() + Float.BYTES * (usePos ? 3 : 0);
            byteBuffer.putFloat(i, r);
            byteBuffer.putFloat(i + 4, g);
            byteBuffer.putFloat(i + 8, b);
            byteBuffer.putFloat(i + 12, a);
        }
        return this;
    }

    public BufferBuilder tex(float u, float v) {
        if (useTex) {
            int i = vertexCount * getOffset() + Float.BYTES * ((useColor ? 4 : 0) + (usePos ? 3 : 0));
            byteBuffer.putFloat(i, u);
            byteBuffer.putFloat(i + 4, v);
        }
        return this;
    }

    public BufferBuilder normal(float nx, float ny, float nz) {
        if (useNormal) {
            int i = vertexCount * getOffset() + Float.BYTES * ((useColor ? 4 : 0) + (useTex ? 2 : 0) + (usePos ? 3 : 0));
            byteBuffer.putFloat(i, nx);
            byteBuffer.putFloat(i + 4, ny);
            byteBuffer.putFloat(i + 8, nz);
        }
        return this;
    }

    public void endVertex() {
        ++vertexCount;
        grow(getOffset());
    }
}
