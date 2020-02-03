package nullengine.client.gui.rendering;

import nullengine.client.gui.control.CheckBox;
import nullengine.client.gui.misc.Insets;
import org.joml.Vector2f;

public class CheckBoxRenderer implements ComponentRenderer<CheckBox> {
    @Override
    public void render(CheckBox component, Graphics graphics) {
        graphics.drawBackground(component.background().getValue(), component);
        graphics.drawBorder(component.border().getValue(), component);
        if (component.state().getValue() == null || component.state().getValue()) {
            Insets insets = component.padding().getValue();
            float width;
            float height;
            if (component.state().getValue() == null) {
                width = component.width().get() - insets.getRight() - insets.getLeft();
                height = component.width().get() - insets.getTop() - insets.getBottom();
                graphics.pushClipRect(insets.getLeft(), insets.getTop(), width, height);
                graphics.setColor(component.contentColor().getValue());
                graphics.fillRect(0, 0, width, height);
                graphics.popClipRect();
            } else {
                width = component.width().get();
                height = component.width().get();
                float vc = (float) Math.sqrt(Math.pow(component.border().getValue().getInsets().getLeft(), 2) / 2);
                graphics.fillQuad(new Vector2f(vc, 0), new Vector2f(width, height - vc), new Vector2f(width - vc, height), new Vector2f(0, vc));
                graphics.fillQuad(new Vector2f(0, height - vc), new Vector2f(width - vc, 0), new Vector2f(width, vc), new Vector2f(vc, height));
            }
        }
    }
}
