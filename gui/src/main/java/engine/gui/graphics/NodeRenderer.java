package engine.gui.graphics;

import engine.gui.Node;

public abstract class NodeRenderer<E extends Node> {

    public void doRender(E node, Graphics graphics) {
        final float x = node.getLayoutX(), y = node.getLayoutY();
        graphics.translate(x, y);
        render(node, graphics);
        graphics.translate(-x, -y);
    }

    public abstract void render(E node, Graphics graphics);
}
