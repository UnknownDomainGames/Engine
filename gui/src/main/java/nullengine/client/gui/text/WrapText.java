package nullengine.client.gui.text;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import nullengine.client.gui.rendering.ComponentRenderer;
import nullengine.client.gui.rendering.WrapTextRenderer;
import nullengine.client.rendering.font.FontHelper;

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
        return WrapTextRenderer.INSTANCE;
    }
}
