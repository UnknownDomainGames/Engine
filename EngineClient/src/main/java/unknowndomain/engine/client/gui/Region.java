package unknowndomain.engine.client.gui;

import unknowndomain.engine.client.gui.rendering.ComponentRenderer;
import unknowndomain.engine.client.gui.rendering.RegionRenderer;
import unknowndomain.engine.client.gui.util.Size;

public class Region extends Container {

    private final Size size = new Size();

    public final Size getSize() {
        return size;
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
