package engine.gui.graphics.control;

import engine.gui.control.CheckBox;
import engine.gui.graphics.Graphics;
import engine.gui.graphics.NodeRenderer;
import engine.gui.misc.Insets;
import org.joml.Vector2f;

public class CheckBoxRenderer extends NodeRenderer<CheckBox> {
    @Override
    public void render(CheckBox node, Graphics graphics) {
        graphics.drawBackground(node.getBackground(), node);
        graphics.drawBorder(node.getBorder(), node);
        if (node.state().get() == null || node.state().get()) {
            Insets insets = node.getPadding();
            float width;
            float height;
            if (node.state().get() == null) {
                width = node.getWidth() - insets.getRight() - insets.getLeft();
                height = node.getWidth() - insets.getTop() - insets.getBottom();
                graphics.setColor(node.contentColor().get());
                graphics.fillRect(insets.getLeft(), insets.getTop(), width, height);
            } else {
                width = node.getWidth();
                height = node.getWidth();
                float vc = (float) Math.sqrt(Math.pow(node.getBorder().getInsets().getLeft(), 2) / 2);
                graphics.fillQuad(new Vector2f(vc, 0), new Vector2f(width, height - vc), new Vector2f(width - vc, height), new Vector2f(0, vc));
                graphics.fillQuad(new Vector2f(0, height - vc), new Vector2f(width - vc, 0), new Vector2f(width, vc), new Vector2f(vc, height));
            }
        }
    }
}
