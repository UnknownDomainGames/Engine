package unknowndomain.engine.client.gui;

public class Screen {

    private int width;
    private int height;

    private final Container root;

    public Screen(Container root) {
        this.root = root;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Container getRoot() {
        return root;
    }
}
