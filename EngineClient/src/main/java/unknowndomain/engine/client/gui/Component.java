package unknowndomain.engine.client.gui;

import unknowndomain.engine.client.gui.renderer.ComponentRenderer;

public abstract class Component {

    public static final int USE_PREF_VALUE = -1;

    private Scene screen;

    Container parent; //FIXME:

    int x, y;
    int width, height;

    private boolean visible = true;

    private ComponentRenderer<?> renderer;

    public final Container getParent() {
        return parent;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public int minWidth() {
        return 0;
    }

    public int minHeight() {
        return 0;
    }

    abstract public int prefWidth();

    abstract public int prefHeight();

    public int maxWidth() {
        return Integer.MAX_VALUE;
    }

    public int maxHeight() {
        return Integer.MAX_VALUE;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean contains(int x, int y) {
        return x >= getX() && x <= getWidth() && y >= getY() && y <= getHeight();
    }

    public ComponentRenderer<?> getRenderer() {
        if (renderer == null)
            renderer = createDefaultRenderer();
        return renderer;
    }

    protected abstract ComponentRenderer<?> createDefaultRenderer();
}
