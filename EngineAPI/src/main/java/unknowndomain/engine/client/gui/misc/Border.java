package unknowndomain.engine.client.gui.misc;

import unknowndomain.engine.util.Color;

public class Border {

    private Insets insets;

    private Insets outsets;

    private final Color color;

    public Border(Color color){
        this(color, 1.0f);
    }

    public Border(Color color, float width){
        this(color, width,width,width,width);
    }

    public Border(Color color, float top,float bottom, float left, float right){
        this(color, new Insets(top, right, bottom, left));
    }

    public Border(Color color, Insets insets){
        this(color, insets, Insets.EMPTY);
    }

    public Border(Color color, Insets insets, Insets outsets){
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
