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
            float width = (float) (node.getWidth() - insets.getRight() - insets.getLeft());
            float height = (float) (node.getWidth() - insets.getTop() - insets.getBottom());
            graphics.setColor(node.contentColor().get());
            graphics.fillRect((float) insets.getLeft(), (float) insets.getTop(), width, height);
        }
    }
}
