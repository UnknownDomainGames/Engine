package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.*;
import unknowndomain.engine.client.gui.event.MouseEvent;
import unknowndomain.engine.client.gui.layout.BorderPane;
import unknowndomain.engine.client.gui.misc.Background;
import unknowndomain.engine.client.gui.misc.Border;
import unknowndomain.engine.client.gui.misc.Insets;
import unknowndomain.engine.client.gui.misc.Pos;
import unknowndomain.engine.client.gui.rendering.CheckBoxRenderer;
import unknowndomain.engine.client.gui.rendering.ComponentRenderer;
import unknowndomain.engine.client.gui.text.Text;
import unknowndomain.engine.util.Color;

public class CheckBox extends Button {
    private MutableValue<Boolean> state = new SimpleMutableObjectValue<>(false);
    private MutableBooleanValue allowIntermediate = new SimpleMutableBooleanValue(false);

    private MutableValue<Color> contentColor = new SimpleMutableObjectValue<>(Color.BLACK);

    public CheckBox(){
        background().setValue(Background.fromColor(Color.WHITE));
        border().setValue(new Border(Color.BLACK, 3));
        padding().setValue(new Insets(2));
        buttonwidth().set(24f);
        buttonheight().set(24f);
        text().setValue("");
    }
    public CheckBox(Boolean state){
        this();
        this.state.setValue(state);
    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent event) {
        super.onClick(event);
        switchState();
    }

    private void switchState(){
        if(allowIntermediate.get() && state.getValue() != null && !state.getValue()){
            state.setValue(null);
        }
        else if(state.getValue() == null){
            state.setValue(true);
        }
        else{
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

    public MutableValue<Boolean> state() {
        return state;
    }

    public MutableBooleanValue allowIntermediate() {
        return allowIntermediate;
    }

    public MutableValue<Color> contentColor() {
        return contentColor;
    }

    public static class Texted extends BorderPane {
        protected MutableValue<Text> text = new SimpleMutableObjectValue<>();
        protected MutableValue<CheckBox> checkBox = new SimpleMutableObjectValue<>();
        private MutableValue<Pos> contentPos = new SimpleMutableObjectValue<>(Pos.CENTER_RIGHT);

        public Texted(){
            this("");
        }

        public Texted(String str){
            text.setValue(new Text(str));
            checkBox.setValue(new CheckBox());
            setMargin(checkBox.getValue(), new Insets(3));
            center().setValue(checkBox.getValue());
            processTextPos();
            contentPos.addChangeListener((observable, oldValue, newValue) -> processTextPos());
        }

        public ObservableValue<Text> text(){
            return text.toImmutable();
        }

        public ObservableValue<CheckBox> checkBox(){
            return checkBox.toImmutable();
        }

        public MutableValue<Pos> contentPos() {
            return contentPos;
        }

        private void processTextPos(){
            Text text1 = text.getValue();
            Pos alignment = BorderPane.getAlignment(text1);
            if(alignment != null){
                switch (alignment){
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
            switch (contentPos.getValue()){
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
