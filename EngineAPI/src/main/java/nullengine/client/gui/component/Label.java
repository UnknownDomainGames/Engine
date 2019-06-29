package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.MutableValue;
import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.text.Font;
import nullengine.client.gui.text.Text;
import nullengine.util.Color;

public class Label extends Control {

    private Text text = new Text();

    public Label() {
        this.getChildren().addAll(text);
    }

    public final MutableValue<Font> font() {
        return text.font();
    }

    public final MutableValue<String> text() {
        return text.text();
    }

    public final MutableValue<Color> textColor() {
        return text.color();
    }

    public MutableValue<Pos> textAlignment() {
        return text.textAlignment();
    }

    @Override
    public float prefWidth() {
        return width().get() != 0 ? width().get() : text.prefWidth() + padding().getValue().getLeft() + padding().getValue().getRight();
    }

    @Override
    public float prefHeight() {
        return height().get() != 0 ? height().get() : text.prefHeight() + padding().getValue().getTop() + padding().getValue().getBottom();
    }

}
