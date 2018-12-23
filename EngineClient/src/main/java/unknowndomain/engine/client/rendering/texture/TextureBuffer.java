package unknowndomain.engine.client.rendering.texture;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TextureBuffer {

    public static TextureBuffer create(PNGDecoder decoder) throws IOException {
        TextureBuffer textureBuffer = new TextureBuffer(decoder.getWidth(), decoder.getHeight());
        ByteBuffer buf = textureBuffer.getBuffer();
        decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buf.flip();
        return textureBuffer;
    }

    private int width, height;
    private int stride;
    private ByteBuffer backingBuffer;

    public TextureBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        initBuffer();
    }

    protected void initBuffer() {
        stride = 4 * width;
        backingBuffer = ByteBuffer.allocateDirect(4 * width * height);
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

    public void setTexture(int x, int y, ByteBuffer buffer, int width, int height) {
        for (int i = 0; i < height; i++) {
            backingBuffer.position((y + i) * this.stride + x * 4);
            for (int j = 0; j < width; j++) {
                backingBuffer.putInt(buffer.getInt());
            }
        }
        backingBuffer.clear();
    }

    public void setTexture(int x, int y, TextureBuffer texture) {
        setTexture(x, y, texture.getBuffer(), texture.getWidth(), texture.getHeight());
    }
}
