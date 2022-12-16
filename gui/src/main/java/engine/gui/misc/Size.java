package engine.gui.misc;

import com.github.mouse0w0.observable.value.MutableDoubleValue;
import com.github.mouse0w0.observable.value.SimpleMutableDoubleValue;

public final class Size {
    public static final double USE_COMPUTED_VALUE = -1;
    public static final double USE_PERF_VALUE = -2;

    private MutableDoubleValue minWidth;
    private MutableDoubleValue minHeight;
    private MutableDoubleValue prefWidth;
    private MutableDoubleValue prefHeight;
    private MutableDoubleValue maxWidth;
    private MutableDoubleValue maxHeight;

    public MutableDoubleValue minWidth() {
        if (minWidth == null) {
            minWidth = new SimpleMutableDoubleValue(USE_COMPUTED_VALUE);
        }
        return minWidth;
    }

    public double getMinWidth() {
        return minWidth == null ? USE_COMPUTED_VALUE : minWidth.get();
    }

    public void setMinWidth(double minWidth) {
        minWidth().set(minWidth);
    }

    public MutableDoubleValue minHeight() {
        if (minHeight == null) {
            minHeight = new SimpleMutableDoubleValue(USE_COMPUTED_VALUE);
        }
        return minHeight;
    }

    public double getMinHeight() {
        return minHeight == null ? USE_COMPUTED_VALUE : minHeight.get();
    }

    public void setMinHeight(double minHeight) {
        minHeight().set(minHeight);
    }

    public void setMinSize(double width, double height) {
        setMinWidth(width);
        setMinHeight(height);
    }

    public MutableDoubleValue prefWidth() {
        if (prefWidth == null) {
            prefWidth = new SimpleMutableDoubleValue(USE_COMPUTED_VALUE);
        }
        return prefWidth;
    }

    public double getPrefWidth() {
        return prefWidth == null ? USE_COMPUTED_VALUE : prefWidth.get();
    }

    public void setPrefWidth(double prefWidth) {
        prefWidth().set(prefWidth);
    }

    public MutableDoubleValue prefHeight() {
        if (prefHeight == null) {
            prefHeight = new SimpleMutableDoubleValue(USE_COMPUTED_VALUE);
        }
        return prefHeight;
    }

    public double getPrefHeight() {
        return prefHeight == null ? USE_COMPUTED_VALUE : prefHeight.get();
    }

    public void setPrefHeight(double prefHeight) {
        prefHeight().set(prefHeight);
    }

    public void setPrefSize(double width, double height) {
        setPrefWidth(width);
        setPrefHeight(height);
    }

    public MutableDoubleValue maxWidth() {
        if (maxWidth == null) {
            maxWidth = new SimpleMutableDoubleValue(USE_COMPUTED_VALUE);
        }
        return maxWidth;
    }

    public double getMaxWidth() {
        return maxWidth == null ? USE_COMPUTED_VALUE : maxWidth.get();
    }

    public void setMaxWidth(double maxWidth) {
        maxWidth().set(maxWidth);
    }

    public MutableDoubleValue maxHeight() {
        if (maxHeight == null) {
            maxHeight = new SimpleMutableDoubleValue(USE_COMPUTED_VALUE);
        }
        return maxHeight;
    }

    public double getMaxHeight() {
        return maxHeight == null ? USE_COMPUTED_VALUE : maxHeight.get();
    }

    public void setMaxHeight(double maxHeight) {
        maxHeight().set(maxHeight);
    }

    public void setMaxSize(double width, double height) {
        setMaxWidth(width);
        setMaxHeight(height);
    }
}
