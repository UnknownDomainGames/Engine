package engine.gui.text;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import engine.graphics.font.FontManager;
import engine.gui.graphics.ComponentRenderer;
import engine.gui.graphics.WrapTextRenderer;

public class WrapText extends Text {

    private final MutableFloatValue textWidth = new SimpleMutableFloatValue(-1);

    public WrapText() {
        super();
        textWidth.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
    }

    public WrapText(String text) {
        this();
        setText(text);
    }

    public final MutableFloatValue textWidth() {
        return textWidth;
    }

    public final float getTextWidth() {
        return textWidth.get();
    }

    public final void setTextWidth(float textWidth) {
        this.textWidth.set(textWidth);
    }

    @Override
    public float prefWidth() {
        return FontManager.instance().computeTextWidth(getText(), getFont(), textWidth.get());
    }

    @Override
    public float prefHeight() {
        return FontManager.instance().computeTextHeight(getText(), getFont(), textWidth.get(), (float) getLeading());
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return new WrapTextRenderer(this);
    }
}
