package engine.graphics.graph;

public interface DrawDispatcher {

    void init(Drawer drawer);

    void draw(Frame frame, Drawer drawer, Renderer renderer);
}
