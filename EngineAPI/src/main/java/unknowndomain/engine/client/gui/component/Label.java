package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import unknowndomain.engine.client.gui.misc.Pos;
import unknowndomain.engine.client.gui.text.Font;
import unknowndomain.engine.client.gui.text.Text;
import unknowndomain.engine.util.Color;

public class Label extends Control {

    private Text text = new Text();
    public Label(){
        this.getChildren().addAll(text);
    }

    public final MutableValue<Font> font() {
        return text.font();
    }
    public final MutableValue<String> text(){
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
