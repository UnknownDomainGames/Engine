package engine.gui.shape;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.graphics.NodeRenderer;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Rect extends Shape {

    private final MutableObjectValue<Vector2fc> size = new SimpleMutableObjectValue<>(new Vector2f(0));

    public Rect() {
    }

    public Rect(Vector2f size) {
        this.size.set(size);
    }

    public MutableObjectValue<Vector2fc> rectSize() {
        return size;
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
    protected NodeRenderer createDefaultRenderer() {
        return (NodeRenderer<Rect>) (component, graphics) -> {
            float strokeWidth = getStrokeWidth();
            if (strokeWidth > 0) {
                graphics.popClipRect();
                graphics.pushClipRect(component.getLayoutX() - strokeWidth, component.getLayoutY() - strokeWidth, component.getWidth() + strokeWidth * 2, component.getHeight() + strokeWidth * 2);
                graphics.setColor(component.getStrokeColor());
                graphics.fillRect(0, 0, component.getWidth() + strokeWidth * 2, component.getHeight() + strokeWidth * 2);
            }
            graphics.setColor(component.getFillColor());
            graphics.fillRect(strokeWidth, strokeWidth, component.getWidth(), component.getHeight());
        };
    }
}
