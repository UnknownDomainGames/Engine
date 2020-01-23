package nullengine.client.gui.internal.impl.gl;

import nullengine.client.gui.image.Image;
import nullengine.client.rendering.RenderEngine;
import nullengine.client.rendering.image.ReadOnlyImage;
import nullengine.client.rendering.management.ResourceFactory;
import nullengine.client.rendering.texture.FilterMode;
import nullengine.client.rendering.texture.Texture2D;

import java.util.Map;
import java.util.WeakHashMap;

public class GUIResourceFactory {

    private final Map<Image, Texture2D> textures = new WeakHashMap<>();

    private final ResourceFactory resourceFactory = RenderEngine.getManager().getResourceFactory();

    public Texture2D getTexture(Image image) {
        Texture2D texture = textures.get(image);
        if (texture != null) return texture;

        ReadOnlyImage loadedImage = image.getLoadedImage();
        if (loadedImage == null) return null;

        Texture2D.Builder builder = resourceFactory.createTexture2DBuilder();
        if (image.isSmooth()) {
            builder.magFilter(FilterMode.LINEAR);
            builder.minFilter(FilterMode.LINEAR);
        }
        texture = builder.build(loadedImage);
        textures.put(image, texture);
        return texture;
    }
}
