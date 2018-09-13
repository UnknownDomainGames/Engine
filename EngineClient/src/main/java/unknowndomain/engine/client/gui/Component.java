package unknowndomain.engine.client.gui;

public abstract class Component {

    private GuiScreen screen;
    private Container parent;

    private int x;
    private int y;

    private int width;
    private int height;

    public Container getParent() {
        return parent;
    }

    void updateParent(Container container) {
        this.parent = parent;
    }

    void updateScreen(GuiScreen screen) {
        this.screen = screen;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public boolean contains(int x, int y) {
        return x >= getX() && x <= getWidth() && y >= getY() && y <= getHeight();
    }

    public void resize() {}
}
