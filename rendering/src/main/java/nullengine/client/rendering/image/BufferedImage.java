package nullengine.client.rendering.image;

import nullengine.util.Color;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryUtil.*;

public class BufferedImage {

    private final int width;
    private final int height;
    private final int stride;
    private final ByteBuffer pixelBuffer;

    public static BufferedImage create(String url) throws IOException {
        return new BufferedImage(ImageHelper.instance().loadImage(url));
    }

    public static BufferedImage create(InputStream input) throws IOException {
        return new BufferedImage(ImageHelper.instance().loadImage(input));
    }

    public static BufferedImage create(ByteBuffer bytes) throws IOException {
        return new BufferedImage(ImageHelper.instance().loadImage(bytes));
    }

    public BufferedImage(int size) {
        this(size, size);
    }

    public BufferedImage(int width, int height) {
        this(ByteBuffer.allocateDirect(Integer.BYTES * width * height), width, height);
    }

    public BufferedImage(int width, int height, int initColor) {
        this(width, height);
        fill(initColor);
    }

    public BufferedImage(ByteBuffer pixelBuffer, int width, int height) {
        this.width = width;
        this.height = height;
        this.stride = Integer.BYTES * width;
        if (!pixelBuffer.isDirect()) {
            this.pixelBuffer = ByteBuffer.allocateDirect(pixelBuffer.capacity()).put(pixelBuffer).flip();
        } else {
            this.pixelBuffer = pixelBuffer;
        }
    }

    public BufferedImage(BufferedImage image) {
        this.width = image.width;
        this.height = image.height;
        this.stride = image.stride;
        this.pixelBuffer = ByteBuffer.allocateDirect(image.pixelBuffer.capacity()).put(image.pixelBuffer).flip();
    }

    public BufferedImage(LoadedImage image) {
        this(image.getPixelBuffer(), image.getWidth(), image.getHeight());
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
