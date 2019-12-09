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
    private ByteBuffer pixelBuffer;

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
            ByteBuffer pixelBuffer = STBImage.stbi_load_from_memory(buffer, w, h, c, 4);
            int width = w.get(0);
            int height = h.get(0);
            if (pixelBuffer == null) {
                throw new IOException("File buffer cannot be load as pixel buffer by STBImage");
            }
            var tex = new BufferedImage(width, height);
            tex.getPixelBuffer().put(pixelBuffer);
            tex.getPixelBuffer().flip();
            pixelBuffer.clear();
            return tex;
        }
    }

    public BufferedImage(int size) {
        this(size, size);
    }

    public BufferedImage(int width, int height) {
        this.width = width;
        this.height = height;
        initPixelBuffer();
    }

    public BufferedImage(int width, int height, int initColor) {
        this(width, height);
        fill(initColor);
    }

    public BufferedImage(int width, int height, ByteBuffer buffer) {
        this.width = width;
        this.height = height;
        this.pixelBuffer = buffer;
    }

    protected void initPixelBuffer() {
        stride = Integer.BYTES * width;
        pixelBuffer = ByteBuffer.allocateDirect(stride * height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getPixelBuffer() {
        return pixelBuffer;
    }

    public void setTexture(int x, int y, ByteBuffer pixelBuffer, int u, int v, int width, int height) {
        int bufferStride = width * Integer.BYTES;
        long srcAddress = memAddress(pixelBuffer, 0);
        long dstAddress = memAddress(this.pixelBuffer, 0);
        for (int i = 0; i < height; i++) {
            memCopy(srcAddress + (y + i) * this.stride + x * Integer.BYTES,
                    dstAddress + (u + i) * bufferStride + v * Integer.BYTES,
                    width * Integer.BYTES);
        }
        this.pixelBuffer.clear();
    }

    public void setTexture(int x, int y, ByteBuffer buffer, int width, int height) {
        setTexture(x, y, buffer, 0, 0, width, height);
    }

    public void setTexture(int x, int y, BufferedImage image) {
        setTexture(x, y, image.getPixelBuffer(), image.getWidth(), image.getHeight());
    }

    public void setTexture(int x, int y, BufferedImage image, int u, int v) {
        setTexture(x, y, image.getPixelBuffer(), u, v, image.getWidth(), image.getHeight());
    }

    public void setPixel(int x, int y, int color) {
        pixelBuffer.putInt(color, y * stride + x * Integer.BYTES);
    }

    public void setPixel(int x, int y, Color color) {
        setPixel(x, y, color.toRGBA());
    }

    public int getPixel(int x, int y) {
        return pixelBuffer.getInt(y * stride + x * Integer.BYTES);
    }

    public void fill(Color color) {
        fill(color.toRGBA());
    }

    public void fill(int color) {
        pixelBuffer.position(0);
        memSet(pixelBuffer.asIntBuffer(), color);
        pixelBuffer.clear();
    }
}
