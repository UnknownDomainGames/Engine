package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.internal.Internal;
import unknowndomain.engine.client.gui.rendering.ComponentRenderer;
import unknowndomain.engine.client.gui.rendering.ImageRenderer;
import unknowndomain.engine.client.rendering.texture.GLTexture;
import unknowndomain.engine.client.resource.ResourcePath;

public class Image extends Component {
    private final SimpleMutableObjectValue<ResourcePath> image = new SimpleMutableObjectValue<>();
    private final SimpleMutableFloatValue imgX = new SimpleMutableFloatValue();
    private final SimpleMutableFloatValue imgY = new SimpleMutableFloatValue();
    private final SimpleMutableFloatValue imgWidth = new SimpleMutableFloatValue();
    private final SimpleMutableFloatValue imgHeight = new SimpleMutableFloatValue();
    private ResourcePath cachedPath;
    private GLTexture cachedTexture;

    public Image() {
        image.addChangeListener((ob, o, n) -> {
            buildCache();
            requestParentLayout();
        });
        imgX.addChangeListener((ob, o, n) -> requestParentLayout());
        imgY.addChangeListener((ob, o, n) -> requestParentLayout());
        imgWidth.addChangeListener((ob, o, n) -> requestParentLayout());
        imgHeight.addChangeListener((ob, o, n) -> requestParentLayout());
    }

    public Image(ResourcePath path) {
        this();
        image.setValue(path);
    }

    public void buildCache() {
        cachedPath = image.getValue();
        cachedTexture = Internal.getContext().getImageHelper().getTexture(cachedPath);
        if (cachedTexture != null) {
            imgWidth.set(cachedTexture.getWidth());
            imgHeight.set(cachedTexture.getHeight());
        }
    }

    @Override
    public float prefWidth() {
        return cachedTexture != null ? imgWidth.get() : 0;
    }

    @Override
    public float prefHeight() {
        return cachedTexture != null ? imgHeight.get() : 0;
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return ImageRenderer.INSTANCE;
    }

    public GLTexture getCachedTexture() {
        return cachedTexture;
    }

    public SimpleMutableFloatValue imageX() {
        return imgX;
    }

    public SimpleMutableFloatValue imageY() {
        return imgY;
    }

    public SimpleMutableFloatValue imageWidth() {
        return imgWidth;
    }

    public SimpleMutableFloatValue imageHeight() {
        return imgHeight;
    }
}
