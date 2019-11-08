package nullengine.client.gui.text;

import com.github.mouse0w0.observable.value.MutableDoubleValue;
import com.github.mouse0w0.observable.value.MutableValue;
import com.github.mouse0w0.observable.value.SimpleMutableDoubleValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.Component;
import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.rendering.ComponentRenderer;
import nullengine.client.gui.rendering.TextRenderer;
import nullengine.client.rendering.font.Font;
import nullengine.client.rendering.font.FontHelper;
import nullengine.util.Color;

public class Text extends Component {

    private final MutableValue<String> text = new SimpleMutableObjectValue<>();
    private final MutableValue<Font> font = new SimpleMutableObjectValue<>(Font.getDefaultFont());
    private final MutableValue<Color> color = new SimpleMutableObjectValue<>(Color.WHITE);
    private final MutableValue<Pos> textAlignment = new SimpleMutableObjectValue<>(Pos.CENTER);
    private final MutableDoubleValue leading = new SimpleMutableDoubleValue(1.0);

    public Text() {
        text.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        font.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        textAlignment.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        leading.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
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

    public MutableValue<Pos> textAlignment() {
        return textAlignment;
    }

    public MutableDoubleValue leading() {
        return leading;
    }

    @Override
    public float prefWidth() {
        return FontHelper.instance().computeTextWidth(text.getValue(), font().getValue());
    }

    @Override
    public float prefHeight() {
        return FontHelper.instance().computeTextHeight(text().getValue(), font().getValue(), -1, leading.getFloat());
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return TextRenderer.INSTANCE;
    }
}
