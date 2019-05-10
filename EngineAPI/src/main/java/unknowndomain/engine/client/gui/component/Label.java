package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.gui.misc.Background;
import unknowndomain.engine.client.gui.misc.Border;
import unknowndomain.engine.client.gui.misc.Insets;
import unknowndomain.engine.client.gui.misc.Pos;
import unknowndomain.engine.client.gui.rendering.ComponentRenderer;
import unknowndomain.engine.client.gui.rendering.LabelRenderer;
import unknowndomain.engine.client.gui.text.Font;
import unknowndomain.engine.client.gui.text.Text;
import unknowndomain.engine.util.Color;

public class Label extends Control  {
    private final MutableValue<String> text = new SimpleMutableObjectValue<>();
    private final MutableValue<Font> font = new SimpleMutableObjectValue<>(Font.getDefaultFont());
    private final MutableValue<Color> textColor = new SimpleMutableObjectValue<>(Color.WHITE);
    private final MutableValue<Pos> textAlignment = new SimpleMutableObjectValue<>(Pos.CENTER);

    private final MutableFloatValue labelWidth = new SimpleMutableFloatValue();
    private final MutableFloatValue labelHeight = new SimpleMutableFloatValue();
    private Text cachedText;

    public Label(){
        text().addChangeListener((ob, o, n) -> {
            rebuildText();
            requestParentLayout();
        });
        font().addChangeListener((ob, o, n) -> {
            rebuildText();
            requestParentLayout();
        });
        textColor().addChangeListener((ob, o, n) -> {
            rebuildText();
            requestParentLayout();
        });
        textAlignment().addChangeListener((observable, oldValue, newValue) -> {
            rebuildText();
            requestParentLayout();
        });
        labelWidth().addChangeListener((ob, o, n) -> {
            rebuildText();
            requestParentLayout();
        });
        labelHeight().addChangeListener((ob, o, n) -> {
            rebuildText();
            requestParentLayout();
        });
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return LabelRenderer.INSTANCE;
    }

    public final MutableValue<String> text() {
        return text;
    }

    public final MutableValue<Font> font() {
        return font;
    }

    public final MutableValue<Color> textColor() {
        return textColor;
    }

    public MutableValue<Pos> textAlignment() {
        return textAlignment;
    }

    public MutableFloatValue labelWidth() {
        return labelWidth;
    }

    public MutableFloatValue labelHeight() {
        return labelHeight;
    }
    @Override
    public float prefWidth() {
        return labelWidth().get() != 0 ? labelWidth().get() : cachedText.prefWidth() + padding().getValue().getLeft() + padding().getValue().getRight();
    }
    @Override
    public float prefHeight() {
        return labelHeight().get() != 0 ? labelHeight().get() : cachedText.prefHeight() + padding().getValue().getTop() + padding().getValue().getBottom();
    }


    public Text getCachedText() {
        return cachedText;
    }
    protected void rebuildText() {
        if (cachedText == null) {
            cachedText = new Text();
        }
        cachedText.text().setValue(text.getValue());
        cachedText.font().setValue(font.getValue());
        cachedText.color().setValue(textColor.getValue());
        cachedText.textAlignment().setValue(textAlignment.getValue());
        cachedText.relocate(padding().getValue().getLeft(), padding().getValue().getTop());
        cachedText.resize(this.prefWidth() - padding().getValue().getLeft() - padding().getValue().getRight(),
                this.prefHeight() - padding().getValue().getTop() - padding().getValue().getBottom());
    }
}
