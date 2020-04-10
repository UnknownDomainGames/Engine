package engine.gui.graphics;

import engine.gui.Node;
import engine.gui.Parent;

public class ParentRenderer<E extends Parent> implements NodeRenderer<E> {

    public static final ParentRenderer<?> INSTANCE = new ParentRenderer<>();

    @Override
    public void render(E parent, Graphics graphics) {
        for (Node child : parent.getUnmodifiableChildren()) {
            if (!child.isVisible()) continue;
            graphics.pushClipRect(child.getLayoutX(), child.getLayoutY(), child.getWidth(), child.getHeight());
            child.getRenderer().render(child, graphics);
            graphics.popClipRect();
        }
    }
}
