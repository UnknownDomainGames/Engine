package engine.gui.graphics.control;

import engine.gui.control.RadioButton;
import engine.gui.graphics.Graphics;
import engine.gui.graphics.NodeRenderer;
import engine.gui.misc.Insets;

public class RadioButtonRenderer extends NodeRenderer<RadioButton> {
    @Override
    public void render(RadioButton node, Graphics graphics) {
        graphics.drawBackground(node.getBackground(), node);
        graphics.drawBorder(node.getBorder(), node);
        if (node.selected().get()) {
            Insets insets = node.getPadding();
            float width = node.getWidth() - insets.getRight() - insets.getLeft();
            float height = node.getWidth() - insets.getTop() - insets.getBottom();
            graphics.setColor(node.contentColor().get());
            graphics.fillRect(insets.getLeft(), insets.getTop(), width, height);
        }
    }
}
