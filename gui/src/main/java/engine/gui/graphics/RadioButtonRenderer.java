package engine.gui.graphics;

import engine.gui.control.RadioButton;
import engine.gui.misc.Insets;

public class RadioButtonRenderer implements ComponentRenderer<RadioButton> {
    @Override
    public void render(RadioButton component, Graphics graphics) {
        graphics.drawBackground(component.getBackground(), component);
        graphics.drawBorder(component.getBorder(), component);
        if (component.selected().get()) {
            Insets insets = component.getPadding();
            float width = component.getWidth() - insets.getRight() - insets.getLeft();
            float height = component.getWidth() - insets.getTop() - insets.getBottom();
            graphics.pushClipRect(insets.getLeft(), insets.getTop(), width, height);
            graphics.setColor(component.contentColor().get());
            graphics.fillRect(0, 0, width, height);
            graphics.popClipRect();
        }
    }
}
