package engine.gui.text;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import engine.graphics.font.FontManager;
import engine.gui.rendering.ComponentRenderer;
import engine.gui.rendering.WrapTextRenderer;

public class WrapText extends Text {

    private final MutableFloatValue textWidth = new SimpleMutableFloatValue(-1);

    public WrapText() {
        super();
        textWidth.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
    }

    public WrapText(String text) {
        this();
        this.text().setValue(text);
    }

    public MutableFloatValue textWidth() {
        return textWidth;
    }

    @Override
    public float prefWidth() {
        var max = FontManager.instance().computeTextWidth(text().getValue(), font().getValue(), textWidth.get());

        return max;
    }

    @Override
    public float prefHeight() {
        return FontManager.instance().computeTextHeight(text().getValue(), font().getValue(), textWidth.get(), leading().getFloat());
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return new WrapTextRenderer(this);
    }
}
