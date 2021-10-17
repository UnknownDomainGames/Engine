package engine.gui.control;

import com.github.mouse0w0.observable.value.*;
import engine.graphics.font.Font;
import engine.gui.Node;
import engine.gui.misc.Pos;
import engine.util.Color;

public class Labeled extends Control {

    private MutableObjectValue<Node> graphic;
    private MutableObjectValue<Pos> alignment;
    private final Text text = new Text();

    public Labeled() {
        getChildren().add(text);
    }

    public final MutableObjectValue<Node> graphic() {
        if (graphic == null) {
            graphic = new SimpleMutableObjectValue<>();
            graphic.addChangeListener((observable, oldValue, newValue) -> {
                if (oldValue != null) getChildren().remove(oldValue);
                if (newValue != null) getChildren().add(newValue);
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

    public final void setFontSize(float fontSize) {
        text.setFontSize(fontSize);
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

    public final MutableFloatValue leading() {
        return text.leading();
    }

    public final double getLeading() {
        return text.getLeading();
    }

    public final void setLeading(float leading) {
        text.setLeading(leading);
    }

    @Override
    public float computeWidth() {
        if (graphic != null && graphic.isPresent() && text.getText() == null) {
            return graphic.get().prefWidth() + getPadding().getLeft() + getPadding().getRight();
        } else {
            return text.prefWidth() + getPadding().getLeft() + getPadding().getRight();
        }
    }

    @Override
    public float computeHeight() {
        if (graphic != null && graphic.isPresent() && text.getText() == null) {
            return graphic.get().prefHeight() + getPadding().getTop() + getPadding().getBottom();
        } else {
            return text.prefHeight() + getPadding().getTop() + getPadding().getBottom();
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
    }
}
