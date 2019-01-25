package unknowndomain.engine.client.rendering.util;

import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.util.Color;
import unknowndomain.engine.util.Math2;

import java.nio.ByteBuffer;

@Deprecated
public class BufferBuilder {

    private ByteBuffer backingBuffer;

    public BufferBuilder(int size) {
        backingBuffer = BufferUtils.createByteBuffer(size);
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
            backingBuffer.limit(backingBuffer.capacity());
            offset = 48;
        }
    }

    public void finish() {
        if (!isDrawing) {
            throw new IllegalStateException("Not yet drawn!");
        } else {
            if (drawMode == GL11.GL_QUADS || drawMode == GL11.GL_QUAD_STRIP) {
                if (vertexCount % 4 != 0)
                    throw new IllegalArgumentException(String.format("Not enough vertexes! Expected: %d, Found: %d", (vertexCount / 4 + 1) * 4, vertexCount));
                ByteBuffer bb = ByteBuffer.allocate(backingBuffer.capacity());
                backingBuffer.rewind();
                bb.put(backingBuffer);
                backingBuffer.rewind();
                backingBuffer.clear();
                bb.flip();
                for (int i = 0; i < vertexCount / 4; i++) {
                    byte[] bs = new byte[getOffset() * 4];
                    bb.get(bs, 0, getOffset() * 4);
                    backingBuffer.put(bs, 0, getOffset() * 3);
                    backingBuffer.put(bs, getOffset() * 2, getOffset() * 2);
                    backingBuffer.put(bs, 0, getOffset());
                }
                vertexCount = vertexCount / 4 * 6;
                drawMode = drawMode == GL11.GL_QUAD_STRIP ? GL11.GL_TRIANGLE_STRIP : GL11.GL_TRIANGLES;
            }
            isDrawing = false;
            backingBuffer.position(0);
            backingBuffer.limit(vertexCount * getOffset());
        }
    }

    public void reset() {
        drawMode = 0;
        usePos = false;
        useColor = false;
        useTex = false;
        useNormal = false;
        vertexCount = 0;
        posOffset(0, 0, 0);
        offset = 0;
    }

    public int getOffset() {
        return offset;
    }

    public void grow(int needLength) {
        if (needLength > backingBuffer.remaining()) {
            int oldSize = this.backingBuffer.capacity();
            int newSize = oldSize + Math2.ceil(needLength, 0x200000);
            int oldPosition = backingBuffer.position();
            ByteBuffer newBuffer = BufferUtils.createByteBuffer(newSize);
            this.backingBuffer.position(0);
            newBuffer.put(this.backingBuffer);
            newBuffer.rewind();
            this.backingBuffer = newBuffer;
            newBuffer.position(oldPosition);
        }
    }

    private int vertexCount;

    public int getVertexCount() {
        return vertexCount;
    }

    public ByteBuffer build() {
        return backingBuffer;
    }

    public BufferBuilder pos(float x, float y, float z) {
        if (usePos) {
            int i = vertexCount * getOffset();
            backingBuffer.putFloat(i, x + posOffsetX);
            backingBuffer.putFloat(i + 4, y + posOffsetY);
            backingBuffer.putFloat(i + 8, z + posOffsetZ);
        }
        return this;
    }

    public BufferBuilder posOffset(float x, float y, float z) {
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
            int i = vertexCount * getOffset() + Float.BYTES * 3;
            backingBuffer.putFloat(i, r);
            backingBuffer.putFloat(i + 4, g);
            backingBuffer.putFloat(i + 8, b);
            backingBuffer.putFloat(i + 12, a);
        }
        return this;
    }

    public BufferBuilder tex(float u, float v) {
        if (useTex) {
            int i = vertexCount * getOffset() + Float.BYTES * 7;
            backingBuffer.putFloat(i, u);
            backingBuffer.putFloat(i + 4, v);
        }
        return this;
    }

    public BufferBuilder normal(Vector3fc vec) {
        return normal(vec.x(), vec.y(), vec.z());
    }

    public BufferBuilder normal(float nx, float ny, float nz) {
        if (useNormal) {
            int i = vertexCount * getOffset() + Float.BYTES * 9;
            backingBuffer.putFloat(i, nx);
            backingBuffer.putFloat(i + 4, ny);
            backingBuffer.putFloat(i + 8, nz);
        }
        return this;
    }

    public BufferBuilder put(byte[] bytes) {
        if (bytes.length % 48 != 0) {
            throw new IllegalArgumentException();
        }
        backingBuffer.put(bytes);
        vertexCount += bytes.length / 48;
        return this;
    }

    public BufferBuilder put(int[] ints) {
        if (ints.length % 12 != 0) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < ints.length; i++) {
            backingBuffer.putInt(ints[i]);
        }
        vertexCount += ints.length / 12;
        return this;
    }

    public BufferBuilder put(float[] floats) {
        if (floats.length % 12 != 0) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < floats.length; i++) {
            backingBuffer.putFloat(floats[i]);
        }
        vertexCount += floats.length / 12;
        return this;
    }

    public void endVertex() {
        ++vertexCount;
        grow(getOffset());
    }
}
