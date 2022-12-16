package engine.gui.shape;

import com.github.mouse0w0.observable.value.MutableDoubleValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableDoubleValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.util.Color;

public abstract class Shape extends Node {

    private MutableObjectValue<Color> fillColor;

    private MutableObjectValue<Color> strokeColor;
    private MutableDoubleValue strokeWidth;

    public final MutableObjectValue<Color> fillColor() {
        if (fillColor == null) {
            fillColor = new SimpleMutableObjectValue<>(Color.TRANSPARENT);
        }
        return fillColor;
    }

    public final Color getFillColor() {
        return fillColor == null ? Color.TRANSPARENT : fillColor.get();
    }

    public final void setFillColor(Color fillColor) {
        fillColor().set(fillColor);
    }

    public final MutableObjectValue<Color> strokeColor() {
        if (strokeColor == null) {
            strokeColor = new SimpleMutableObjectValue<>(Color.TRANSPARENT);
        }
        return strokeColor;
    }

    public final Color getStrokeColor() {
        return strokeColor == null ? Color.TRANSPARENT : strokeColor.get();
    }

    public final void setStrokeColor(Color strokeColor) {
        strokeColor().set(strokeColor);
    }

    public final MutableDoubleValue strokeWidth() {
        if (strokeWidth == null) {
            strokeWidth = new SimpleMutableDoubleValue(1);
        }
        return strokeWidth;
    }

    public final double getStrokeWidth() {
        return strokeWidth == null ? 1 : strokeWidth.get();
    }

    public final void setStrokeWidth(double strokeWidth) {
        strokeWidth().set(strokeWidth);
    }
}
