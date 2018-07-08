package unknowndomain.engine.client.resource;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * 2D Texture
 * 二维纹理
 * <p>
 * 2d texture class for opengl
 *
 * @author cvrunmin
 * @since 1.0
 */
public class FileTexture2D extends Texture2D {

    public final URI uri;

    public FileTexture2D(File file) {
        this(file.toURI());
    }

    public FileTexture2D(URI uri) {
        this.uri = uri;
    }

    @Override
    public boolean loadImage() {
        if (texId != -1) return false;
        BufferedImage img = null;
        try {
            img = ImageIO.read(uri.toURL());
            loadImage(img);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (img != null)
                img.flush();
        }
    }

}
