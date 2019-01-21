package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.*;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.event.MouseEvent;
import unknowndomain.engine.client.gui.rendering.ButtonRenderer;
import unknowndomain.engine.client.gui.rendering.ComponentRenderer;
import unknowndomain.engine.client.gui.text.Font;
import unknowndomain.engine.client.gui.text.Text;
import unknowndomain.engine.util.Color;

import java.util.function.Consumer;

public class Button extends Component {

    private final MutableValue<String> text = new SimpleMutableObjectValue<>();
    private final MutableValue<Font> font = new SimpleMutableObjectValue<>(Font.getDefaultFont());
    private final MutableValue<Color> textColor = new SimpleMutableObjectValue<>(Color.WHITE);

    private final MutableFloatValue buttonWidth = new SimpleMutableFloatValue();
    private final MutableFloatValue buttonHeight = new SimpleMutableFloatValue();

    private final MutableValue<Vector4fc> padding = new SimpleMutableObjectValue<>(new Vector4f(5,5,5,5));

    //TODO: support image
    private final MutableValue<Color> background = new SimpleMutableObjectValue<>(Color.BLACK);
    private final MutableValue<Color> hoveredBg = new SimpleMutableObjectValue<>(Color.BLUE);
    private final MutableValue<Color> pressedBg = new SimpleMutableObjectValue<>(Color.fromRGB(0x507fff));
    private final MutableValue<Color> disableBg = new SimpleMutableObjectValue<>(Color.fromRGB(0x7f7f7f));

    private Text cachedText;

    public Button(){
        text.addChangeListener((ob,o,n)-> {
            rebuildText();
            requestParentLayout();
        });
        font.addChangeListener((ob,o,n)-> {
            rebuildText();
            requestParentLayout();
        });
        textColor.addChangeListener((ob, o, n)-> {
            rebuildText();
            requestParentLayout();
        });
        buttonWidth.addChangeListener((ob,o,n)->requestParentLayout());
        buttonHeight.addChangeListener((ob,o,n)->requestParentLayout());
        pressed.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        disabled.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        hover.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
    }

    public Button(String text){
        this();
        this.text.setValue(text);
    }

    private void rebuildText(){
        if(cachedText == null) {
            cachedText = new Text();
        }
        cachedText.text().setValue(text.getValue());
        cachedText.font().setValue(font.getValue());
        cachedText.color().setValue(textColor.getValue());
    }

    @Override
    public float prefWidth() {
        return buttonwidth().get() != 0 ? buttonwidth().get() : cachedText.prefWidth() + padding.getValue().x() + padding.getValue().z();
    }

    @Override
    public float prefHeight() {
        return buttonheight().get() != 0 ? buttonheight().get() : cachedText.prefHeight() + padding.getValue().y() + padding.getValue().w();
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return ButtonRenderer.INSTANCE;
    }

    public MutableValue<Color> textcolor() {
        return textColor;
    }

    public MutableValue<String> text() {
        return text;
    }

    public MutableValue<Font> font() {
        return font;
    }

    public MutableValue<Color> background() {
        return background;
    }

    public MutableValue<Color> hoverbackground() {
        return hoveredBg;
    }

    public MutableValue<Color> pressbackground() {
        return pressedBg;
    }

    public MutableValue<Color> disabledbackground() {
        return disableBg;
    }

    private Consumer<MouseEvent.MouseClickEvent> onClick;

    @Override
    public void onClick(MouseEvent.MouseClickEvent event) {
        if(onClick != null)
        onClick.accept(event);
    }

    public void setOnClick(Consumer<MouseEvent.MouseClickEvent> onClick) {
        this.onClick = onClick;
    }

    public Text getCachedText() {
        return cachedText;
    }

    public MutableFloatValue buttonwidth() {
        return buttonWidth;
    }

    public MutableFloatValue buttonheight() {
        return buttonHeight;
    }

    public MutableValue<Vector4fc> padding() {
        return padding;
    }
}
