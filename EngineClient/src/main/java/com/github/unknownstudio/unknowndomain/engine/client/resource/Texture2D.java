package com.github.unknownstudio.unknowndomain.engine.client.resource;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
public class Texture2D {

    public final URI uri;

    private int texId = -1;

    public Texture2D(File file) {
        this(file.toURI());
    }

    public Texture2D(URI uri) {
        this.uri = uri;
    }

    public boolean loadImage() {
        if (texId != -1) return false;
        BufferedImage img = null;
        try {
            img = ImageIO.read(uri.toURL());
            boolean istexenabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
            if (!istexenabled) GL11.glEnable(GL11.GL_TEXTURE_2D);
            texId = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
            int[] data = new int[img.getWidth() * img.getHeight()];
            img.getRGB(0, 0, img.getWidth(), img.getHeight(), data, 0, img.getWidth());

            ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);
            boolean isAlpha = img.getType() == BufferedImage.TYPE_4BYTE_ABGR ||
                    img.getType() == BufferedImage.TYPE_4BYTE_ABGR_PRE ||
                    img.getType() == BufferedImage.TYPE_INT_ARGB ||
                    img.getType() == BufferedImage.TYPE_INT_ARGB_PRE;

            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int pixel = data[y * img.getWidth() + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                    buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                    buffer.put((byte) (pixel & 0xFF));               // Blue component
                    if (isAlpha)
                        buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
                    else {
                        if (img.getType() == BufferedImage.TYPE_BYTE_INDEXED) {
                            if (pixel == img.getColorModel().getRGB(((IndexColorModel) img.getColorModel()).getTransparentPixel())) {
                                buffer.put((byte) 0);
                            }else{
                                buffer.put((byte) 255);
                            }
                        } else {
                            buffer.put((byte) 255);
                        }
                    }
                }
            }
            buffer.flip();

            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, img.getWidth(), img.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);


            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                    GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                    GL11.GL_LINEAR_MIPMAP_LINEAR);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            if (!istexenabled) GL11.glDisable(GL11.GL_TEXTURE_2D);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (img != null)
                img.flush();
        }
    }

    public void useTexture() {
        if (texId != -1) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        }
    }

    public void unloadImage() {
        if (texId != -1) {
            GL11.glDeleteTextures(texId);
            texId = -1;
        }
    }

}
