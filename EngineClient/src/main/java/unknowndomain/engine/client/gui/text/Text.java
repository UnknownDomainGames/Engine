package unknowndomain.engine.client.gui.text;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.internal.Internal;
import unknowndomain.engine.client.gui.rendering.ComponentRenderer;
import unknowndomain.engine.client.gui.rendering.TextRenderer;
import unknowndomain.engine.util.Color;

public class Text extends Component {

    private final MutableValue<String> text = new SimpleMutableObjectValue<>();
    private final MutableValue<Font> font = new SimpleMutableObjectValue<>(Font.getDefaultFont());
    private final MutableValue<Color> color = new SimpleMutableObjectValue<>(Color.WHITE);

    public Text() {
    }

    public Text(String text) {
        text().setValue(text);
    }

    public final MutableValue<String> text() {
        return text;
    }

    public final MutableValue<Font> font() {
        return font;
    }

    public MutableValue<Color> color() {
        return color;
    }

    @Override
    public float prefWidth() {
        return (int) Internal.getContext().getFontHelper().computeTextWidth(text().getValue(), font().getValue());
    }

    @Override
    public float prefHeight() {
        return font().getValue().getSize();
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return TextRenderer.INSTANCE;
    }
}
