package nullengine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.event.MouseEvent;
import nullengine.client.gui.layout.BorderPane;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Border;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.rendering.ComponentRenderer;
import nullengine.client.gui.rendering.RadioButtonRenderer;
import nullengine.client.gui.text.Text;
import nullengine.util.Color;

public class RadioButton extends ToggleButton {

    private MutableValue<Color> contentColor = new SimpleMutableObjectValue<>(Color.BLACK);


    public RadioButton() {
        this(false);
    }

    public RadioButton(boolean selected) {
        super(selected);
        background().setValue(Background.fromColor(Color.WHITE));
        border().addChangeListener((observable, oldValue, newValue) -> {
            setOffColor(newValue);
            setOnColor(newValue);
        });
        border().setValue(new Border(Color.BLACK, 3));
        padding().setValue(new Insets(5));
        resize(24f, 24f);
    }

    public MutableValue<Color> contentColor() {
        return contentColor;
    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent event) {
        super.onClick(event);
        //TODO check radiobuttongroup
        selected().set(true);
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return new RadioButtonRenderer();
    }

    @Override
    protected void handleBackground() {

    }

    public static class Texted extends BorderPane {
        protected MutableValue<Text> text = new SimpleMutableObjectValue<>();
        protected MutableValue<RadioButton> radioButton = new SimpleMutableObjectValue<>();
        private MutableValue<Pos> contentPos = new SimpleMutableObjectValue<>(Pos.CENTER_RIGHT);

        public Texted() {
            this("");
        }

        public Texted(String str) {
            text.setValue(new Text(str));
            radioButton.setValue(new RadioButton());
            setMargin(radioButton.getValue(), new Insets(3));
            center().setValue(radioButton.getValue());
            processTextPos();
            contentPos.addChangeListener((observable, oldValue, newValue) -> processTextPos());
        }

        public ObservableValue<Text> text() {
            return text.toImmutable();
        }

        public ObservableValue<RadioButton> radioButton() {
            return radioButton.toImmutable();
        }

        public MutableValue<Pos> contentPos() {
            return contentPos;
        }

        private void processTextPos() {
            Text text1 = text.getValue();
            Pos alignment = BorderPane.getAlignment(text1);
            if (alignment != null) {
                switch (alignment) {
                    case TOP_LEFT:
                    case TOP_RIGHT:
                    case BASELINE_RIGHT:
                    case BASELINE_CENTER:
                    case BASELINE_LEFT:
                    case BOTTOM_RIGHT:
                    case BOTTOM_LEFT:
                    case CENTER:
                        break;
                    case TOP_CENTER:
                        top().setValue(null);
                        break;
                    case CENTER_LEFT:
                        left().setValue(null);
                        break;
                    case CENTER_RIGHT:
                        right().setValue(null);
                        break;
                    case BOTTOM_CENTER:
                        bottom().setValue(null);
                        break;
                }
            }
            switch (contentPos.getValue()) {
                case TOP_LEFT:
                case TOP_RIGHT:
                case BASELINE_RIGHT:
                case BASELINE_CENTER:
                case BASELINE_LEFT:
                case BOTTOM_RIGHT:
                case BOTTOM_LEFT:
                case CENTER:
                    break;
                case TOP_CENTER:
                    top().setValue(text1);
                    break;
                case CENTER_LEFT:
                    left().setValue(text1);
                    break;
                case CENTER_RIGHT:
                    right().setValue(text1);
                    break;
                case BOTTOM_CENTER:
                    bottom().setValue(text1);
                    break;
            }
        }
    }

}
