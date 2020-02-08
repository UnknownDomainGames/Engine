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
        return ImageHelper.instance().loadWritableImage(url);
    }

    public static BufferedImage load(InputStream input) throws IOException {
        return ImageHelper.instance().loadWritableImage(input);
    }

    public static BufferedImage load(ByteBuffer bytes) throws IOException {
        return ImageHelper.instance().loadWritableImage(bytes);
    }

    public static BufferedImage wrap(ByteBuffer pixelBuffer, int width, int height) {
        return new BufferedImage(pixelBuffer, width, height, false);
    }

    public static BufferedImage wrapDirect(ByteBuffer pixelBuffer, int width, int height) {
        return new BufferedImage(pixelBuffer, width, height, pixelBuffer.isReadOnly() || !pixelBuffer.isDirect());
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
        this.stride = Integer.BYTES * width;
        this.pixelBuffer = copy ? allocateDirect(pixelBuffer.capacity()).put(pixelBuffer).flip() : pixelBuffer;
        this.readOnlyBuffer = pixelBuffer.asReadOnlyBuffer();
        this.address = memAddress(this.pixelBuffer);
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
        return pixelBuffer.getInt(y * stride + x * Integer.BYTES);
    }

    @Override
    public void setPixel(int x, int y, int rgba) {
        pixelBuffer.putInt(rgba, y * stride + x * Integer.BYTES);
    }

    @Override
    public void setPixel(int x, int y, int width, int height, int rgba) {
        for (int i = 0; i < height; i++) {
            memSet(address + (y + i) * stride + x * Integer.BYTES, rgba, width * Integer.BYTES);
        }
    }

    @Override
    public void setImage(int x, int y, ByteBuffer pixelBuffer, int width, int height, int u, int v) {
        int srcStride = width * Integer.BYTES;
        long srcAddress = memAddress(pixelBuffer, 0);
        for (int i = 0; i < height; i++) {
            memCopy(srcAddress + (u + i) * srcStride + v * Integer.BYTES,
                    address + (y + i) * this.stride + x * Integer.BYTES,
                    width * Integer.BYTES);
        }
        this.pixelBuffer.clear();
    }

    @Override
    public void fill(int rgba) {
        pixelBuffer.position(0);
        memSet(pixelBuffer.asIntBuffer(), rgba);
        pixelBuffer.clear();
    }
}
