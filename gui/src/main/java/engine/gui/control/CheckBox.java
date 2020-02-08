package engine.gui.control;

import com.github.mouse0w0.observable.value.*;
import engine.gui.input.MouseActionEvent;
import engine.gui.layout.BorderPane;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.gui.misc.Insets;
import engine.gui.misc.Pos;
import engine.gui.rendering.CheckBoxRenderer;
import engine.gui.rendering.ComponentRenderer;
import engine.gui.text.Text;
import engine.util.Color;

public class CheckBox extends Button {
    private MutableObjectValue<Boolean> state = new SimpleMutableObjectValue<>(false);
    private MutableBooleanValue allowIntermediate = new SimpleMutableBooleanValue(false);

    private MutableObjectValue<Color> contentColor = new SimpleMutableObjectValue<>(Color.BLACK);

    public CheckBox() {
        background().setValue(Background.fromColor(Color.WHITE));
        border().setValue(new Border(Color.BLACK, 3));
        padding().setValue(new Insets(2));
        resize(24f, 24f);
        text().setValue("");
        addEventHandler(MouseActionEvent.MOUSE_CLICKED, event -> switchState());
    }

    public CheckBox(Boolean state) {
        this();
        this.state.setValue(state);
    }

    private void switchState() {
        if (allowIntermediate.get() && state.getValue() != null && !state.getValue()) {
            state.setValue(null);
        } else if (state.getValue() == null) {
            state.setValue(true);
        } else {
            state.setValue(!state.getValue());
        }
    }

    @Override
    protected void handleBackground() {

    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return new CheckBoxRenderer();
    }

    public MutableObjectValue<Boolean> state() {
        return state;
    }

    public MutableBooleanValue allowIntermediate() {
        return allowIntermediate;
    }

    public MutableObjectValue<Color> contentColor() {
        return contentColor;
    }

    public static class Texted extends BorderPane {
        protected MutableObjectValue<Text> text = new SimpleMutableObjectValue<>();
        protected MutableObjectValue<CheckBox> checkBox = new SimpleMutableObjectValue<>();
        private MutableObjectValue<Pos> contentPos = new SimpleMutableObjectValue<>(Pos.CENTER_RIGHT);

        public Texted() {
            this("");
        }

        public Texted(String str) {
            text.setValue(new Text(str));
            checkBox.setValue(new CheckBox());
            setMargin(checkBox.getValue(), new Insets(3));
            center().setValue(checkBox.getValue());
            processTextPos();
            contentPos.addChangeListener((observable, oldValue, newValue) -> processTextPos());
        }

        public ObservableValue<Text> text() {
            return text.toUnmodifiable();
        }

        public ObservableValue<CheckBox> checkBox() {
            return checkBox.toUnmodifiable();
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
