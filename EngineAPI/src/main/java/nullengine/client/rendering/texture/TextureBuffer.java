package nullengine.client.rendering.texture;

import nullengine.util.Color;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureBuffer {

    public static TextureBuffer create(ByteBuffer filebuf) throws IOException {
        if (!filebuf.isDirect()) {
            ByteBuffer direct = ByteBuffer.allocateDirect(filebuf.capacity());
            direct.put(filebuf);
            direct.flip();
            filebuf = direct;
        }
        try (var stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);
            ByteBuffer pixelbuf = STBImage.stbi_load_from_memory(filebuf, w, h, c, 4);
            int width = w.get(0);
            int height = h.get(0);
            if (pixelbuf == null) {
                throw new IOException("File buffer cannot be load as pixel buffer by STBImage");
            }
            var tex = new TextureBuffer(width, height);
            tex.getBuffer().put(pixelbuf);
            tex.getBuffer().flip();
            pixelbuf.clear();
            return tex;
        }
    }

    private int width, height;
    private int stride;
    private ByteBuffer backingBuffer;

    public TextureBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        initBuffer();
    }

    public TextureBuffer(int width, int height, int initColor) {
        this.width = width;
        this.height = height;
        initBuffer();
        fill(initColor);
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

    public void setTexture(int x, int y, ByteBuffer buffer, int bufferWidth, int bufferHeight, int u, int v, int regionWidth, int regionHeight) {
        int bufferStride = bufferWidth * Integer.BYTES;
        for (int i = 0; i < regionHeight; i++) {
            backingBuffer.position((y + i) * this.stride + x * Integer.BYTES);
            buffer.position((u + i) * bufferStride + v * Integer.BYTES);
            for (int j = 0; j < regionWidth; j++) {
                backingBuffer.putInt(buffer.getInt());
            }
        }
        backingBuffer.clear();
    }

    public void setTexture(int x, int y, ByteBuffer buffer, int width, int height) {
        setTexture(x, y, buffer, width, height, 0, 0, width, height);
    }

    public void setTexture(int x, int y, TextureBuffer texture) {
        setTexture(x, y, texture.getBuffer(), texture.getWidth(), texture.getHeight());
    }

    public void setTexture(int x, int y, TextureBuffer texture, int u, int v, int regionWidth, int regionHeight) {
        setTexture(x, y, texture.getBuffer(), texture.getWidth(), texture.getHeight(), u, v, regionWidth, regionHeight);
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

    public void fill(int color) {
        for (int i = 0; i < width * height; i++) {
            backingBuffer.putInt(color);
        }
        backingBuffer.clear();
    }
}
