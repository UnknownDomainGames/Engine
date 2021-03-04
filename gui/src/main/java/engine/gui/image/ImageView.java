package engine.gui.image;

import com.github.mouse0w0.observable.value.*;
import engine.gui.Node;
import engine.gui.graphics.NodeRenderer;
import engine.gui.graphics.control.ImageViewRenderer;

public class ImageView extends Node {

    private MutableObjectValue<Image> image;

    private ValueChangeListener<Boolean> loadedChangeListener;

    private MutableFloatValue customWidth = new SimpleMutableFloatValue();
    private MutableFloatValue customHeight = new SimpleMutableFloatValue();

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
        return image != null && image.isPresent() ? (customWidth.get() != 0 ? customWidth.get() : image.get().getWidth()) : 0f;
    }

    @Override
    public float prefHeight() {
        return image != null && image.isPresent() ? (customHeight.get() != 0 ? customHeight.get() : image.get().getHeight()) : 0f;
    }

    @Override
    protected NodeRenderer createDefaultRenderer() {
        return ImageViewRenderer.INSTANCE;
    }

    public MutableFloatValue getCustomWidth() {
        return customWidth;
    }

    public MutableFloatValue getCustomHeight() {
        return customHeight;
    }
}
