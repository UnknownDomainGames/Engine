package unknowndomain.engine.client.texture;

import de.matthiasmann.twl.utils.PNGDecoder;
import unknowndomain.engine.api.resource.Resource;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.util.DomainedPath;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.WeakHashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureManager {
    private ResourceManager resourceManager;
    private Map<DomainedPath, GLTexture> textures = new WeakHashMap<>();
    private TextureMap textureMap;

    public TextureManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    private void loadTexture0(DomainedPath path, int textureId) throws IOException {
        Resource resource = resourceManager.load(path);
        // Load Texture file
        PNGDecoder decoder = new PNGDecoder(resource.open());

        // Load texture contents into a byte buffer
        ByteBuffer buf = ByteBuffer.allocateDirect(
                4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buf.flip();

        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);//渐远纹理
    }

    public int[] requireTexture(DomainedPath path) {
        try {
            Resource load = resourceManager.load(path);
            InputStream stream = load.open();
            DataInputStream datainputstream = new DataInputStream(stream);

            if (datainputstream.readLong() != -8552249625308161526L)
                throw new IOException("Bad PNG Signature");
            else if (datainputstream.readInt() != 13)
                throw new IOException("Bad length for IHDR chunk!");
            else if (datainputstream.readInt() != 1229472850)
                throw new IOException("Bad type for IHDR chunk!");
            else {
                int w = datainputstream.readInt();
                int h = datainputstream.readInt();

                if (w != h) return null;
                return textureMap.require(w, h);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[2];
    }

    public GLTexture loadTexture(DomainedPath path) {
        GLTexture texture = this.textures.get(path);
        if (texture != null) return texture;
        int id = glGenTextures();
        try {
            loadTexture0(path, id);
            textures.put(path, texture = new GLTexture(id));
        } catch (IOException e) {
            glDeleteTextures(id);
        }
        return texture;
    }

    public GLTexture getTextureMap() {
        return textureMap;
    }

    public GLTexture loadTexture(DomainedPath path, boolean force) {
        if (!force) return loadTexture(path);

        GLTexture texture = this.textures.get(path);
        int id = texture == null ? glGenTextures() : texture.id;

        try {
            loadTexture0(path, id);
            if (texture == null) textures.put(path, texture = new GLTexture(id));
        } catch (IOException e) {
            if (texture == null) glDeleteTextures(id);
        }
        return texture;
    }
}
