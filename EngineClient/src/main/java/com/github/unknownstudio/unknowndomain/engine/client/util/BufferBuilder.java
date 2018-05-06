package com.github.unknownstudio.unknowndomain.engine.client.util;

import java.nio.*;

public class BufferBuilder {

    private ByteBuffer byteBuffer;
    private IntBuffer intBuffer;
    private ShortBuffer shortBuffer;
    private FloatBuffer floatBuffer;

    public BufferBuilder(int size){
        byteBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
        intBuffer = byteBuffer.asIntBuffer();
        shortBuffer = byteBuffer.asShortBuffer();
        floatBuffer = byteBuffer.asFloatBuffer();
    }

    private boolean isDrawing;
    private int drawMode;
    private boolean usePos;
    private boolean useColor;
    private boolean useTex;

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

    public ByteBuffer build() {
        return byteBuffer;
    }

    public BufferBuilder pos(float x, float y, float z){
        int i = vertexCount * getOffset();
        byteBuffer.putFloat(i,x);
        byteBuffer.putFloat(i+4,y);
        byteBuffer.putFloat(i+8,z);
        return this;
    }

    public BufferBuilder color(float r,float g,float b,float a){
        int i = vertexCount * getOffset() + Float.BYTES * 5;
        byteBuffer.putFloat(i, r);
        byteBuffer.putFloat(i+4, g);
        byteBuffer.putFloat(i+8, b);
        byteBuffer.putFloat(i+12, a);
        return this;
    }

    public BufferBuilder tex(float u, float v){
        int i = vertexCount * getOffset() + Float.BYTES * 3;
        byteBuffer.putFloat(i,u);
        byteBuffer.putFloat(i + 4,v);
        return this;
    }

    public void endVertex(){
        ++vertexCount;
        grow(getOffset());
    }
}
