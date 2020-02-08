package engine.gui.text;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import engine.gui.rendering.ComponentRenderer;
import engine.gui.rendering.WrapTextRenderer;
import engine.graphics.font.FontHelper;

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
        var max = FontHelper.instance().computeTextWidth(text().getValue(), font().getValue(), textWidth.get());

        return max;
    }

    @Override
    public float prefHeight() {
        return FontHelper.instance().computeTextHeight(text().getValue(), font().getValue(), textWidth.get(), leading().getFloat());
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return new WrapTextRenderer(this);
    }
}
