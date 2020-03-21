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
        setBackground(Background.fromColor(Color.WHITE));
        setBorder(new Border(Color.BLACK, 3));
        setPadding(new Insets(2));
        setPrefSize(24, 24);
        text().set("");
        addEventHandler(MouseActionEvent.MOUSE_CLICKED, event -> switchState());
    }

    public CheckBox(Boolean state) {
        this();
        this.state.set(state);
    }

    private void switchState() {
        if (allowIntermediate.get() && state.get() != null && !state.get()) {
            state.set(null);
        } else if (state.get() == null) {
            state.set(true);
        } else {
            state.set(!state.get());
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
            text.set(new Text(str));
            checkBox.set(new CheckBox());
            setMargin(checkBox.get(), new Insets(3));
            center().set(checkBox.get());
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
