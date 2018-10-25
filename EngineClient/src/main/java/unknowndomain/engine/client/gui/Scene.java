package unknowndomain.engine.client.gui;

import java.util.Objects;

public class Scene {

    private int width, height;

    private Container root;

    public Scene(Container root) {
        this.root = Objects.requireNonNull(root);
    }

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
        this.root = Objects.requireNonNull(root);
    }

}
