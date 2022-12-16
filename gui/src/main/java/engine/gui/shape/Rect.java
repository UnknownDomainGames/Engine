package engine.gui.shape;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.graphics.NodeRenderer;
import engine.gui.graphics.shape.RectRenderer;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Rect extends Shape {

    private final MutableObjectValue<Vector2fc> size = new SimpleMutableObjectValue<>(new Vector2f(0));

    public Rect() {
    }

    public Rect(Vector2f size) {
        this.size.set(size);
    }

    public final MutableObjectValue<Vector2fc> rectSize() {
        return size;
    }

    @Override
    public double prefWidth() {
        return size.get().x();
    }

    @Override
    public double prefHeight() {
        return size.get().y();
    }

    @Override
    protected NodeRenderer createDefaultRenderer() {
        return RectRenderer.INSTANCE;
    }
}
