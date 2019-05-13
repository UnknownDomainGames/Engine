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

    private final MutableFloatValue labelWidth = new SimpleMutableFloatValue();
    private final MutableFloatValue labelHeight = new SimpleMutableFloatValue();

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

    public MutableFloatValue labelWidth() {
        return labelWidth;
    }

    public MutableFloatValue labelHeight() {
        return labelHeight;
    }

    @Override
    public float prefWidth() {
        return labelWidth().get() != 0 ? labelWidth().get() : text.prefWidth() + padding().getValue().getLeft() + padding().getValue().getRight();
    }

    @Override
    public float prefHeight() {
        return labelHeight().get() != 0 ? labelHeight().get() : text.prefHeight() + padding().getValue().getTop() + padding().getValue().getBottom();
    }

}
