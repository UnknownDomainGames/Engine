package engine.graphics.graph;

import engine.graphics.management.BindingProxy;

public interface DrawDispatcher {

    void draw(Frame frame, BindingProxy shader);
}
