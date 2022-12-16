package engine.gui.graphics;

import engine.gui.Node;

public abstract class NodeRenderer<E extends Node> {

    public void doRender(E node, Graphics graphics) {
        float x = (float) node.getLayoutX(), y = (float) node.getLayoutY();
        graphics.translate(x, y);
        render(node, graphics);
        graphics.translate(-x, -y);
    }

    public abstract void render(E node, Graphics graphics);
}
