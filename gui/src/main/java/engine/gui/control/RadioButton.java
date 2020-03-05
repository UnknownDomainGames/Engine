package engine.gui.control;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.ObservableValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.input.MouseActionEvent;
import engine.gui.layout.BorderPane;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.gui.misc.Insets;
import engine.gui.misc.Pos;
import engine.gui.rendering.ComponentRenderer;
import engine.gui.rendering.RadioButtonRenderer;
import engine.gui.text.Text;
import engine.util.Color;

public class RadioButton extends ToggleButton {

    private MutableObjectValue<Color> contentColor = new SimpleMutableObjectValue<>(Color.BLACK);

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
        getSize().setPrefSize(24, 24);
        addEventHandler(MouseActionEvent.MOUSE_CLICKED, this::onClicked);
    }

    public MutableObjectValue<Color> contentColor() {
        return contentColor;
    }

//    private void onClicked(MouseActionEvent event) {
//        //TODO check radio button group
//        selected().set(true);
//    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return new RadioButtonRenderer();
    }

    @Override
    protected void handleBackground() {

    }

    public static class Texted extends BorderPane {
        protected MutableObjectValue<Text> text = new SimpleMutableObjectValue<>();
        protected MutableObjectValue<RadioButton> radioButton = new SimpleMutableObjectValue<>();
        private MutableObjectValue<Pos> contentPos = new SimpleMutableObjectValue<>(Pos.CENTER_RIGHT);

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
            return text.toUnmodifiable();
        }

        public ObservableValue<RadioButton> radioButton() {
            return radioButton.toUnmodifiable();
        }

        public MutableObjectValue<Pos> contentPos() {
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
