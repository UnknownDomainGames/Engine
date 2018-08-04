package unknowndomain.engine.client.resource;

import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.client.util.GLHelper;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class Texture2D extends RegistryEntry.Impl<Texture2D> {
    protected int texId = -1;

    private BufferedImage img;

    public Texture2D(){}

    public Texture2D(BufferedImage image){
        setImage(image);
    }

    public void setImage(BufferedImage image){
        unloadImage();
        img = image;
        loadImage();
    }

    public boolean isLoaded(){
        return texId != -1;
    }

    public boolean loadImage(){
        if(img != null){
        loadImage(img);
        return true;
        }
        return false;
    }

    protected void loadImage(BufferedImage img) {
        boolean istexenabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        if (!istexenabled) GL11.glEnable(GL11.GL_TEXTURE_2D);
        texId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        ByteBuffer buffer = GLHelper.getByteBufferFromImage(img);

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, img.getWidth(), img.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        enableRepeatingX(true);
        enableRepeatingY(true);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR_MIPMAP_LINEAR);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        if (!istexenabled) GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public Texture2D enableRepeatingX(boolean flag){
        useTexture();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, flag ? GL11.GL_REPEAT : GL11.GL_CLAMP);
        unuseTexture();
        return this;
    }

    public Texture2D enableRepeatingY(boolean flag){
        useTexture();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, flag ? GL11.GL_REPEAT : GL11.GL_CLAMP);
        unuseTexture();
        return this;
    }

    public void useTexture() {
        if (texId != -1) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        }
    }

    public static void unuseTexture(){
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void unloadImage() {
        if (texId != -1) {
            GL11.glDeleteTextures(texId);
            texId = -1;
        }
    }
}
