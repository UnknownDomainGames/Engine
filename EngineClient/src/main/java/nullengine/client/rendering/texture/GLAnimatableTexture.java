package nullengine.client.rendering.texture;

import org.apache.commons.io.IOUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GLAnimatableTexture extends GLTexture {

    private int width;
    private int height;
    private int layers;

    public GLAnimatableTexture(int id) {
        super(id, 0, 0);
    }


    public static GLAnimatableTexture ofGIF(InputStream stream) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(IOUtils.toByteArray(stream));
        ByteBuffer tmp = ByteBuffer.allocateDirect(buffer.capacity());
        tmp.put(buffer);
        buffer.clear();
        buffer = tmp;
        tmp.clear();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pb = stack.mallocPointer(1);
            IntBuffer x, y, z, channel;
            x = stack.mallocInt(1);
            y = stack.mallocInt(1);
            z = stack.mallocInt(1);
            channel = stack.mallocInt(1);
            ByteBuffer buf = STBImage.stbi_load_gif_from_memory(buffer, pb, x, y, z, channel, 0);

            if (!buf.isDirect()) {
                ByteBuffer direct = ByteBuffer.allocateDirect(buf.capacity());
                direct.put(buf);
                buf = direct;
            }
            GLAnimatableTexture glTexture = new GLAnimatableTexture(glGenTextures());
            glBindTexture(GL_TEXTURE_2D, glTexture.getId());
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            int maxSize = glGetInteger(GL_MAX_TEXTURE_SIZE);
            int width = x.get(0);
            int height = y.get(0);
            int layers = z.get(0);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height * layers, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

            glGenerateMipmap(GL_TEXTURE_2D);
            glTexture.width = width;
            glTexture.height = height;
            glTexture.layers = layers;
            return glTexture;
        }
    }


    public int getWidth() {
        return width;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    protected void setHeight(int height) {
        this.height = height;
    }

    public int getLayers() {
        return layers;
    }

    protected void setLayers(int layers) {
        this.layers = layers;
    }
}