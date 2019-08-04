package nullengine.client.rendering.texture;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.GL_SRGB_ALPHA;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GLTexture {

    public static final GLTexture EMPTY = new GLTexture(0, 0, 0);

    private final int id;
    private final int width, height;

    protected GLTexture(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public void bind() {
        // glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public static GLTexture of(ByteBuffer filebuf) throws IOException {
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
        ByteBuffer pixelbuf = STBImage.stbi_load_from_memory(filebuf, w, h, c, 4);
        int width = w.get(0);
        int height = h.get(0);
        MemoryStack.stackPop();
        if (pixelbuf == null) {
            throw new IOException("File buffer cannot be loadDirect as pixel buffer by STBImage");
        }
        pixelbuf.flip();
        return of(width, height, pixelbuf);
    }

    public static GLTexture of(int width, int height, ByteBuffer pixelbuf) {
        if (!pixelbuf.isDirect()) {
            ByteBuffer direct = ByteBuffer.allocateDirect(pixelbuf.capacity());
            direct.put(pixelbuf);
            pixelbuf = direct;
        }
        GLTexture glTexture = new GLTexture(glGenTextures(), width, height);
        glBindTexture(GL_TEXTURE_2D, glTexture.id);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_SRGB_ALPHA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelbuf);
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelbuf);

        glGenerateMipmap(GL_TEXTURE_2D);
        return glTexture;
    }

    public void dispose() {
        glDeleteTextures(id);
    }

    @Override
    public String toString() {
        return "GLTexture { id: " + id + " }";
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }
}
