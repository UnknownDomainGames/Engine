package unknowndomain.engine.client.rendering.texture;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

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
        MemoryStack.stackPush();
        IntBuffer w = MemoryUtil.memAllocInt(1);
        IntBuffer h = MemoryUtil.memAllocInt(1);
        IntBuffer c = MemoryUtil.memAllocInt(1);
        ByteBuffer pixelbuf = STBImage.stbi_load_from_memory(filebuf,w,h,c,4);
        int width = w.get(0);
        int height = h.get(0);
        MemoryStack.stackPop();
        if(pixelbuf == null){
            throw new IOException("File buffer cannot be load as pixel buffer by STBImage");
        }
        var tex = new TextureBuffer(width,height);
        tex.getBuffer().put(pixelbuf);
        tex.getBuffer().flip();
        pixelbuf.clear();
        return tex;
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
