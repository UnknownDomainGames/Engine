package nullengine.client.rendering.image;

import nullengine.util.Color;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryUtil.*;

public class BufferedImage {

    private int width, height;
    private int stride;
    private ByteBuffer backingBuffer;

    public static BufferedImage create(ByteBuffer buffer) throws IOException {
        if (!buffer.isDirect()) {
            ByteBuffer direct = ByteBuffer.allocateDirect(buffer.capacity());
            direct.put(buffer);
            direct.flip();
            buffer = direct;
        }
        try (var stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);
            ByteBuffer bitmapBuffer = STBImage.stbi_load_from_memory(buffer, w, h, c, 4);
            int width = w.get(0);
            int height = h.get(0);
            if (bitmapBuffer == null) {
                throw new IOException("File buffer cannot be load as pixel buffer by STBImage");
            }
            var tex = new BufferedImage(width, height);
            tex.getBuffer().put(bitmapBuffer);
            tex.getBuffer().flip();
            bitmapBuffer.clear();
            return tex;
        }
    }

    public BufferedImage(int size) {
        this(size, size);
    }

    public BufferedImage(int width, int height) {
        this.width = width;
        this.height = height;
        initBuffer();
    }

    public BufferedImage(int width, int height, int initColor) {
        this(width, height);
        fill(initColor);
    }

    public BufferedImage(int width, int height, ByteBuffer buffer) {
        this.width = width;
        this.height = height;
        this.backingBuffer = buffer;
    }

    protected void initBuffer() {
        stride = Integer.BYTES * width;
        backingBuffer = ByteBuffer.allocateDirect(stride * height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getBuffer() {
        return backingBuffer;
    }

    public void setTexture(int x, int y, ByteBuffer texture, int u, int v, int width, int height) {
        int bufferStride = width * Integer.BYTES;
        long textureAddress = memAddress(texture, 0);
        long address = memAddress(backingBuffer, 0);
        for (int i = 0; i < height; i++) {
            memCopy(textureAddress + (y + i) * this.stride + x * Integer.BYTES,
                    address + (u + i) * bufferStride + v * Integer.BYTES,
                    width * Integer.BYTES);
        }
        backingBuffer.clear();
    }

    public void setTexture(int x, int y, ByteBuffer buffer, int width, int height) {
        setTexture(x, y, buffer, 0, 0, width, height);
    }

    public void setTexture(int x, int y, BufferedImage texture) {
        setTexture(x, y, texture.getBuffer(), texture.getWidth(), texture.getHeight());
    }

    public void setTexture(int x, int y, BufferedImage texture, int u, int v) {
        setTexture(x, y, texture.getBuffer(), u, v, texture.getWidth(), texture.getHeight());
    }

    public void setPixel(int x, int y, int color) {
        backingBuffer.putInt(color, y * stride + x * Integer.BYTES);
    }

    public void setPixel(int x, int y, Color color) {
        setPixel(x, y, color.toRGBA());
    }

    public int getPixel(int x, int y) {
        return backingBuffer.getInt(y * stride + x * Integer.BYTES);
    }

    public void fill(Color color) {
        fill(color.toRGBA());
    }

    public void fill(int color) {
        backingBuffer.position(0);
        memSet(backingBuffer.asIntBuffer(), color);
        backingBuffer.clear();
    }
}
