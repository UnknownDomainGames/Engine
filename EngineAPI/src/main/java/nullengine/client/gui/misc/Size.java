package nullengine.client.gui.misc;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;

public final class Size {
    public static final int USE_PREF_VALUE = -1;
    public static final int USE_COMPUTE_VALUE = -2;

    private final MutableFloatValue minWidth = new SimpleMutableFloatValue(0);
    private final MutableFloatValue minHeight = new SimpleMutableFloatValue(0);
    private final MutableFloatValue prefWidth = new SimpleMutableFloatValue();
    private final MutableFloatValue prefHeight = new SimpleMutableFloatValue();
    private final MutableFloatValue maxWidth = new SimpleMutableFloatValue(Float.MAX_VALUE);
    private final MutableFloatValue maxHeight = new SimpleMutableFloatValue(Float.MAX_VALUE);

    public MutableFloatValue minWidth() {
        return minWidth;
    }

    public MutableFloatValue minHeight() {
        return minHeight;
    }

    public MutableFloatValue prefWidth() {
        return prefWidth;
    }

    public MutableFloatValue prefHeight() {
        return prefHeight;
    }

    public MutableFloatValue maxWidth() {
        return maxWidth;
    }

    public MutableFloatValue maxHeight() {
        return maxHeight;
    }
}
