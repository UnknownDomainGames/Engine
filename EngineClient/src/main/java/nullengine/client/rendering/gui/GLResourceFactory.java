package nullengine.client.rendering.gui;

import nullengine.client.gui.image.Image;
import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.client.rendering.image.LoadedImage;
import nullengine.client.rendering.texture.Texture2D;

import java.util.Map;
import java.util.WeakHashMap;

public class GLResourceFactory {

    // FIXME: gl memory leak
    private final Map<Image, Texture2D> textures = new WeakHashMap<>();

    public Texture2D getTexture(Image image) {
        Texture2D texture = textures.get(image);
        if (texture != null) return texture;

        LoadedImage loadedImage = image.getLoadedImage();
        if (loadedImage == null) return null;

        texture = GLTexture2D.of(loadedImage.getPixelBuffer(), loadedImage.getWidth(), loadedImage.getHeight());
        textures.put(image, texture);
        return texture;
    }
}
