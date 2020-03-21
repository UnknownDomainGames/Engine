package engine.gui.shape;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.rendering.ComponentRenderer;
import engine.util.Color;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Rect extends Shape {

    private final MutableObjectValue<Vector2fc> size = new SimpleMutableObjectValue<>(new Vector2f(0));
    private final MutableFloatValue strokeSize = new SimpleMutableFloatValue();
    private final MutableObjectValue<Color> strokeColor = new SimpleMutableObjectValue<>(Color.BLACK);
    private final MutableObjectValue<Color> fillColor = new SimpleMutableObjectValue<>(Color.WHITE);

    public Rect() {
    }

    public Rect(Vector2f size) {
        this.size.set(size);
    }

    public MutableObjectValue<Vector2fc> rectSize() {
        return size;
    }

    public MutableFloatValue strokeSize() {
        return strokeSize;
    }

    public MutableObjectValue<Color> fillColor() {
        return fillColor;
    }

    public MutableObjectValue<Color> strokeColor() {
        return strokeColor;
    }

    @Override
    public float prefWidth() {
        return size.get().x();
    }

    @Override
    public float prefHeight() {
        return size.get().y();
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return (ComponentRenderer<Rect>) (component, graphics) -> {
            if (strokeSize.get() > 0) {
                graphics.popClipRect();
                graphics.pushClipRect(component.getLayoutX() - component.strokeSize.get(), component.getLayoutY() - component.strokeSize.get(), component.getWidth() + component.strokeSize.get() * 2, component.getHeight() + component.strokeSize.get() * 2);
                graphics.setColor(component.strokeColor.get());
                graphics.fillRect(0, 0, component.getWidth() + component.strokeSize.get() * 2, component.getHeight() + component.strokeSize.get() * 2);
            }
            graphics.setColor(component.fillColor.get());
            graphics.fillRect(component.strokeSize.get(), component.strokeSize.get(), component.getWidth(), component.getHeight());
        };
    }
}
