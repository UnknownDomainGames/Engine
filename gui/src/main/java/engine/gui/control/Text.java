package engine.gui.control;

import com.github.mouse0w0.observable.value.*;
import engine.graphics.font.Font;
import engine.graphics.font.FontManager;
import engine.gui.Node;
import engine.gui.graphics.NodeRenderer;
import engine.gui.graphics.control.TextRenderer;
import engine.gui.misc.Pos;
import engine.util.Color;

public class Text extends Node {

    private final MutableStringValue text = new SimpleMutableStringValue();

    private MutableObjectValue<Font> font;
    private MutableObjectValue<Color> color;
    private MutableObjectValue<Pos> textAlignment;
    private MutableFloatValue leading;
    private MutableFloatValue textWidth;

    public Text() {
        text().addChangeListener((observable, oldValue, newValue) -> {
            requestParentLayout();
            getRenderer().markDirty();
        });
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
        text().set(text);
    }

    public final MutableObjectValue<Color> color() {
        if (color == null) {
            color = new SimpleMutableObjectValue<>(Color.WHITE);
        }
        return color;
    }

    public final Color getColor() {
        return color == null ? Color.WHITE : color.get();
    }

    public final void setColor(Color color) {
        color().set(color);
    }

    public final MutableObjectValue<Font> font() {
        if (font == null) {
            font = new SimpleMutableObjectValue<>(Font.getDefaultFont());
            font.addChangeListener((observable, oldValue, newValue) -> {
                requestParentLayout();
                getRenderer().markDirty();
            });
        }
        return font;
    }

    public final Font getFont() {
        return font == null ? Font.getDefaultFont() : font.get();
    }

    public final void setFont(Font font) {
        font().set(font);
    }

    public final MutableObjectValue<Pos> textAlignment() {
        if (textAlignment == null) {
            textAlignment = new SimpleMutableObjectValue<>(Pos.CENTER);
            textAlignment.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        }
        return textAlignment;
    }

    public final Pos getTextAlignment() {
        return textAlignment == null ? Pos.CENTER : textAlignment.get();
    }

    public final void setTextAlignment(Pos alignment) {
        textAlignment().set(alignment);
    }

    public final MutableFloatValue leading() {
        if (leading == null) {
            leading = new SimpleMutableFloatValue(1f);
            leading.addChangeListener((observable, oldValue, newValue) -> {
                requestParentLayout();
                getRenderer().markDirty();
            });
        }
        return leading;
    }

    public final float getLeading() {
        return leading == null ? 1f : leading.get();
    }

    public final void setLeading(float leading) {
        leading().set(leading);
    }

    public final MutableFloatValue textWidth() {
        if (textWidth == null) {
            textWidth = new SimpleMutableFloatValue(-1);
            textWidth.addChangeListener((observable, oldValue, newValue) -> {
                requestParentLayout();
                getRenderer().markDirty();
            });
        }
        return textWidth;
    }

    public final float getTextWidth() {
        return textWidth == null ? -1 : textWidth.get();
    }

    public final void setTextWidth(float textWidth) {
        textWidth().set(textWidth);
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
    public TextRenderer getRenderer() {
        return (TextRenderer) super.getRenderer();
    }

    @Override
    protected NodeRenderer<Text> createDefaultRenderer() {
        return new TextRenderer(this);
    }
}
