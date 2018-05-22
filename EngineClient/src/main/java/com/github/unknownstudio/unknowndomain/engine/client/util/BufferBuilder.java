package com.github.unknownstudio.unknowndomain.engine.client.util;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.*;

public class BufferBuilder {

    private ByteBuffer byteBuffer;
    private IntBuffer intBuffer;
    private ShortBuffer shortBuffer;
    private FloatBuffer floatBuffer;

    public BufferBuilder(int size){
        byteBuffer = BufferUtils.createByteBuffer(size);
        intBuffer = byteBuffer.asIntBuffer();
        shortBuffer = byteBuffer.asShortBuffer();
        floatBuffer = byteBuffer.asFloatBuffer();
    }

    private boolean isDrawing;
    private int drawMode;
    private boolean usePos;
    private boolean useColor;
    private boolean useTex;

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

    public void begin(int mode, boolean pos, boolean color, boolean tex){
        if (isDrawing){
            throw new IllegalStateException("Already drawing!");
        }
        else{
            isDrawing = true;
            reset();
            drawMode = mode;
            usePos = pos;
            useColor = color;
            useTex = tex;
            byteBuffer.limit(byteBuffer.capacity());
        }
    }

    public void finish(){
        if (!isDrawing){
            throw new IllegalStateException("Not yet drawn!");
        }else{
            if (drawMode == GL11.GL_QUADS || drawMode == GL11.GL_QUAD_STRIP){
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
                    bb.get(bs,0, getOffset() * 4);
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

    public void reset(){
        drawMode = 0;
        usePos = false;
        useColor = false;
        useTex = false;
        vertexCount = 0;
    }

    public int getOffset(){
        return (usePos ? Float.BYTES * 3 : 0) + (useColor ? Float.BYTES * 4 : 0) + (useTex ? Float.BYTES * 2 : 0);
    }

    public void grow(int deltaLen){
         if (roundUp(deltaLen, 4) / 4 > intBuffer.remaining() || vertexCount * getOffset() + deltaLen > byteBuffer.capacity()){

             int i = this.byteBuffer.capacity();
             int j = i + roundUp(deltaLen, 2097152);
             int k = intBuffer.position();
             ByteBuffer bytebuffer = ByteBuffer.allocateDirect(j).order(ByteOrder.nativeOrder());
             this.byteBuffer.position(0);
             bytebuffer.put(this.byteBuffer);
             bytebuffer.rewind();
             this.byteBuffer = bytebuffer;
             floatBuffer = this.byteBuffer.asFloatBuffer().asReadOnlyBuffer();
             intBuffer = this.byteBuffer.asIntBuffer();
             intBuffer.position(k);
             shortBuffer = this.byteBuffer.asShortBuffer();
             shortBuffer.position(k << 1);
         }
    }

    private int roundUp(int number, int interval){
        if (interval == 0)
        {
            return 0;
        }
        else if (number == 0)
        {
            return interval;
        }
        else
        {
            if (number < 0)
            {
                interval *= -1;
            }

            int i = number % interval;
            return i == 0 ? number : number + interval - i;
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

    public BufferBuilder pos(float x, float y, float z){
        if(usePos) {
            int i = vertexCount * getOffset();
            byteBuffer.putFloat(i, x);
            byteBuffer.putFloat(i + 4, y);
            byteBuffer.putFloat(i + 8, z);
        }
        return this;
    }

    public BufferBuilder color(float r,float g,float b,float a){
        if(useColor) {
            int i = vertexCount * getOffset() + Float.BYTES * (useTex ? 5 : 3);
            byteBuffer.putFloat(i, r);
            byteBuffer.putFloat(i + 4, g);
            byteBuffer.putFloat(i + 8, b);
            byteBuffer.putFloat(i + 12, a);
        }
        return this;
    }

    public BufferBuilder tex(float u, float v){
        if(useTex) {
            int i = vertexCount * getOffset() + Float.BYTES * 3;
            byteBuffer.putFloat(i, u);
            byteBuffer.putFloat(i + 4, v);
        }
        return this;
    }

    public void endVertex(){
        ++vertexCount;
        grow(getOffset());
    }
}
