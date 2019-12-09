package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.asset.AssetURL;
import nullengine.client.gui.Node;
import nullengine.client.gui.internal.Internal;
import nullengine.client.gui.rendering.ComponentRenderer;
import nullengine.client.gui.rendering.ImageRenderer;
import nullengine.client.rendering.texture.Texture2D;

public class ImageView extends Node {
    private final SimpleMutableObjectValue<AssetURL> image = new SimpleMutableObjectValue<>();
    private final SimpleMutableFloatValue imageX = new SimpleMutableFloatValue();
    private final SimpleMutableFloatValue imageY = new SimpleMutableFloatValue();
    private final SimpleMutableFloatValue imageWidth = new SimpleMutableFloatValue();
    private final SimpleMutableFloatValue imageHeight = new SimpleMutableFloatValue();
    private Texture2D cachedTexture;

    public ImageView() {
        image.addChangeListener((ob, o, n) -> {
            buildCache();
            requestParentLayout();
        });
        imageX.addChangeListener((ob, o, n) -> requestParentLayout());
        imageY.addChangeListener((ob, o, n) -> requestParentLayout());
        imageWidth.addChangeListener((ob, o, n) -> requestParentLayout());
        imageHeight.addChangeListener((ob, o, n) -> requestParentLayout());
    }

    public ImageView(AssetURL path) {
        this();
        image.setValue(path);
    }

    public void buildCache() {
        cachedTexture = Internal.getContext().getImageHelper().getTexture(image.getValue());
        if (cachedTexture != null) {
            imageWidth.set(cachedTexture.getWidth());
            imageHeight.set(cachedTexture.getHeight());
        }
    }

    @Override
    public float prefWidth() {
        return cachedTexture != null ? imageWidth.get() : 0;
    }

    @Override
    public float prefHeight() {
        return cachedTexture != null ? imageHeight.get() : 0;
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return ImageRenderer.INSTANCE;
    }

    public Texture2D getCachedTexture() {
        return cachedTexture;
    }

    public SimpleMutableFloatValue imageX() {
        return imageX;
    }

    public SimpleMutableFloatValue imageY() {
        return imageY;
    }

    public SimpleMutableFloatValue imageWidth() {
        return imageWidth;
    }

    public SimpleMutableFloatValue imageHeight() {
        return imageHeight;
    }

    public SimpleMutableObjectValue<AssetURL> image() {
        return image;
    }

}
