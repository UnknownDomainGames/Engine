package nullengine.client.gui.control;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import com.github.mouse0w0.observable.value.ValueChangeListener;
import nullengine.client.gui.Node;
import nullengine.client.gui.image.Image;
import nullengine.client.gui.rendering.ComponentRenderer;
import nullengine.client.gui.rendering.ImageViewRenderer;

public class ImageView extends Node {

    private MutableObjectValue<Image> image;

    private ValueChangeListener<Boolean> loadedChangeListener;

    public ImageView() {
    }

    public ImageView(Image image) {
        setImage(image);
    }

    public MutableObjectValue<Image> image() {
        if (image == null) {
            image = new SimpleMutableObjectValue<>();
            image.addChangeListener((observable, oldValue, newValue) -> {
                if (oldValue != null && oldValue.isBackgroundLoading()) {
                    oldValue.loaded().removeChangeListener(loadedChangeListener);
                }

                if (newValue == null || !newValue.isBackgroundLoading()) {
                    requestParentLayout();
                } else {
                    if (loadedChangeListener == null) {
                        loadedChangeListener = (observable1, oldValue1, newValue1) -> requestParentLayout();
                    }
                    newValue.loaded().addChangeListener(loadedChangeListener);
                }
            });
        }
        return image;
    }

    public Image getImage() {
        return image == null ? null : image.get();
    }

    public void setImage(Image image) {
        image().set(image);
    }

    @Override
    public float prefWidth() {
        return image != null && image.isPresent() ? image.get().getWidth() : 0f;
    }

    @Override
    public float prefHeight() {
        return image != null && image.isPresent() ? image.get().getHeight() : 0f;
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return ImageViewRenderer.INSTANCE;
    }
}
