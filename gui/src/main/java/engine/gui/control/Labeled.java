package engine.gui.control;

import com.github.mouse0w0.observable.value.*;
import engine.graphics.font.Font;
import engine.gui.Node;
import engine.gui.misc.Pos;
import engine.gui.text.Text;
import engine.util.Color;

public class Labeled extends Control {

    private MutableObjectValue<Node> graphic;
    private MutableObjectValue<Pos> alignment;
    private Text text = new Text();

    public Labeled() {
        getChildren().add(text);
    }

    public final MutableObjectValue<Node> graphic() {
        if (graphic == null) {
            graphic = new SimpleMutableObjectValue<>();
            graphic.addChangeListener((observable, oldValue, newValue) -> {
                getChildren().remove(oldValue);
                getChildren().add(newValue);
            });
        }
        return graphic;
    }

    public final Node getGraphic() {
        return graphic == null ? null : graphic.get();
    }

    public final void setGraphic(Node graphic) {
        graphic().set(graphic);
    }

    public final MutableObjectValue<Pos> alignment() {
        if (alignment == null) {
            alignment = new NonNullMutableObjectValue<>(Pos.CENTER_LEFT);
            alignment.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        }
        return alignment;
    }

    public final Pos getAlignment() {
        return alignment == null ? Pos.CENTER_LEFT : alignment.get();
    }

    public final void setAlignment(Pos alignment) {
        alignment().set(alignment);
    }

    public final MutableStringValue text() {
        return text.text();
    }

    public final String getText() {
        return text.getText();
    }

    public final void setText(String text) {
        this.text.setText(text);
    }

    public final MutableObjectValue<Color> color() {
        return text.color();
    }

    public final Color getColor() {
        return text.getColor();
    }

    public final void setColor(Color color) {
        text.setColor(color);
    }

    public final MutableObjectValue<Font> font() {
        return text.font();
    }

    public final Font getFont() {
        return text.getFont();
    }

    public final void setFont(Font font) {
        text.setFont(font);
    }

    public final MutableObjectValue<Pos> textAlignment() {
        return text.textAlignment();
    }

    public final Pos getTextAlignment() {
        return text.getTextAlignment();
    }

    public final void setTextAlignment(Pos alignment) {
        text.setTextAlignment(alignment);
    }

    public final MutableDoubleValue leading() {
        return text.leading();
    }

    public final double getLeading() {
        return text.getLeading();
    }

    public final void setLeading(double leading) {
        text.setLeading(leading);
    }

    @Override
    public float computeWidth() {
        return text.prefWidth() + getPadding().getLeft() + getPadding().getRight();
    }

    @Override
    public float computeHeight() {
        return text.prefHeight() + getPadding().getTop() + getPadding().getBottom();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
    }
}
