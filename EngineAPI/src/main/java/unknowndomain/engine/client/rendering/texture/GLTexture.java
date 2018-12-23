package unknowndomain.engine.client.rendering.texture;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GLTexture {

    private final int id;
    private final int width, height;

    public GLTexture(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public void bind() {
        // glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public static GLTexture of(int width, int height, ByteBuffer buf) {
        if (!buf.isDirect()) {
            ByteBuffer direct = ByteBuffer.allocateDirect(buf.capacity());
            direct.put(buf);
            buf = direct;
        }
        GLTexture glTexture = new GLTexture(glGenTextures(), width, height);
        glBindTexture(GL_TEXTURE_2D, glTexture.id);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

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

    public static GLTexture ofPNG(InputStream stream) throws IOException {
        PNGDecoder decoder = new PNGDecoder(stream);
        ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buf.flip();

        return of(decoder.getWidth(), decoder.getHeight(), buf);
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
