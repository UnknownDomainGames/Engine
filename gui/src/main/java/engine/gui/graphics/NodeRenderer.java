package engine.gui.graphics;

import engine.gui.Node;

public interface NodeRenderer<E extends Node> {

    void render(E component, Graphics graphics);
}
