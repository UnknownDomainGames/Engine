package engine.gui.internal.impl.graphics;

import engine.graphics.GraphicsEngine;
import engine.graphics.image.ReadOnlyImage;
import engine.graphics.management.ResourceFactory;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Texture2D;
import engine.gui.image.Image;

import java.util.Map;
import java.util.WeakHashMap;

public final class GUIResourceFactory {

    private final Map<Image, Texture2D> textures = new WeakHashMap<>();

    private final ResourceFactory resourceFactory = GraphicsEngine.getGraphicsBackend().getResourceFactory();

    public Texture2D getTexture(Image image) {
        Texture2D texture = textures.get(image);
        if (texture != null) return texture;

        ReadOnlyImage loadedImage = image.getLoadedImage();
        if (loadedImage == null) return null;

        Texture2D.Builder builder = resourceFactory.createTexture2DBuilder();
        if (image.isSmooth()) {
            builder.magFilter(FilterMode.LINEAR).minFilter(FilterMode.LINEAR);
        }
        texture = builder.build(loadedImage);
        textures.put(image, texture);
        return texture;
    }
}
