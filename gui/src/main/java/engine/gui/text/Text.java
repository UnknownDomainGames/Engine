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

    private MutableObjectValue<Pos> textAlignment;
    private MutableDoubleValue leading;

    public Text() {
        text.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        font.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
    }

    public Text(String text) {
        this();
        setText(text);
    }

    public final MutableStringValue text() {
        return text;
    }

    public final String getText() {
        return text.get();
    }

    public final void setText(String text) {
        this.text.set(text);
    }

    public final MutableObjectValue<Color> color() {
        return color;
    }

    public final Color getColor() {
        return color.get();
    }

    public final void setColor(Color color) {
        this.color.set(color);
    }

    public final MutableObjectValue<Font> font() {
        return font;
    }

    public final Font getFont() {
        return font.get();
    }

    public final void setFont(Font font) {
        this.font.set(font);
    }

    public final MutableObjectValue<Pos> textAlignment() {
        if (textAlignment == null) {
            textAlignment = new SimpleMutableObjectValue<>(Pos.CENTER);
            textAlignment.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        }
        return textAlignment;
    }

    public final Pos getTextAlignment() {
        return textAlignment.get();
    }

    public final void setTextAlignment(Pos alignment) {
        textAlignment().set(alignment);
    }

    public final MutableDoubleValue leading() {
        if (leading == null) {
            leading = new SimpleMutableDoubleValue(1.0);
            leading.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        }
        return leading;
    }

    public final double getLeading() {
        return leading == null ? 1.0 : leading.get();
    }

    public final void setLeading(double leading) {
        leading().set(leading);
    }

    @Override
    public float prefWidth() {
        return FontManager.instance().computeTextWidth(getText(), getFont());
    }

    @Override
    public float prefHeight() {
        return FontManager.instance().computeTextHeight(getText(), getFont(), -1, (float) getLeading());
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return new TextRenderer(this);
    }
}
