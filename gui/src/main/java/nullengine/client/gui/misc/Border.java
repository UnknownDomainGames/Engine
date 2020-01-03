package nullengine.client.gui.misc;

import nullengine.util.Color;

public class Border {

    public static final Border NO_BORDER = new Border(Color.BLACK, Insets.EMPTY, Insets.EMPTY);

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
