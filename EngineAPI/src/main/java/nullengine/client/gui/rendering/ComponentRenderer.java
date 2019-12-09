package nullengine.client.gui.rendering;

import nullengine.client.gui.Node;

public interface ComponentRenderer<E extends Node> {

    void render(E component, Graphics graphics);
}
