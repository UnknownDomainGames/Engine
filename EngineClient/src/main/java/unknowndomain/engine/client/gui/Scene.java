package unknowndomain.engine.client.gui;

public class Scene {

    private int width, height;

    private Container root;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Container getRoot(){
        return root;
    }

    public void setRoot(Container root){
        this.root = root;
    }

}
