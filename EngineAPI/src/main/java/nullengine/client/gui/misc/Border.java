package nullengine.client.gui.misc;

import nullengine.client.gui.Component;
import nullengine.client.gui.rendering.Graphics;
import nullengine.util.Color;

public class Border {

    private Insets insets;

    private Insets outsets;

    private final Color color;

    public Border(Color color) {
        this(color, 1.0f);
    }

    public Border(Color color, float width) {
        this(color, width, width, width, width);
    }

    public Border(Color color, float top, float bottom, float left, float right) {
        this(color, new Insets(top, right, bottom, left));
    }

    public Border(Color color, Insets insets) {
        this(color, insets, Insets.EMPTY);
    }

    public Border(Color color, Insets insets, Insets outsets) {
        this.color = color;
        this.insets = insets;
        this.outsets = outsets;
    }

    public void render(Component component, Graphics graphics) {
        var border = this;
        graphics.setColor(border.getColor());
        if (border.getInsets().getTop() != 0) {
            graphics.fillRect(0, 0, component.width().get(), border.getInsets().getTop());
        }
        if (border.getInsets().getBottom() != 0) {
            graphics.fillRect(0, component.height().get() - border.getInsets().getBottom(), component.width().get(), border.getInsets().getBottom());
        }
        if (border.getInsets().getLeft() != 0) {
            graphics.fillRect(0, 0, border.getInsets().getLeft(), component.height().get());
        }
        if (border.getInsets().getRight() != 0) {
            graphics.fillRect(component.width().get() - border.getInsets().getRight(), 0, border.getInsets().getRight(), component.height().get());
        }
    }

    public Color getColor() {
        return color;
    }

    public Insets getInsets() {
        return insets;
    }

    public Insets getOutsets() {
        return outsets;
    }
}
