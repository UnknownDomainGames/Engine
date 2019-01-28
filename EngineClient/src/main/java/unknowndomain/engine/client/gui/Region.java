package unknowndomain.engine.client.gui;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.gui.misc.Background;
import unknowndomain.engine.client.gui.misc.Insets;
import unknowndomain.engine.client.gui.misc.Size;
import unknowndomain.engine.client.gui.rendering.ComponentRenderer;
import unknowndomain.engine.client.gui.rendering.RegionRenderer;

public class Region extends Container {

    private final Size size = new Size();

    public final Size getSize() {
        return size;
    }

    private final MutableValue<Background> background = new SimpleMutableObjectValue<>(Background.NOTHING);

    public final MutableValue<Background> background() {
        return background;
    }

    private final MutableValue<Insets> padding = new SimpleMutableObjectValue<>(Insets.EMPTY);

    public final MutableValue<Insets> padding() {
        return padding;
    }

    @Override
    public float minWidth() {
        return getSize().minWidth().get();
    }

    @Override
    public float minHeight() {
        return getSize().minHeight().get();
    }

    @Override
    public float prefWidth() {
        return getSize().prefWidth().get();
    }

    @Override
    public float prefHeight() {
        return getSize().prefHeight().get();
    }

    @Override
    public float maxWidth() {
        return getSize().maxWidth().get();
    }

    @Override
    public float maxHeight() {
        return getSize().maxHeight().get();
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return RegionRenderer.INSTANCE;
    }
}
