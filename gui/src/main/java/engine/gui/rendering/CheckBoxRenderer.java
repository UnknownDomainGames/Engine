package engine.gui.rendering;

import engine.gui.control.CheckBox;
import engine.gui.misc.Insets;
import org.joml.Vector2f;

public class CheckBoxRenderer implements ComponentRenderer<CheckBox> {
    @Override
    public void render(CheckBox component, Graphics graphics) {
        graphics.drawBackground(component.background().get(), component);
        graphics.drawBorder(component.border().get(), component);
        if (component.state().get() == null || component.state().get()) {
            Insets insets = component.padding().get();
            float width;
            float height;
            if (component.state().get() == null) {
                width = component.getWidth() - insets.getRight() - insets.getLeft();
                height = component.getWidth() - insets.getTop() - insets.getBottom();
                graphics.pushClipRect(insets.getLeft(), insets.getTop(), width, height);
                graphics.setColor(component.contentColor().get());
                graphics.fillRect(0, 0, width, height);
                graphics.popClipRect();
            } else {
                width = component.getWidth();
                height = component.getWidth();
                float vc = (float) Math.sqrt(Math.pow(component.border().get().getInsets().getLeft(), 2) / 2);
                graphics.fillQuad(new Vector2f(vc, 0), new Vector2f(width, height - vc), new Vector2f(width - vc, height), new Vector2f(0, vc));
                graphics.fillQuad(new Vector2f(0, height - vc), new Vector2f(width - vc, 0), new Vector2f(width, vc), new Vector2f(vc, height));
            }
        }
    }
}
