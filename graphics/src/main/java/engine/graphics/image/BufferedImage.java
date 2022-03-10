package engine.graphics.image;

import engine.util.Color;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static java.nio.ByteBuffer.allocateDirect;
import static org.lwjgl.system.MemoryUtil.*;

public class BufferedImage implements WritableImage {

    private final int width;
    private final int height;
    private final int stride;
    private final ByteBuffer pixelBuffer;
    private final ByteBuffer readOnlyBuffer;
    private final long address;

    public static BufferedImage load(String url) throws IOException {
        return ImageLoader.instance().loadWritableImage(url);
    }

    public static BufferedImage load(InputStream input) throws IOException {
        return ImageLoader.instance().loadWritableImage(input);
    }

    public static BufferedImage load(ByteBuffer bytes) throws IOException {
        return ImageLoader.instance().loadWritableImage(bytes);
    }

    public static BufferedImage wrap(ByteBuffer pixelBuffer, int width, int height) {
        return new BufferedImage(pixelBuffer, width, height, pixelBuffer.isReadOnly() || !pixelBuffer.isDirect());
    }

    public static BufferedImage resize(BufferedImage src, int size) {
        return resize(src, size, size);
    }

    public static BufferedImage resize(BufferedImage src, int width, int height) {
        BufferedImage image = new BufferedImage(width, height);
        image.setImage(0, 0, src);
        return image;
    }

    public BufferedImage(int size) {
        this(size, size);
    }

    public BufferedImage(int width, int height) {
        this(allocateDirect(Integer.BYTES * width * height), width, height, false);
    }

    public BufferedImage(int width, int height, Color fillColor) {
        this(width, height);
        fill(fillColor);
    }

    public BufferedImage(int width, int height, int fillColorRGBA) {
        this(width, height);
        fill(fillColorRGBA);
    }

    public BufferedImage(ReadOnlyImage image) {
        this(image.getPixelBuffer(), image.getWidth(), image.getHeight(), true);
    }

    public BufferedImage(WritableImage image) {
        this(image.getWritablePixelBuffer(), image.getWidth(), image.getHeight(), true);
    }

    private BufferedImage(ByteBuffer pixelBuffer, int width, int height, boolean copy) {
        this.width = width;
        this.height = height;
        this.stride = width << 2;
        this.pixelBuffer = copy ? allocateDirect(pixelBuffer.capacity()).put(pixelBuffer).flip() : pixelBuffer;
        this.readOnlyBuffer = pixelBuffer.asReadOnlyBuffer();
        this.address = memAddress0(this.pixelBuffer);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public ByteBuffer getPixelBuffer() {
        return readOnlyBuffer;
    }

    @Override
    public ByteBuffer getWritablePixelBuffer() {
        return pixelBuffer;
    }

    @Override
    public int getPixel(int x, int y) {
        return pixelBuffer.getInt(y * stride + (x << 2));
    }

    @Override
    public void setPixel(int x, int y, int rgba) {
        pixelBuffer.putInt(y * stride + (x << 2), rgba);
    }

    @Override
    public void setPixel(int x, int y, int width, int height, int rgba) {
        for (int i = y, maxY = y + height; i < maxY; i++) {
            memSet(address + i * stride + (x << 2), rgba, width << 2);
        }
    }

    @Override
    public void setImage(int x, int y, ByteBuffer src, int srcWidth, int srcHeight, int srcMinX, int srcMinY, int srcMaxX, int srcMaxY) {
        final int srcOffset = srcMinX << 2;
        final int dstOffset = x << 2;
        final long srcAddress = memAddress0(src) + srcOffset;
        final long dstAddress = address + dstOffset;
        final int srcStride = srcWidth << 2;
        final int bytes = (srcMaxX - srcMinX) << 2;
        for (int srcY = srcMinY, dstY = y; srcY < srcMaxY; srcY++, dstY++) {
            memCopy(srcAddress + srcY * srcStride, dstAddress + dstY * stride, bytes);
        }
    }

    @Override
    public void fill(int rgba) {
        pixelBuffer.clear();
        memSet(pixelBuffer.asIntBuffer(), rgba);
    }
}
