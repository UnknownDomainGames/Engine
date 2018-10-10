package unknowndomain.engine.client.gui;

import unknowndomain.engine.client.gui.renderer.ComponentRenderer;

public abstract class Component {

    public static final int USE_PREF_VALUE = -1;

    private Scene screen;
    private Container parent;

    private int x, y;
    private int width, height;

    private ComponentRenderer<?> renderer;

    public Container getParent() {
        return parent;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
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
