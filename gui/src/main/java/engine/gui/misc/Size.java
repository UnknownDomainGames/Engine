package engine.gui.misc;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;

public final class Size {
    public static final int USE_PARENT_VALUE = -1;
    public static final int USE_COMPUTE_VALUE = -2;

    private MutableFloatValue minWidth;
    private MutableFloatValue minHeight;
    private MutableFloatValue prefWidth;
    private MutableFloatValue prefHeight;
    private MutableFloatValue maxWidth;
    private MutableFloatValue maxHeight;

    public MutableFloatValue minWidth() {
        if (minWidth == null) {
            minWidth = new SimpleMutableFloatValue(0);
        }
        return minWidth;
    }

    public float getMinWidth() {
        return minWidth == null ? 0 : minWidth.get();
    }

    public MutableFloatValue minHeight() {
        if (minHeight == null) {
            minHeight = new SimpleMutableFloatValue(0);
        }
        return minHeight;
    }

    public float getMinHeight() {
        return minHeight == null ? 0 : minHeight.get();
    }

    public void setMinSize(float width, float height) {
        minHeight.set(width);
        minHeight.set(height);
    }

    public MutableFloatValue prefWidth() {
        if (prefWidth == null) {
            prefWidth = new SimpleMutableFloatValue(USE_COMPUTE_VALUE);
        }
        return prefWidth;
    }

    public float getPrefWidth() {
        return prefWidth == null ? USE_COMPUTE_VALUE : prefWidth.get();
    }

    public MutableFloatValue prefHeight() {
        if (prefHeight == null) {
            prefHeight = new SimpleMutableFloatValue(USE_COMPUTE_VALUE);
        }
        return prefHeight;
    }

    public float getPrefHeight() {
        return prefHeight == null ? USE_COMPUTE_VALUE : prefHeight.get();
    }

    public void setPrefSize(float width, float height) {
        prefWidth.set(width);
        prefHeight.set(height);
    }

    public MutableFloatValue maxWidth() {
        if (maxWidth == null) {
            maxWidth = new SimpleMutableFloatValue(Float.MAX_VALUE);
        }
        return maxWidth;
    }

    public float getMaxWidth() {
        return maxWidth == null ? Float.MAX_VALUE : maxWidth.get();
    }

    public MutableFloatValue maxHeight() {
        if (maxHeight == null) {
            maxHeight = new SimpleMutableFloatValue(Float.MAX_VALUE);
        }
        return maxHeight;
    }

    public float getMaxHeight() {
        return maxHeight == null ? Float.MAX_VALUE : maxHeight.get();
    }

    public void setMaxSize(float width, float height) {
        maxWidth().set(width);
        maxHeight().set(height);
    }
}
