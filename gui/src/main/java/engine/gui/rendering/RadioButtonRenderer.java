package engine.gui.rendering;

import engine.gui.control.RadioButton;
import engine.gui.misc.Insets;

public class RadioButtonRenderer implements ComponentRenderer<RadioButton> {
    @Override
    public void render(RadioButton component, Graphics graphics) {
        graphics.drawBackground(component.background().getValue(), component);
        graphics.drawBorder(component.border().getValue(), component);
        if (component.selected().get()) {
            Insets insets = component.padding().getValue();
            float width = component.width().get() - insets.getRight() - insets.getLeft();
            float height = component.width().get() - insets.getTop() - insets.getBottom();
            graphics.pushClipRect(insets.getLeft(), insets.getTop(), width, height);
            graphics.setColor(component.contentColor().getValue());
            graphics.fillRect(0, 0, width, height);
            graphics.popClipRect();
        }
    }
}
