package nullengine.client.gui.internal.impl.gl;

import nullengine.client.gui.image.Image;
import nullengine.client.rendering.gl.texture.FilterMode;
import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.client.rendering.image.ReadOnlyImage;
import nullengine.client.rendering.texture.Texture2D;

import java.util.Map;
import java.util.WeakHashMap;

public class GLResourceFactory {

    private final Map<Image, Texture2D> textures = new WeakHashMap<>();

    public Texture2D getTexture(Image image) {
        Texture2D texture = textures.get(image);
        if (texture != null) return texture;

        ReadOnlyImage loadedImage = image.getLoadedImage();
        if (loadedImage == null) return null;

        GLTexture2D.Builder builder = GLTexture2D.builder();
        if (image.isSmooth()) {
            builder.magFilter(FilterMode.LINEAR);
            builder.minFilter(FilterMode.LINEAR);
        }
        texture = builder.build(loadedImage.getPixelBuffer(), loadedImage.getWidth(), loadedImage.getHeight());
        textures.put(image, texture);
        return texture;
    }
}
