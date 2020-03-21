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
        setBackground(Background.fromColor(Color.WHITE));
        border().addChangeListener((observable, oldValue, newValue) -> {
            setOffColor(newValue);
            setOnColor(newValue);
        });
        setBorder(new Border(Color.BLACK, 3));
        setPadding(new Insets(5));
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
            text.set(new Text(str));
            radioButton.set(new RadioButton());
            setMargin(radioButton.get(), new Insets(3));
            center().set(radioButton.get());
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
            Text text1 = text.get();
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
                        top().set(null);
                        break;
                    case CENTER_LEFT:
                        left().set(null);
                        break;
                    case CENTER_RIGHT:
                        right().set(null);
                        break;
                    case BOTTOM_CENTER:
                        bottom().set(null);
                        break;
                }
            }
            switch (contentPos.get()) {
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
                    top().set(text1);
                    break;
                case CENTER_LEFT:
                    left().set(text1);
                    break;
                case CENTER_RIGHT:
                    right().set(text1);
                    break;
                case BOTTOM_CENTER:
                    bottom().set(text1);
                    break;
            }
        }
    }

}
