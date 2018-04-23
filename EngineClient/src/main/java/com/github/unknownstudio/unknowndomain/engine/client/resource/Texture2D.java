package com.github.unknownstudio.unknowndomain.engine.client.resource;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * 2D Texture
 * 二维纹理
 *
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

    public Texture2D(URI uri){
        this.uri = uri;
    }

    public boolean loadImage(){
        if(texId != -1)return false;
        BufferedImage img = null;
        try {
            img = ImageIO.read(uri.toURL());
            boolean istexenabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
            if(!istexenabled) GL11.glEnable(GL11.GL_TEXTURE_2D);
            texId = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
            int[] data = new int[img.getWidth() * img.getHeight()];
            img.getRGB(0,0,img.getWidth(), img.getHeight(), data, 0, img.getWidth());
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, img.getWidth(), img.getHeight(), 0, GL11.GL_RGBA, GL11.GL_RGB, data);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            if(!istexenabled) GL11.glDisable(GL11.GL_TEXTURE_2D);
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if (img != null)
                img.flush();
        }
    }

    public void useTexture(){
        if (texId != -1){
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        }
    }

    public void unloadImage(){
        if (texId != -1){
            GL11.glDeleteTextures(texId);
            texId = -1;
        }
    }

}
