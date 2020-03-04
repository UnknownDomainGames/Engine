package engine.gui.text;

import com.github.mouse0w0.observable.value.*;
import engine.graphics.font.Font;
import engine.graphics.font.FontManager;
import engine.gui.Node;
import engine.gui.misc.Pos;
import engine.gui.rendering.ComponentRenderer;
import engine.gui.rendering.TextRenderer;
import engine.util.Color;

public class Text extends Node {

    private final MutableStringValue text = new SimpleMutableStringValue();
    private final MutableObjectValue<Font> font = new SimpleMutableObjectValue<>(Font.getDefaultFont());
    private final MutableObjectValue<Color> color = new SimpleMutableObjectValue<>(Color.WHITE);
    private final MutableObjectValue<Pos> textAlignment = new SimpleMutableObjectValue<>(Pos.CENTER);
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

    public final MutableStringValue text() {
        return text;
    }

    public final MutableObjectValue<Font> font() {
        return font;
    }

    public MutableObjectValue<Color> color() {
        return color;
    }

    public MutableObjectValue<Pos> textAlignment() {
        return textAlignment;
    }

    public MutableDoubleValue leading() {
        return leading;
    }

    @Override
    public float prefWidth() {
        return FontManager.instance().computeTextWidth(text.getValue(), font().getValue());
    }

    @Override
    public float prefHeight() {
        return FontManager.instance().computeTextHeight(text().getValue(), font().getValue(), -1, leading.getFloat());
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return new TextRenderer(this);
    }
}
