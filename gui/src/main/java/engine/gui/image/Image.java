package engine.gui.image;

import com.github.mouse0w0.observable.value.*;
import engine.graphics.image.ImageLoader;
import engine.graphics.image.ReadOnlyImage;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public final class Image {

    private final String url;

    private final boolean smooth;
    private final boolean backgroundLoading;

    private MutableIntValue width;
    private MutableIntValue height;

    private MutableBooleanValue loaded;
    private MutableBooleanValue error;
    private MutableObjectValue<Exception> exception;
    private ReadOnlyImage loadedImage;

    public Image(String url) {
        this(url, false, false);
    }

    public Image(String url, boolean smooth) {
        this(url, smooth, false);
    }

    public Image(ReadOnlyImage image, boolean smooth) {
        this.url = "";
        this.smooth = smooth;
        this.backgroundLoading = false;
        finishImage(image);
    }

    public Image(String url, boolean smooth, boolean backgroundLoading) {
        this.url = url;
        this.smooth = smooth;
        this.backgroundLoading = backgroundLoading;
        initialize();
    }

    private void initialize() {
        if (backgroundLoading) {
            CompletableFuture.runAsync(this::loadImage);
        } else {
            loadImage();
        }
    }

    private void loadImage() {
        try {
            finishImage(ImageLoader.instance().loadImage(url));
        } catch (IOException e) {
            finishImage(e);
        }
    }

    private void finishImage(ReadOnlyImage image) {
        loadedImage = image;
        widthImpl().set(image.getWidth());
        heightImpl().set(image.getHeight());
    }

    private void finishImage(Exception e) {
        exceptionImpl().set(e);
    }

    public final String getUrl() {
        return url;
    }

    public boolean isSmooth() {
        return smooth;
    }

    public boolean isBackgroundLoading() {
        return backgroundLoading;
    }

    private MutableIntValue widthImpl() {
        if (width == null) {
            width = new SimpleMutableIntValue();
        }
        return width;
    }

    public ObservableIntValue width() {
        return widthImpl().toUnmodifiable();
    }

    public int getWidth() {
        return width == null ? 0 : width.get();
    }

    private MutableIntValue heightImpl() {
        if (height == null) {
            height = new SimpleMutableIntValue();
        }
        return height;
    }

    public ObservableIntValue height() {
        return heightImpl().toUnmodifiable();
    }

    public int getHeight() {
        return height == null ? 0 : height.get();
    }

    private MutableBooleanValue loadedImpl() {
        if (loaded == null) {
            loaded = new SimpleMutableBooleanValue();
        }
        return loaded;
    }

    public ObservableBooleanValue loaded() {
        return loadedImpl().toUnmodifiable();
    }

    public boolean isLoaded() {
        return loaded != null && loaded.get();
    }

    private MutableBooleanValue errorImpl() {
        if (error == null) {
            error = new SimpleMutableBooleanValue();
        }
        return error;
    }

    public ObservableBooleanValue error() {
        return errorImpl().toUnmodifiable();
    }

    public boolean isError() {
        return error != null && error.get();
    }

    private MutableObjectValue<Exception> exceptionImpl() {
        if (exception == null) {
            exception = new SimpleMutableObjectValue<>();
        }
        return exception;
    }

    public ObservableObjectValue<Exception> exception() {
        return exceptionImpl().toUnmodifiable();
    }

    public Exception getException() {
        return exception == null ? null : exception.get();
    }

    public ReadOnlyImage getLoadedImage() {
        return loadedImage;
    }
}
