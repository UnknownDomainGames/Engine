package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.event.MouseEvent;
import nullengine.client.gui.layout.BorderPane;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Border;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.rendering.CheckBoxRenderer;
import nullengine.client.gui.rendering.ComponentRenderer;
import nullengine.client.gui.text.Text;
import nullengine.util.Color;

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
    }

    public CheckBox(Boolean state) {
        this();
        this.state.setValue(state);
    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent event) {
        super.onClick(event);
        switchState();
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
            return text.toImmutable();
        }

        public ObservableValue<CheckBox> checkBox() {
            return checkBox.toImmutable();
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
