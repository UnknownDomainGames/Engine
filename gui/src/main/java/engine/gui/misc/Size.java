package engine.gui.misc;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;

public final class Size {
    public static final float USE_COMPUTED_VALUE = -1;
    public static final float USE_PERF_VALUE = -2;

    private MutableFloatValue minWidth;
    private MutableFloatValue minHeight;
    private MutableFloatValue prefWidth;
    private MutableFloatValue prefHeight;
    private MutableFloatValue maxWidth;
    private MutableFloatValue maxHeight;

    public MutableFloatValue minWidth() {
        if (minWidth == null) {
            minWidth = new SimpleMutableFloatValue(USE_COMPUTED_VALUE);
        }
        return minWidth;
    }

    public float getMinWidth() {
        return minWidth == null ? USE_COMPUTED_VALUE : minWidth.get();
    }

    public void setMinWidth(float minWidth) {
        minWidth().set(minWidth);
    }

    public MutableFloatValue minHeight() {
        if (minHeight == null) {
            minHeight = new SimpleMutableFloatValue(USE_COMPUTED_VALUE);
        }
        return minHeight;
    }

    public float getMinHeight() {
        return minHeight == null ? USE_COMPUTED_VALUE : minHeight.get();
    }

    public void setMinHeight(float minHeight) {
        minHeight().set(minHeight);
    }

    public void setMinSize(float width, float height) {
        setMinWidth(width);
        setMinHeight(height);
    }

    public MutableFloatValue prefWidth() {
        if (prefWidth == null) {
            prefWidth = new SimpleMutableFloatValue(USE_COMPUTED_VALUE);
        }
        return prefWidth;
    }

    public float getPrefWidth() {
        return prefWidth == null ? USE_COMPUTED_VALUE : prefWidth.get();
    }

    public void setPrefWidth(float prefWidth) {
        prefWidth().set(prefWidth);
    }

    public MutableFloatValue prefHeight() {
        if (prefHeight == null) {
            prefHeight = new SimpleMutableFloatValue(USE_COMPUTED_VALUE);
        }
        return prefHeight;
    }

    public float getPrefHeight() {
        return prefHeight == null ? USE_COMPUTED_VALUE : prefHeight.get();
    }

    public void setPrefHeight(float prefHeight) {
        prefHeight().set(prefHeight);
    }

    public void setPrefSize(float width, float height) {
        setPrefWidth(width);
        setPrefHeight(height);
    }

    public MutableFloatValue maxWidth() {
        if (maxWidth == null) {
            maxWidth = new SimpleMutableFloatValue(USE_COMPUTED_VALUE);
        }
        return maxWidth;
    }

    public float getMaxWidth() {
        return maxWidth == null ? USE_COMPUTED_VALUE : maxWidth.get();
    }

    public void setMaxWidth(float maxWidth) {
        maxWidth().set(maxWidth);
    }

    public MutableFloatValue maxHeight() {
        if (maxHeight == null) {
            maxHeight = new SimpleMutableFloatValue(USE_COMPUTED_VALUE);
        }
        return maxHeight;
    }

    public float getMaxHeight() {
        return maxHeight == null ? USE_COMPUTED_VALUE : maxHeight.get();
    }

    public void setMaxHeight(float maxHeight) {
        maxHeight().set(maxHeight);
    }

    public void setMaxSize(float width, float height) {
        setMaxWidth(width);
        setMaxHeight(height);
    }
}
