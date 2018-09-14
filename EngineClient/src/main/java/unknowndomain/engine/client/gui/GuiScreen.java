package unknowndomain.engine.client.gui;

import unknowndomain.engine.client.gui.renderer.ScreenRenderer;

public class GuiScreen {

    private int width;
    private int height;

    private Container root;

    private ScreenRenderer renderer;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setRoot(Container root){
        this.root = root;
    }

    public Container getRoot(){
        return root;
    }

    public ScreenRenderer getRenderer() {
        if(renderer == null)
            renderer = createDefaultRenderer();
        return renderer;
    }

    protected ScreenRenderer createDefaultRenderer() {
        return new ScreenRenderer(this);
    }
}
