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
    private final MutableValue<TextAlignment> textAlignment = new SimpleMutableObjectValue<>(TextAlignment.LEFT);

    public Text() {
        text.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        font.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        textAlignment.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
    }

    public Text(String text) {
        this();
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

    public MutableValue<TextAlignment> textAlignment() {
        return textAlignment;
    }

    @Override
    public float prefWidth() {
        return (int) Internal.getContext().getFontHelper().computeTextWidth(text().getValue(), font().getValue());
    }

    @Override
    public float prefHeight() {
//        return font().getValue().getSize();
        return Internal.getContext().getFontHelper().computeTextHeight(text().getValue(), font().getValue());
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return TextRenderer.INSTANCE;
    }
}
