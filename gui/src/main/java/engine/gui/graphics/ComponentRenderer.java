package engine.gui.graphics;

import engine.gui.Node;

public interface ComponentRenderer<E extends Node> {

    void render(E component, Graphics graphics);
}
