package nullengine.client.gui.misc;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;

public final class Size {
    public static final int USE_PARENT_VALUE = -1;
    public static final int USE_COMPUTE_VALUE = -2;

    private final MutableFloatValue minWidth = new SimpleMutableFloatValue(0);
    private final MutableFloatValue minHeight = new SimpleMutableFloatValue(0);
    private final MutableFloatValue prefWidth = new SimpleMutableFloatValue(USE_COMPUTE_VALUE);
    private final MutableFloatValue prefHeight = new SimpleMutableFloatValue(USE_COMPUTE_VALUE);
    private final MutableFloatValue maxWidth = new SimpleMutableFloatValue(Float.MAX_VALUE);
    private final MutableFloatValue maxHeight = new SimpleMutableFloatValue(Float.MAX_VALUE);

    public MutableFloatValue minWidth() {
        return minWidth;
    }

    public MutableFloatValue minHeight() {
        return minHeight;
    }

    public void setMinSize(float width, float height) {
        minHeight.set(width);
        minHeight.set(height);
    }

    public MutableFloatValue prefWidth() {
        return prefWidth;
    }

    public MutableFloatValue prefHeight() {
        return prefHeight;
    }

    public void setPrefSize(float width, float height) {
        prefWidth.set(width);
        prefHeight.set(height);
    }

    public MutableFloatValue maxWidth() {
        return maxWidth;
    }

    public MutableFloatValue maxHeight() {
        return maxHeight;
    }

    public void setMaxSize(float width, float height) {
        maxWidth.set(width);
        maxHeight.set(height);
    }
}
