package engine.graphics.graph;

public interface DrawDispatcher {

    void init(Drawer drawer);

    void draw(FrameContext frameContext, Drawer drawer, Renderer renderer);
}
