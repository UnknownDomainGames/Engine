package nullengine.client.gui.shape;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.rendering.ComponentRenderer;
import nullengine.util.Color;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Rect extends Shape {

    private final MutableObjectValue<Vector2fc> size = new SimpleMutableObjectValue<>(new Vector2f(0));
    private final MutableFloatValue strokeSize = new SimpleMutableFloatValue();
    private final MutableObjectValue<Color> strokeColor = new SimpleMutableObjectValue<>(Color.BLACK);
    private final MutableObjectValue<Color> fillColor = new SimpleMutableObjectValue<>(Color.WHITE);

    public Rect(){}

    public Rect(Vector2f size){
        this.size.setValue(size);
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
        return size.getValue().x();
    }

    @Override
    public float prefHeight() {
        return size.getValue().y();
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return (ComponentRenderer<Rect>) (component, graphics) -> {
            if(strokeSize.get() > 0) {
                graphics.popClipRect();
                graphics.pushClipRect(component.x().get() - component.strokeSize.get(), component.y().get() - component.strokeSize.get(), component.width().get() + component.strokeSize.get() * 2, component.height().get() + component.strokeSize.get() * 2);
                graphics.setColor(component.strokeColor.getValue());
                graphics.fillRect(0, 0, component.width().get() + component.strokeSize.get() * 2, component.height().get() + component.strokeSize.get() * 2);
            }
            graphics.setColor(component.fillColor.getValue());
            graphics.fillRect(component.strokeSize.get(), component.strokeSize.get(), component.width().get(), component.height().get());
        };
    }
}
